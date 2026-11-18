package com.openclassrooms.mddapi.subsciption;

import com.openclassrooms.mddapi.common.exception.ResourceConflictException;
import com.openclassrooms.mddapi.common.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.subscription.entity.Subscription;
import com.openclassrooms.mddapi.subscription.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.subscription.service.SubscriptionService;
import com.openclassrooms.mddapi.theme.entity.Theme;
import com.openclassrooms.mddapi.theme.repository.ThemeRepository;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void shouldSubscribeUserSuccessfully() {
        Long userId = 1L;
        Long themeId = 2L;

        User user = mock(User.class);
        Theme theme = mock(Theme.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(themeRepository.findById(themeId))
                .thenReturn(Optional.of(theme));

        when(subscriptionRepository.existsByUserIdAndThemeId(
                userId, themeId
        )).thenReturn(false);

        subscriptionService.subscribe(userId, themeId);

        ArgumentCaptor<Subscription> captor =
                ArgumentCaptor.forClass(Subscription.class);

        verify(subscriptionRepository).save(captor.capture());

        Subscription savedSubscription = captor.getValue();

        assertSame(user, savedSubscription.getUser());
        assertSame(theme, savedSubscription.getTheme());
    }

    @Test
    void shouldRejectDuplicateSubscription() {
        Long userId = 1L;
        Long themeId = 2L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(mock(User.class)));

        when(themeRepository.findById(themeId))
                .thenReturn(Optional.of(mock(Theme.class)));

        when(subscriptionRepository.existsByUserIdAndThemeId(
                userId, themeId
        )).thenReturn(true);

        assertThrows(
                ResourceConflictException.class,
                () -> subscriptionService.subscribe(userId, themeId)
        );

        verify(subscriptionRepository, never())
                .save(any(Subscription.class));
    }

    @Test
    void shouldRejectUnsubscribeWhenSubscriptionNotFound() {
        Long userId = 1L;
        Long themeId = 2L;

        when(subscriptionRepository.findByUserIdAndThemeId(
                userId, themeId
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> subscriptionService.unsubscribe(userId, themeId)
        );

        verify(subscriptionRepository, never())
                .delete(any(Subscription.class));
    }
}
