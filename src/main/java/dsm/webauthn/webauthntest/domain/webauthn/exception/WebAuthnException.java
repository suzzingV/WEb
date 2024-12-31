package dsm.webauthn.webauthntest.domain.webauthn.exception;

import dsm.webauthn.webauthntest.global.response.exceptionClass.CustomException;
import dsm.webauthn.webauthntest.global.response.properties.ErrorCode;

public class WebAuthnException extends CustomException {

    public WebAuthnException(ErrorCode errorCode) {
        super(errorCode);
    }

    public WebAuthnException(ErrorCode errorCode, String runtimeValue) {
        super(errorCode, runtimeValue);
    }
}
