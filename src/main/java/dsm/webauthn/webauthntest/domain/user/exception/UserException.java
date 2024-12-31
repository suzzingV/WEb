package dsm.webauthn.webauthntest.domain.user.exception;

import dsm.webauthn.webauthntest.global.response.exceptionClass.CustomException;
import dsm.webauthn.webauthntest.global.response.properties.ErrorCode;

public class UserException extends CustomException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String runtimeValue) {
        super(errorCode, runtimeValue);
    }
}
