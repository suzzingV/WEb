package dsm.webauthn.webauthntest.domain.webauthn.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllowCredential {

    private String type;

    private String id;
}
