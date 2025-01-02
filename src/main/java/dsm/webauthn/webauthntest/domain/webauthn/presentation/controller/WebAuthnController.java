package dsm.webauthn.webauthntest.domain.webauthn.presentation.controller;

import dsm.webauthn.webauthntest.domain.webauthn.application.service.WebAuthnService;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.AuthInfoResponse;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.RegisterInfoResponse;
import dsm.webauthn.webauthntest.domain.webauthn.presentation.dto.res.RegisterVerificationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webauthn")
@RequiredArgsConstructor
public class WebAuthnController {

    private final WebAuthnService webAuthnService;

    @GetMapping("/register/info/{userId}")
    public ResponseEntity<?> getRegistrationInfo(@PathVariable Long userId) {
        RegisterInfoResponse response = webAuthnService.getRegistrationInfo(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/{userId}")
    public ResponseEntity<RegisterVerificationResponse> register(@PathVariable Long userId, @RequestBody String request) {
        RegisterVerificationResponse response = webAuthnService.register(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/{userId}")
    public ResponseEntity<AuthInfoResponse> getAuthenticationInfo(@PathVariable Long userId) {
        AuthInfoResponse response = webAuthnService.getAuthenticationInfo(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/{userId}")
    public ResponseEntity<RegisterVerificationResponse> authenticate(@PathVariable Long userId, @RequestBody String request) {
        RegisterVerificationResponse response = webAuthnService.authenticate(userId, request);
        return ResponseEntity.ok(response);
    }
}
