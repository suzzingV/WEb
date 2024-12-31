package dsm.webauthn.webauthntest.domain.webauthn.infrastructure;

import dsm.webauthn.webauthntest.domain.webauthn.domain.entity.PubKeyCredParam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PubKeyCredParamRepository extends JpaRepository<PubKeyCredParam, Long> {
}
