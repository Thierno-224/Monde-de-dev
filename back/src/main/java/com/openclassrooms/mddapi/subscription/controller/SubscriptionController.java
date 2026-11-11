package com.openclassrooms.mddapi.subscription.controller;

import com.openclassrooms.mddapi.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{themeId}/subscriptions")
    public ResponseEntity<Void> subscribe(
            @PathVariable Long themeId,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());

        subscriptionService.subscribe(userId, themeId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{themeId}/subscriptions")
    public ResponseEntity<Void> unsubscribe(
            @PathVariable Long themeId,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());

        subscriptionService.unsubscribe(userId, themeId);

        return ResponseEntity.noContent().build();
    }
}