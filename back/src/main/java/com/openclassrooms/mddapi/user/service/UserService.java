package com.openclassrooms.mddapi.user.service;

import com.openclassrooms.mddapi.common.exception.BadRequestException;
import com.openclassrooms.mddapi.common.exception.ResourceConflictException;
import com.openclassrooms.mddapi.user.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.user.dto.UserResponse;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse updateProfile(
            Authentication authentication,
            UpdateProfileRequest request
    ) {
        Long userId = Long.valueOf(authentication.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(
                        "Utilisateur introuvable"
                ));

        validateUniqueness(request, userId);
        updateUser(user, request);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    private void validateUniqueness(UpdateProfileRequest request, Long userId) {
        if (userRepository.existsByEmailAndIdNot(request.email().trim(), userId)) {
            throw new ResourceConflictException("Cet email est déjà utilisé");
        }

        if (userRepository.existsByUsernameAndIdNot(request.username().trim(), userId)) {
            throw new ResourceConflictException("Ce nom d'utilisateur est déjà utilisé");
        }
    }

    private void updateUser(User user, UpdateProfileRequest request) {
        user.setEmail(request.email().trim());
        user.setUsername(request.username().trim());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
    }
}
