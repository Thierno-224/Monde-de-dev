package com.openclassrooms.mddapi.theme.dto;

public record ThemeResponse(
        Long id,
        String title,
        String description,
        boolean subscribed
) {}