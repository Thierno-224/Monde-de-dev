package com.openclassrooms.mddapi.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(

        @NotBlank(message = "Le commentaire est obligatoire")
        String content

) {}