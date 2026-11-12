package com.openclassrooms.mddapi.article.controller;

import com.openclassrooms.mddapi.article.dto.ArticleResponse;
import com.openclassrooms.mddapi.article.dto.CreateArticleRequest;
import com.openclassrooms.mddapi.article.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @Valid @RequestBody CreateArticleRequest request,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                articleService.createArticle(request, userId)
        );
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }
}
