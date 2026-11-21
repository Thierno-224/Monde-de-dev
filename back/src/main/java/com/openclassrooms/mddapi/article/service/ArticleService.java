package com.openclassrooms.mddapi.article.service;

import com.openclassrooms.mddapi.article.dto.ArticleResponse;
import com.openclassrooms.mddapi.article.dto.CreateArticleRequest;
import com.openclassrooms.mddapi.article.entity.Article;
import com.openclassrooms.mddapi.article.repository.ArticleRepository;
import com.openclassrooms.mddapi.common.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.subscription.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.theme.entity.Theme;
import com.openclassrooms.mddapi.theme.repository.ThemeRepository;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ArticleService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ThemeRepository themeRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public ArticleResponse createArticle(CreateArticleRequest request, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new ResourceNotFoundException("Theme not found"));

        Article article = new Article();
        article.setTitle(request.title());
        article.setContent(request.content());
        article.setAuthor(author);
        article.setTheme(theme);
        articleRepository.save(article);
        return mapToArticleResponse(article);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getAllArticles() {
        List<Article> articles = articleRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return articles.stream()
                .map(this::mapToArticleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> getArticlesBySubscription(
            Long userId,
            int page,
            int size,
            Sort.Direction direction
    ) {
        List<Long> themeIds = subscriptionRepository
                .findByUserId(userId)
                .stream()
                .map(subscription -> subscription.getTheme().getId())
                .toList();

        if (themeIds.isEmpty())
            return Page.empty();

        Pageable pageable = PageRequest.of(
                page, size, Sort.by(direction, "createdAt")
        );

        return articleRepository
                .findByThemeIdIn(themeIds, pageable)
                .map(this::mapToArticleResponse);

    }

    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        return mapToArticleResponse(article);
    }

    private ArticleResponse mapToArticleResponse(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getAuthor().getUsername(),
                article.getTheme().getTitle(),
                article.getCreatedAt()
        );
    }
}
