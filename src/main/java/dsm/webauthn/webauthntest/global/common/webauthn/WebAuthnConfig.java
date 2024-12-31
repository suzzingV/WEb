package dsm.webauthn.webauthntest.global.common.webauthn;

import com.webauthn4j.WebAuthnManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebAuthnConfig {

    @Bean
    public WebAuthnManager webAuthnManager() {
        // WebAuthnManager 생성
        return WebAuthnManager.createNonStrictWebAuthnManager();
    }
}
