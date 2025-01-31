package dsm.webauthn.webauthntest.domain.webauthn.util;

import dsm.webauthn.webauthntest.domain.webauthn.exception.WebAuthnException;
import dsm.webauthn.webauthntest.global.response.properties.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeUtil {

    public static byte[] generateChallenge() {
        UUID uuid = UUID.randomUUID();
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();
        return ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
    }
}
