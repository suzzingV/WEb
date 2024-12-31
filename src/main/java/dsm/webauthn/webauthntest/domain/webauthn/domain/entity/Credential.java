package dsm.webauthn.webauthntest.domain.webauthn.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "credential_TB")
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String credentialId;

    @Lob
    private String publicKey;

    @Builder
    private Credential(Long userId, String credentialId, String publicKey) {
        this.userId = userId;
        this.credentialId = credentialId;
        this.publicKey = publicKey;
    }

}
