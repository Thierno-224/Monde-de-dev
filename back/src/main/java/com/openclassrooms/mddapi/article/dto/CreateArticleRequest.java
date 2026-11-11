package com.openclassrooms.mddapi.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateArticleRequest(
        @NotBlank(message = "Le titre de l'article est obligatoire")
        @Size(max = 100, message = "Le titre de l'article est trop long")
        String title,

        @NotBlank(message = "Le contenu de l'article est obligatoire")
        @Size(max = 1000, message = "Le contenu de l'article est trop long")
        String content,

        @NotNull(message = "L'ID du thème est obligatoire")
        Long themeId
) {
}
