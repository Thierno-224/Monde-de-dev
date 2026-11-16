package com.openclassrooms.mddapi.article.repository;

import com.openclassrooms.mddapi.article.entity.Article;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByThemeIdIn(List<Long> themeIds, Sort sort);
}
