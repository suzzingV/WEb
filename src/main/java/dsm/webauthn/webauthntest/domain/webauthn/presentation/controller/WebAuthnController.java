package dsm.webauthn.webauthntest.domain.webauthn.presentation.controller;

import dsm.webauthn.webauthntest.domain.webauthn.application.service.WebAuthnService;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webauthn")
@RequiredArgsConstructor
public class WebAuthnController {

    private final WebAuthnService webAuthnService;

    @PostMapping("/init/{userId}")
    public ResponseEntity<RegisterResponse> init(@PathVariable Long userId) {
        RegisterResponse response = webAuthnService.init(userId);
        return ResponseEntity.ok(response);
    }

}
