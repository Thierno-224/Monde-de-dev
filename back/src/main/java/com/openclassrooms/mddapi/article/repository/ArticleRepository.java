package com.openclassrooms.mddapi.article.repository;

import com.openclassrooms.mddapi.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
