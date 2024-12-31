package dsm.webauthn.webauthntest.domain.webauthn.application.service;

import dsm.webauthn.webauthntest.domain.user.domain.entity.User;
import dsm.webauthn.webauthntest.domain.user.infrastructure.UserRepository;
import dsm.webauthn.webauthntest.domain.webauthn.domain.entity.PubKeyCredParam;
import dsm.webauthn.webauthntest.domain.webauthn.exception.WebAuthnException;
import dsm.webauthn.webauthntest.domain.webauthn.infrastructure.PubKeyCredParamRepository;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.RegisterResponse;
import dsm.webauthn.webauthntest.domain.webauthn.util.ChallengeUtil;
import dsm.webauthn.webauthntest.global.common.redis.RedisService;
import dsm.webauthn.webauthntest.global.response.properties.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WebAuthnService {

    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PubKeyCredParamRepository pubKeyCredParamRepository;

    @Value( "${webauthn.rp}")
    private String rp;

    private final static String CHALLENGE_PREFIX = "challenge_";
    private final static Duration CHALLENGE_DURATION = Duration.ofMinutes(3);

    public RegisterResponse init(Long userId) {
        User user = findUserById(userId);
        String challenge = generateAndEncodeChallenge(userId);
        List<PubKeyCredParam> pubKeyCredParams = pubKeyCredParamRepository.findAll();

        return RegisterResponse.builder()
                .challenge(challenge)
                .rp(rp)
                .user(user)
                .pubKeyCredParams(pubKeyCredParams)
                .build();
    }

    private String generateAndEncodeChallenge(Long userId) {
        byte[] challenge = ChallengeUtil.generateChallenge();
        String encodedChallenge = ChallengeUtil.toBase64Url(challenge);
        redisService.setValue(CHALLENGE_PREFIX + userId, encodedChallenge, CHALLENGE_DURATION);
        return encodedChallenge;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new WebAuthnException(ErrorCode.USER_NOT_FOUND));
    }
}
