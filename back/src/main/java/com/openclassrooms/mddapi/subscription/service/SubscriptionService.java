package com.openclassrooms.mddapi.subscription.service;

import com.openclassrooms.mddapi.common.exception.ResourceConflictException;
import com.openclassrooms.mddapi.common.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.subscription.entity.Subscription;
import com.openclassrooms.mddapi.subscription.repository.SubscriptionRepository;

import com.openclassrooms.mddapi.theme.dto.ThemeResponse;
import com.openclassrooms.mddapi.theme.entity.Theme;
import com.openclassrooms.mddapi.theme.repository.ThemeRepository;

import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void subscribe(Long userId, Long themeId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ResourceNotFoundException("Thème introuvable"));

        if (subscriptionRepository.existsByUserIdAndThemeId(userId, themeId)) {
            throw new ResourceConflictException("Vous êtes déjà abonné à ce thème");
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setTheme(theme);

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void unsubscribe(Long userId, Long themeId) {
        Subscription subscription = subscriptionRepository
                .findByUserIdAndThemeId(userId, themeId)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement introuvable"));

        subscriptionRepository.delete(subscription);
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> getMySubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .stream()
                .map(subscription -> toResponse(subscription.getTheme()))
                .toList();
    }

    private ThemeResponse toResponse(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getTitle(),
                theme.getDescription(),
                true
        );
    }
}