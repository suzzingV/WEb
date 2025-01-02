package dsm.webauthn.webauthntest.domain.webauthn.domain.entity;

import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AuthenticatorTransport;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

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

    @Lob
    private String registrationDataJSON;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    private Credential(Long userId, String credentialId, String publicKey, String registrationDataJSON) {
        this.userId = userId;
        this.credentialId = credentialId;
        this.publicKey = publicKey;
        this.createdAt = LocalDateTime.now();
        this.registrationDataJSON = registrationDataJSON;
    }

}
