package com.openclassrooms.mddapi.user.controller;

import com.openclassrooms.mddapi.user.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.user.dto.UserResponse;
import com.openclassrooms.mddapi.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(
                userService.updateProfile(authentication, request)
        );
    }
}