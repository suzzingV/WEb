package dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res;

import dsm.webauthn.webauthntest.domain.user.domain.entity.User;
import dsm.webauthn.webauthntest.domain.webauthn.application.dto.AllowCredential;
import dsm.webauthn.webauthntest.domain.webauthn.domain.entity.PubKeyCredParam;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AuthInfoResponse {

    private String challenge;

    private List<AllowCredential> allowCredentials;

    private String userVerification;
}
