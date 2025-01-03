package dsm.webauthn.webauthntest.domain.webauthn.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.*;
import com.webauthn4j.data.attestation.AttestationObject;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.client.CollectedClientData;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.util.Base64UrlUtil;
import com.webauthn4j.verifier.exception.VerificationException;
import dsm.webauthn.webauthntest.domain.user.domain.entity.User;
import dsm.webauthn.webauthntest.domain.user.infrastructure.UserRepository;
import dsm.webauthn.webauthntest.domain.webauthn.application.dto.AllowCredential;
import dsm.webauthn.webauthntest.domain.webauthn.domain.entity.Credential;
import dsm.webauthn.webauthntest.domain.webauthn.domain.entity.PubKeyCredParam;
import dsm.webauthn.webauthntest.domain.webauthn.exception.WebAuthnException;
import dsm.webauthn.webauthntest.domain.webauthn.infrastructure.CredentialRepository;
import dsm.webauthn.webauthntest.domain.webauthn.infrastructure.PubKeyCredParamRepository;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.AuthInfoResponse;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.RegisterInfoResponse;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.RegisterVerificationResponse;
import dsm.webauthn.webauthntest.domain.webauthn.util.ChallengeUtil;
import dsm.webauthn.webauthntest.global.common.redis.RedisService;
import dsm.webauthn.webauthntest.global.response.properties.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static dsm.webauthn.webauthntest.global.common.redis.RedisService.NOT_EXIST;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WebAuthnService {

    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PubKeyCredParamRepository pubKeyCredParamRepository;
    private final WebAuthnManager webAuthnManager;
    private final ObjectMapper objectMapper;
    private final CredentialRepository credentialRepository;

    @Value( "${webauthn.rp}")
    private String rp;
    @Value( "${webauthn.authenticatorSelection.authenticatorAttachment}")
    private String authenticatorAttachment;
    @Value("${webauthn.authenticatorSelection.requireResidentKey}")
    private boolean requireResidentKey;
    @Value("${webauthn.authenticatorSelection.userVerification}")
    private String userVerification;
    @Value("${webauthn.origin}")
    private String originStr;
    @Value("${webauthn.authenticatorSelection.userVerificationRequired}")
    private boolean userVerificationRequired;
    @Value("${webauthn.authenticatorSelection.userPresenceRequired}")
    private boolean userPresenceRequired;
    @Value("${webauthn.pub-key-cred-param.type}")
    private String pubKeyCredParamType;

    private final static String CHALLENGE_PREFIX = "challenge_";
    private final static Duration CHALLENGE_DURATION = Duration.ofMinutes(3);

    public RegisterInfoResponse getRegistrationInfo(Long userId) {
        User user = findUserById(userId);
        // challenge 생성
        String challenge = generateAndEncodeChallenge(userId);
        List<PubKeyCredParam> pubKeyCredParams = pubKeyCredParamRepository.findAll(); //  ECDSA (ES256), RSA (RS256)

        return RegisterInfoResponse.builder()
                .challenge(challenge)
                .rp(rp) // 서비스 제공자
                .user(user)
                .pubKeyCredParams(pubKeyCredParams)
                .authenticatorAttachment(authenticatorAttachment) // 인증기의 종류 지정
                .requireResidentKey(requireResidentKey) // 인증기 내에 키를 저장할지 (서버에 공개키 저장)
                .userVerification(userVerification) // 사용자 검증 요구 (추가 인증)
                .build();
    }

    public RegisterVerificationResponse register(Long userId, String request) {
        // 인증기에 저장된 데이터
        RegistrationData registrationData = parseRequestToRegistrationData(request);
        // 서버에 저장된 데이터
        RegistrationParameters registrationParameters = getRegistrationParameters(userId);
        verifyRegistration(registrationData, registrationParameters);
        saveCredential(userId, registrationData, request);

        return RegisterVerificationResponse.builder()
                .userId(userId)
                .build();
    }

    public AuthInfoResponse getAuthenticationInfo(Long userId) {
        String challenge = generateAndEncodeChallenge(userId);
        List<AllowCredential> allowCredentials = generateAllowCredentials(userId);

        return AuthInfoResponse.builder()
                .challenge(challenge)
                .allowCredentials(allowCredentials)
                .userVerification(userVerification)
                .build();
    }

    public RegisterVerificationResponse authenticate(Long userId, String request) {
        AuthenticationData authenticationData = parseAuthenticationData(request);
        AuthenticationParameters authenticationParameters = getAuthenticationParameters(userId);

        verifyAuthentication(authenticationData, authenticationParameters);

        return RegisterVerificationResponse.builder()
                .userId(userId)
                .build();
// please update the counter of the authenticator record
//        updateCounter(authenticationData.getCredentialId(), authenticationData.getAuthenticatorData().getSignCount());
    }

    private AuthenticationParameters getAuthenticationParameters(Long userId) {
        ServerProperty serverProperty = getServerProperty(userId);
// expectations
        // user의 유효한 credential Id
        List<byte[]> allowCredentials = credentialRepository.findByUserId(userId)
                .stream()
                .map(credential -> Base64.getUrlDecoder().decode(credential.getCredentialId())).toList();
        // credential 정보(공개 키, 등록 정보)
        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new WebAuthnException(ErrorCode.CREDENTIAL_NOT_FOUND));
        RegistrationData registrationData = parseRequestToRegistrationData(credential.getRegistrationDataJSON());
        CredentialRecord credentialRecord =
                new CredentialRecordImpl( // You may create your own CredentialRecord implementation to save friendly authenticator name
                        registrationData.getAttestationObject(),
                        registrationData.getCollectedClientData(),
                        registrationData.getClientExtensions(),
                        registrationData.getTransports()
                );
// AuthenticationParameters 설정
        AuthenticationParameters authenticationParameters =
                new AuthenticationParameters(
                        serverProperty,
                        credentialRecord,
                        allowCredentials,
                        userVerificationRequired,
                        userPresenceRequired
                );
        return authenticationParameters;
    }

    private AuthenticationData parseAuthenticationData(String request) {
        AuthenticationData authenticationData;
        try {
            authenticationData = webAuthnManager.parseAuthenticationResponseJSON(request);
        } catch (DataConversionException e) {
            // If you would like to handle WebAuthn data structure parse error, please catch DataConversionException
            throw new WebAuthnException(ErrorCode.PARSING_ERROR);
        }
        return authenticationData;
    }

    private void verifyAuthentication(AuthenticationData authenticationData, AuthenticationParameters authenticationParameters) {
        try {
            webAuthnManager.verify(authenticationData, authenticationParameters);
        } catch (VerificationException e) {
            // If you would like to handle WebAuthn data validation error, please catch ValidationException
            throw new WebAuthnException(ErrorCode.VERIFICATION_ERROR);
        }
    }

    private List<AllowCredential> generateAllowCredentials(Long userId) {
        return credentialRepository.findByUserId(userId)
                .map(credential -> {
                    return AllowCredential.builder()
                            .type(pubKeyCredParamType)
                            .id(credential.getCredentialId())
                            .build();
                }).stream().toList();
    }

    private void saveCredential(Long userId, RegistrationData registrationData, String request) {
        String publicKeyJson;
        try {
            publicKeyJson = objectMapper.writeValueAsString(registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData().getCOSEKey());
        } catch (JsonProcessingException e) {
            throw new WebAuthnException(ErrorCode.PARSING_ERROR);
        }

        credentialRepository.findByUserId(userId)
                .ifPresent(credentialRepository::delete);

        CollectedClientData clientData = registrationData.getCollectedClientData();
        String clientDataJSON = null;
        try {
            clientDataJSON = objectMapper.writeValueAsString(clientData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        AttestationObject attestationObject = registrationData.getAttestationObject();
        String attestationObjectJSON = null;
        try {
            attestationObjectJSON = objectMapper.writeValueAsString(attestationObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Credential credential = Credential.builder()
                .publicKey(publicKeyJson)
                .userId(userId)
                .credentialId(com.webauthn4j.util.Base64UrlUtil.encodeToString(registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData().getCredentialId()))
                .registrationDataJSON(request)
                .build();
        credentialRepository.save(credential);
    }

    private void verifyRegistration(RegistrationData registrationData, RegistrationParameters registrationParameters) {
        try {
            webAuthnManager.verify(registrationData, registrationParameters);
        } catch (VerificationException e) {
            throw new WebAuthnException(ErrorCode.VERIFICATION_ERROR);
        }
    }

    private RegistrationParameters getRegistrationParameters(Long userId) {
        ServerProperty serverProperty = getServerProperty(userId);

// expectations
        List<PublicKeyCredentialParameters> pubKeyCredParams = getPubKeyCredParams();
        RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty, pubKeyCredParams, userVerificationRequired, userPresenceRequired);
        return registrationParameters;
    }

    private List<PublicKeyCredentialParameters> getPubKeyCredParams() {
        List<PubKeyCredParam> pubKeyCredParams = pubKeyCredParamRepository.findAll();

        // PubKeyCredParam -> PublicKeyCredentialParameters 변환
        return pubKeyCredParams.stream()
                .map(param -> new PublicKeyCredentialParameters(
                        PublicKeyCredentialType.create(param.getType()),
                        COSEAlgorithmIdentifier.create(param.getAlg().longValue())
                ))
                .toList();
    }

    private ServerProperty getServerProperty(Long userId) {
        Origin origin = Origin.create(originStr) /* set origin */;
        String rpId = rp /* set rpId */;
        String challengeStr = redisService.getStrValue(CHALLENGE_PREFIX + userId);
        Challenge challenge = new DefaultChallenge(challengeStr); /* set challenge */
        ;
        byte[] tokenBindingId = null /* set tokenBindingId */;
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, tokenBindingId);
        return serverProperty;
    }

    private RegistrationData parseRequestToRegistrationData(String request) {
        RegistrationData registrationData;
        try {
            registrationData = webAuthnManager.parseRegistrationResponseJSON(request);
        } catch (DataConversionException e) {
            // If you would like to handle WebAuthn data structure parse error, please catch DataConversionException
            throw new WebAuthnException(ErrorCode.PARSING_ERROR);
        }
        return registrationData;
    }

    private String generateAndEncodeChallenge(Long userId) {
        String savedChallenge = redisService.getStrValue(CHALLENGE_PREFIX + userId);
        if(savedChallenge.equals(NOT_EXIST)) {
            byte[] challenge = ChallengeUtil.generateChallenge();
            String encodedChallenge = Base64UrlUtil.encodeToString(challenge);
            // Redis에 Chellenge 값 저장
            redisService.setValue(CHALLENGE_PREFIX + userId, encodedChallenge, CHALLENGE_DURATION);
            return encodedChallenge;
        }
        return savedChallenge;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new WebAuthnException(ErrorCode.USER_NOT_FOUND));
    }
}
