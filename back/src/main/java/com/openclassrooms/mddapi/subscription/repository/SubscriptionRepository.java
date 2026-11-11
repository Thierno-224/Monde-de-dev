package com.openclassrooms.mddapi.subscription.repository;

import com.openclassrooms.mddapi.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    boolean existsByUserIdAndThemeId(Long userId, Long themeId);

    List<Subscription> findByUserId(Long userId);

    Optional<Subscription> findByUserIdAndThemeId(
            Long userId,
            Long themeId
    );
}
