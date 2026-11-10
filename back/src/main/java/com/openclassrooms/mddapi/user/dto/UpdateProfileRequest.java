package com.openclassrooms.mddapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        String username,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email est invalide")
        String email,

        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String password

) {}