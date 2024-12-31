package dsm.webauthn.webauthntest.domain.webauthn.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    private Credential(Long userId, String credentialId, String publicKey) {
        this.userId = userId;
        this.credentialId = credentialId;
        this.publicKey = publicKey;
        this.createdAt = LocalDateTime.now();
    }

}
