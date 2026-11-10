package com.openclassrooms.mddapi.user.dto;

public record UserResponse(
        Long id,
        String username,
        String email
) {}