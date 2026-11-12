package com.openclassrooms.mddapi.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.article.dto.CreateArticleRequest;
import com.openclassrooms.mddapi.article.entity.Article;
import com.openclassrooms.mddapi.article.repository.ArticleRepository;
import com.openclassrooms.mddapi.comment.repository.CommentRepository;
import com.openclassrooms.mddapi.security.jwt.JwtService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArticleIntegrationTest {

    private static final String ARTICLES_URL = "/api/articles";
    private static final String ARTICLE_TITLE = "Introduction à Spring Boot";
    private static final String ARTICLE_CONTENT = "Contenu de l'article";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Theme theme;
    private String token;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        subscriptionRepository.deleteAll();
        themeRepository.deleteAll();
        userRepository.deleteAll();

        user = createUser();
        theme = createTheme();

        token = jwtService.generateToken(user.getId());
    }

    @Test
    void shouldCreateArticleSuccessfully() throws Exception {
        mockMvc.perform(post(ARTICLES_URL)
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleBody(
                                ARTICLE_TITLE,
                                ARTICLE_CONTENT,
                                theme.getId()
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(ARTICLE_TITLE))
                .andExpect(jsonPath("$.content").value(ARTICLE_CONTENT))
                .andExpect(jsonPath("$.author").value(user.getUsername()))
                .andExpect(jsonPath("$.theme").value(theme.getTitle()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRejectBlankTitle() throws Exception {
        mockMvc.perform(post(ARTICLES_URL)
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleBody(
                                "",
                                ARTICLE_CONTENT,
                                theme.getId()
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankContent() throws Exception {
        mockMvc.perform(post(ARTICLES_URL)
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleBody(
                                ARTICLE_TITLE,
                                " ",
                                theme.getId()
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMissingThemeId() throws Exception {
        mockMvc.perform(post(ARTICLES_URL)
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleBody(
                                ARTICLE_TITLE,
                                ARTICLE_CONTENT,
                                null
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenThemeDoesNotExist() throws Exception {
        mockMvc.perform(post(ARTICLES_URL)
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleBody(
                                ARTICLE_TITLE,
                                ARTICLE_CONTENT,
                                Long.MAX_VALUE
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectArticleCreationWithoutAuthentication() throws Exception {
        mockMvc.perform(post(ARTICLES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleBody(
                                ARTICLE_TITLE,
                                ARTICLE_CONTENT,
                                theme.getId()
                        )))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnArticleById() throws Exception {
        Article article = createArticle(ARTICLE_TITLE, ARTICLE_CONTENT);

        mockMvc.perform(get(ARTICLES_URL + "/{id}", article.getId())
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(article.getId()))
                .andExpect(jsonPath("$.title").value(ARTICLE_TITLE))
                .andExpect(jsonPath("$.content").value(ARTICLE_CONTENT))
                .andExpect(jsonPath("$.author").value(user.getUsername()))
                .andExpect(jsonPath("$.theme").value(theme.getTitle()));
    }

    @Test
    void shouldReturnNotFoundWhenArticleDoesNotExist() throws Exception {
        mockMvc.perform(get(ARTICLES_URL + "/{id}", Long.MAX_VALUE)
                        .header("Authorization", bearerToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllArticles() throws Exception {
        createArticle("Premier article", "Premier contenu");
        createArticle("Deuxième article", "Deuxième contenu");

        mockMvc.perform(get(ARTICLES_URL)
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturnEmptyListWhenNoArticlesExist() throws Exception {
        mockMvc.perform(get(ARTICLES_URL)
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnArticlesFromAllThemes() throws Exception {
        Theme anotherTheme = createTheme("Angular", "Articles sur Angular");

        createArticle(ARTICLE_TITLE, ARTICLE_CONTENT);
        createArticle(
                "Introduction à Angular",
                "Contenu Angular",
                anotherTheme
        );

        mockMvc.perform(get(ARTICLES_URL)
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldRejectArticleRetrievalWithoutAuthentication() throws Exception {
        mockMvc.perform(get(ARTICLES_URL))
                .andExpect(status().isUnauthorized());
    }

    private User createUser() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("encoded-password");

        return userRepository.save(newUser);
    }

    private Theme createTheme() {
        return createTheme("Java", "Articles sur Java");
    }

    private Theme createTheme(String title, String description) {
        Theme newTheme = new Theme();
        newTheme.setTitle(title);
        newTheme.setDescription(description);

        return themeRepository.save(newTheme);
    }

    private Article createArticle(String title, String content) {
        return createArticle(title, content, theme);
    }

    private Article createArticle(
            String title,
            String content,
            Theme articleTheme
    ) {
        Article newArticle = new Article();
        newArticle.setTitle(title);
        newArticle.setContent(content);
        newArticle.setAuthor(user);
        newArticle.setTheme(articleTheme);

        return articleRepository.saveAndFlush(newArticle);
    }

    private String articleBody(
            String title,
            String content,
            Long themeId
    ) throws Exception {
        CreateArticleRequest request =
                new CreateArticleRequest(title, content, themeId);

        return objectMapper.writeValueAsString(request);
    }

    private String bearerToken() {
        return "Bearer " + token;
    }
}
