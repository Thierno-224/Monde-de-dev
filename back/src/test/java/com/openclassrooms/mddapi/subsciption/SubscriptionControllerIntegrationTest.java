package com.openclassrooms.mddapi.subsciption;

import com.openclassrooms.mddapi.security.jwt.JwtService;
import com.openclassrooms.mddapi.subscription.entity.Subscription;
import com.openclassrooms.mddapi.subscription.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.theme.entity.Theme;
import com.openclassrooms.mddapi.theme.repository.ThemeRepository;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubscriptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private User user;
    private Theme theme;
    private String token;

    @BeforeEach
    void setUp() {
        subscriptionRepository.deleteAll();
        themeRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("subscriber");
        user.setEmail("subscriber@example.com");
        user.setPassword("encoded-password");
        user = userRepository.save(user);

        theme = new Theme();
        theme.setTitle("Java");
        theme.setDescription("Développement Java");
        theme = themeRepository.save(theme);

        token = jwtService.generateToken(user.getId());
    }

    @Test
    void shouldSubscribeUserToTheme() throws Exception {
        mockMvc.perform(authenticated(
                        post("/api/themes/{themeId}/subscriptions", theme.getId())))
                .andExpect(status().isCreated());

        assertTrue(subscriptionRepository
                .existsByUserIdAndThemeId(user.getId(), theme.getId()));
    }

    @Test
    void shouldReturnConflictWhenAlreadySubscribed() throws Exception {
        createSubscription();

        mockMvc.perform(authenticated(
                        post("/api/themes/{themeId}/subscriptions", theme.getId())))
                .andExpect(status().isConflict());

        assertTrue(subscriptionRepository
                .existsByUserIdAndThemeId(user.getId(), theme.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenThemeDoesNotExist() throws Exception {
        mockMvc.perform(authenticated(
                        post("/api/themes/{themeId}/subscriptions", Long.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUnsubscribeUserFromTheme() throws Exception {
        createSubscription();

        mockMvc.perform(authenticated(
                        delete("/api/themes/{themeId}/subscriptions", theme.getId())))
                .andExpect(status().isNoContent());

        assertFalse(subscriptionRepository
                .existsByUserIdAndThemeId(user.getId(), theme.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenSubscriptionDoesNotExist() throws Exception {
        mockMvc.perform(authenticated(
                        delete("/api/themes/{themeId}/subscriptions", theme.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUserSubscriptions() throws Exception {
        createSubscription();

        mockMvc.perform(authenticated(
                        get("/api/users/me/subscriptions")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(theme.getId()))
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].subscribed").value(true));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoSubscriptions() throws Exception {
        mockMvc.perform(authenticated(
                        get("/api/users/me/subscriptions")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(post(
                        "/api/themes/{themeId}/subscriptions", theme.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenGettingSubscriptionsWithoutToken()
            throws Exception {
        mockMvc.perform(get("/api/users/me/subscriptions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenUnsubscribingWithoutToken()
            throws Exception {
        mockMvc.perform(delete(
                        "/api/themes/{themeId}/subscriptions", theme.getId()))
                .andExpect(status().isUnauthorized());
    }

    private void createSubscription() {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setTheme(theme);
        subscriptionRepository.save(subscription);
    }

    private MockHttpServletRequestBuilder authenticated(
            MockHttpServletRequestBuilder request
    ) {
        return request.header(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + token
        );
    }
}