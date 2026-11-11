package com.openclassrooms.mddapi.theme;

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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ThemeControllerIntegrationTest {

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

        user = createUser("user1", "user1@example.com");
        theme = createTheme("Java", "Développement Java");

        token = jwtService.generateToken(user.getId());
    }

    @Test
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/themes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnAllThemes() throws Exception {
        createTheme("Angular", "Développement Angular");

        mockMvc.perform(get("/api/themes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].title")
                        .value(hasItem("Java")))
                .andExpect(jsonPath("$[*].title")
                        .value(hasItem("Angular")));
    }

    @Test
    void shouldReturnSubscribedFalse() throws Exception {
        mockMvc.perform(get("/api/themes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subscribed").value(false));
    }

    @Test
    void shouldReturnSubscribedTrue() throws Exception {
        createSubscription(user, theme);

        mockMvc.perform(get("/api/themes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subscribed").value(true));
    }

    @Test
    void shouldReturnEmptyListWhenNoThemesExist() throws Exception {
        themeRepository.deleteAll();

        mockMvc.perform(get("/api/themes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldNotReturnOtherUserSubscription() throws Exception {
        User otherUser = createUser(
                "user2",
                "user2@example.com"
        );

        createSubscription(otherUser, theme);

        mockMvc.perform(get("/api/themes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subscribed").value(false));
    }

    private User createUser(String username, String email) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword("encoded-password");

        return userRepository.save(newUser);
    }

    private Theme createTheme(String title, String description) {
        Theme newTheme = new Theme();
        newTheme.setTitle(title);
        newTheme.setDescription(description);

        return themeRepository.save(newTheme);
    }

    private void createSubscription(User user, Theme theme) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setTheme(theme);

        subscriptionRepository.save(subscription);
    }
}
