package com.openclassrooms.mddapi.auth.controller;

import com.openclassrooms.mddapi.auth.dto.AuthResponse;
import com.openclassrooms.mddapi.auth.dto.LoginRequest;
import com.openclassrooms.mddapi.auth.dto.RegisterRequest;
import com.openclassrooms.mddapi.auth.service.AuthService;
import com.openclassrooms.mddapi.auth.service.RegistrationService;
import com.openclassrooms.mddapi.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        registrationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                authService.getCurrentUser(authentication)
        );
    }
}
