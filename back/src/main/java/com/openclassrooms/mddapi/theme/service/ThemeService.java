package com.openclassrooms.mddapi.theme.service;

import com.openclassrooms.mddapi.subscription.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.theme.dto.ThemeResponse;
import com.openclassrooms.mddapi.theme.entity.Theme;
import com.openclassrooms.mddapi.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<ThemeResponse> getAllThemes(Long userId) {
        return themeRepository.findAll()
                .stream()
                .map(theme -> toResponse(theme, userId))
                .toList();
    }

    private ThemeResponse toResponse(Theme theme, Long userId) {
        boolean subscribed = subscriptionRepository
                .existsByUserIdAndThemeId(userId, theme.getId());

        return new ThemeResponse(
                theme.getId(),
                theme.getTitle(),
                theme.getDescription(),
                subscribed
        );
    }
}