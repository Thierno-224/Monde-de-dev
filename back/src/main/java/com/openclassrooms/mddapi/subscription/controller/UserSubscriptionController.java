package com.openclassrooms.mddapi.subscription.controller;

import com.openclassrooms.mddapi.subscription.service.SubscriptionService;
import com.openclassrooms.mddapi.theme.dto.ThemeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users/me/subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getMySubscriptions(
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                subscriptionService.getMySubscriptions(userId)
        );
    }
}
