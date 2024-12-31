package dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res;

import dsm.webauthn.webauthntest.domain.user.domain.entity.User;
import dsm.webauthn.webauthntest.domain.webauthn.domain.entity.PubKeyCredParam;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RegisterInfoResponse {

    private String challenge;

    private String rp;

    private User user;

    private List<PubKeyCredParam> pubKeyCredParams;

    private String authenticatorAttachment;

    private boolean requireResidentKey;

    private String userVerification;
}
