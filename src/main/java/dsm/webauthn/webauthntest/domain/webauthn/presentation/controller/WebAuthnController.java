package dsm.webauthn.webauthntest.domain.webauthn.presentation.controller;

import dsm.webauthn.webauthntest.domain.webauthn.application.service.WebAuthnService;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.req.RegisterVerificationRequest;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.RegisterInitResponse;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.RegisterVerificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webauthn")
@RequiredArgsConstructor
public class WebAuthnController {

    private final WebAuthnService webAuthnService;

    @PostMapping("/register/init/{userId}")
    public ResponseEntity<RegisterInitResponse> init(@PathVariable Long userId) {
        RegisterInitResponse response = webAuthnService.init(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/verification/{userId}")
    public ResponseEntity<RegisterVerificationResponse> register(@PathVariable Long userId, @RequestBody String request) {
        RegisterVerificationResponse response = webAuthnService.register(userId, request);
        return ResponseEntity.ok(response);
    }

}
