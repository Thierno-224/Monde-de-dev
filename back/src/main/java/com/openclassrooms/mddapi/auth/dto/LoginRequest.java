package com.openclassrooms.mddapi.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "L'identifiant est obligatoire")
        String identifier,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String password
) {}