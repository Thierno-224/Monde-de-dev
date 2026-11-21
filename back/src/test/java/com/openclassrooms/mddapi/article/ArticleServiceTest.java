package com.openclassrooms.mddapi.article;

import com.openclassrooms.mddapi.article.dto.ArticleResponse;
import com.openclassrooms.mddapi.article.dto.CreateArticleRequest;
import com.openclassrooms.mddapi.article.entity.Article;
import com.openclassrooms.mddapi.article.repository.ArticleRepository;
import com.openclassrooms.mddapi.article.service.ArticleService;
import com.openclassrooms.mddapi.subscription.repository.SubscriptionRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void shouldCreateArticleWithCorrectAuthorAndTheme() {
        Long userId = 1L;
        Long themeId = 2L;

        CreateArticleRequest request = new CreateArticleRequest(
                "Spring Boot",
                "Introduction à Spring Boot",
                themeId
        );

        User author = mock(User.class);
        Theme theme = mock(Theme.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(author));

        when(themeRepository.findById(themeId))
                .thenReturn(Optional.of(theme));

        when(author.getUsername()).thenReturn("test_user");
        when(theme.getTitle()).thenReturn("Java");

        articleService.createArticle(request, userId);

        ArgumentCaptor<Article> captor =
                ArgumentCaptor.forClass(Article.class);

        verify(articleRepository).save(captor.capture());

        Article savedArticle = captor.getValue();

        assertEquals(request.title(), savedArticle.getTitle());
        assertEquals(request.content(), savedArticle.getContent());
        assertSame(author, savedArticle.getAuthor());
        assertSame(theme, savedArticle.getTheme());
    }

    @Test
    void shouldReturnEmptyFeedWhenUserHasNoSubscriptions() {
        Long userId = 1L;
        int page = 0;
        int size = 10;

        when(subscriptionRepository.findByUserId(userId))
                .thenReturn(List.of());

        Page<ArticleResponse> result =
                articleService.getArticlesBySubscription(
                        userId,
                        page,
                        size,
                        Sort.Direction.DESC
                );

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getNumber());

        verifyNoInteractions(articleRepository);
    }
}
