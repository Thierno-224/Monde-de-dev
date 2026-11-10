package com.openclassrooms.mddapi.auth.service;

import com.openclassrooms.mddapi.auth.dto.AuthResponse;
import com.openclassrooms.mddapi.auth.dto.LoginRequest;
import com.openclassrooms.mddapi.security.jwt.JwtService;
import com.openclassrooms.mddapi.user.dto.UserResponse;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmailOrUsername(
                        request.identifier(),
                        request.identifier()
                )
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Identifiants incorrects"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new BadCredentialsException(
                    "Identifiants incorrects"
            );
        }

        String token = jwtService.generateToken(user.getId());

        return new AuthResponse(token);
    }

    public UserResponse getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Utilisateur non authentifié"
            );
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AuthenticationCredentialsNotFoundException(
                                "Utilisateur non authentifié"
                        )
                );

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}