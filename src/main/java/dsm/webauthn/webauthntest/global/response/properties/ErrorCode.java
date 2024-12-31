package dsm.webauthn.webauthntest.global.response.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400
    VALIDATION_ERROR(BAD_REQUEST, "입력값이 유효하지 않습니다."),
    CHALLENGE_ENCODING_ERROR(BAD_REQUEST, "챌린지 값이 null이거나 비어서는 안됩니다."),

    // 404
    USER_NOT_FOUND(NOT_FOUND, "user을 찾을 수 없습니다."),

    // 500
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "예상치 못한 서버 에러가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
