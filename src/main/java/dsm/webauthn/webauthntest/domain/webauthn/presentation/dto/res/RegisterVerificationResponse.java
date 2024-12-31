package dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterVerificationResponse {

    private Long userId;
}
