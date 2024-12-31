package dsm.webauthn.webauthntest.domain.user.infrastructure;

import dsm.webauthn.webauthntest.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
