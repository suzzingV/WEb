package dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.req;

import lombok.Getter;

@Getter
public class RegisterVerificationRequest {

    private String credentialId;

    private String credentialRawId;

    private String credentialType;

    private String clientDataJSON;

    private String attestationObject;
}
