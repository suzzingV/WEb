package dsm.webauthn.webauthntest.domain.webauthn.infrastructure;

import dsm.webauthn.webauthntest.domain.webauthn.domain.entity.Credential;
import dsm.webauthn.webauthntest.domain.webauthn.domain.entity.PubKeyCredParam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
}
