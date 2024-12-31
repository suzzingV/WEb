package dsm.webauthn.webauthntest.domain.webauthn.util;

import dsm.webauthn.webauthntest.domain.webauthn.exception.WebAuthnException;
import dsm.webauthn.webauthntest.global.response.properties.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Base64UrlUtil {

    public static String toBase64Url(byte[] data) {
        if (data == null || data.length == 0) {
            throw new WebAuthnException(ErrorCode.CHALLENGE_ENCODING_ERROR);
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
