package com.openclassrooms.mddapi.article.service;

import com.openclassrooms.mddapi.article.dto.ArticleResponse;
import com.openclassrooms.mddapi.article.dto.CreateArticleRequest;
import com.openclassrooms.mddapi.article.entity.Article;
import com.openclassrooms.mddapi.article.repository.ArticleRepository;
import com.openclassrooms.mddapi.common.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.theme.entity.Theme;
import com.openclassrooms.mddapi.theme.repository.ThemeRepository;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
