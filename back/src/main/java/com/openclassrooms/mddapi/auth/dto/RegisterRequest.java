package com.openclassrooms.mddapi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(max = 100, message = "Le nom d'utilisateur est trop long")
        String username,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Le format de l'email est invalide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$",
                message = "Le mot de passe doit contenir une majuscule, " +
                        "une minuscule, un chiffre et un caractère spécial"
        )
        String password
) {
}
