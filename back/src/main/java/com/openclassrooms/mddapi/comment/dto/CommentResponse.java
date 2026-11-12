package com.openclassrooms.mddapi.comment.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String author,
        LocalDateTime createdAt
) {}