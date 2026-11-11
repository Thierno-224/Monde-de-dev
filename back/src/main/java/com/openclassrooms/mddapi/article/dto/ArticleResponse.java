package com.openclassrooms.mddapi.article.dto;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        String author,
        String theme,
        LocalDateTime createdAt
) {
}
