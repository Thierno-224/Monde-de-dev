package com.openclassrooms.mddapi.auth.service;

import com.openclassrooms.mddapi.auth.dto.RegisterRequest;
import com.openclassrooms.mddapi.common.exception.BadRequestException;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException(
                    "Cette adresse email est déjà utilisée"
            );
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException(
                    "Ce nom d'utilisateur est déjà utilisé"
            );
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
    }
}
