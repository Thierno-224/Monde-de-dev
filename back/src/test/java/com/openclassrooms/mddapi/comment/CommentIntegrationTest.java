package com.openclassrooms.mddapi.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.article.entity.Article;
import com.openclassrooms.mddapi.article.repository.ArticleRepository;
import com.openclassrooms.mddapi.comment.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.comment.entity.Comment;
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
class CommentIntegrationTest {

    private static final String COMMENTS_URL = "/api/articles/{articleId}/comments";
    private static final String COMMENT_CONTENT = "Merci pour cet article !";

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
    private Article article;
    private String token;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        subscriptionRepository.deleteAll();
        themeRepository.deleteAll();
        userRepository.deleteAll();

        user = createUser();
        Theme theme = createTheme();
        article = createArticle(user, theme);

        token = jwtService.generateToken(user.getId());
    }

    @Test
    void shouldCreateCommentSuccessfully() throws Exception {
        mockMvc.perform(post(COMMENTS_URL, article.getId())
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody(COMMENT_CONTENT)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.content").value(COMMENT_CONTENT))
                .andExpect(jsonPath("$.author").value(user.getUsername()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRejectBlankComment() throws Exception {
        mockMvc.perform(post(COMMENTS_URL, article.getId())
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content")
                        .value("Le commentaire est obligatoire"));
    }

    @Test
    void shouldRejectMissingContent() throws Exception {
        mockMvc.perform(post(COMMENTS_URL, article.getId())
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content")
                        .value("Le commentaire est obligatoire"));
    }

    @Test
    void shouldReturnNotFoundWhenArticleDoesNotExist() throws Exception {
        mockMvc.perform(post(COMMENTS_URL, Long.MAX_VALUE)
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody(COMMENT_CONTENT)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectCommentCreationWithoutAuthentication() throws Exception {
        mockMvc.perform(post(COMMENTS_URL, article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody(COMMENT_CONTENT)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnCommentsForArticle() throws Exception {
        createComment(article, user, "Premier commentaire");
        createComment(article, user, "Deuxième commentaire");

        mockMvc.perform(get(COMMENTS_URL, article.getId())
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content")
                        .value("Premier commentaire"))
                .andExpect(jsonPath("$[1].content")
                        .value("Deuxième commentaire"))
                .andExpect(jsonPath("$[0].author")
                        .value(user.getUsername()));
    }

    @Test
    void shouldReturnEmptyListWhenArticleHasNoComments() throws Exception {
        mockMvc.perform(get(COMMENTS_URL, article.getId())
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldNotReturnCommentsFromAnotherArticle() throws Exception {
        Article otherArticle = createArticle(
                user,
                article.getTheme()
        );

        createComment(otherArticle, user, "Autre article");
        createComment(article, user, COMMENT_CONTENT);

        mockMvc.perform(get(COMMENTS_URL, article.getId())
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content")
                        .value(COMMENT_CONTENT));
    }

    @Test
    void shouldRejectCommentRetrievalWithoutAuthentication() throws Exception {
        mockMvc.perform(get(COMMENTS_URL, article.getId()))
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
        Theme theme = new Theme();
        theme.setTitle("Java");
        theme.setDescription("Articles sur Java");

        return themeRepository.save(theme);
    }

    private Article createArticle(User author, Theme theme) {
        Article newArticle = new Article();
        newArticle.setTitle("Introduction à Spring Boot");
        newArticle.setContent("Contenu de l'article");
        newArticle.setAuthor(author);
        newArticle.setTheme(theme);

        return articleRepository.saveAndFlush(newArticle);
    }

    private Comment createComment(
            Article article,
            User author,
            String content
    ) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setArticle(article);
        comment.setAuthor(author);

        return commentRepository.saveAndFlush(comment);
    }

    private String bearerToken() {
        return "Bearer " + token;
    }

    private String commentBody(String content) throws Exception {
        return objectMapper.writeValueAsString(
                new CreateCommentRequest(content)
        );
    }
}
