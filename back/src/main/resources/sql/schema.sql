CREATE DATABASE IF NOT EXISTS mdd
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mdd;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE themes (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(100) NOT NULL UNIQUE,
                        description TEXT NOT NULL
);

CREATE TABLE subscriptions (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               theme_id BIGINT NOT NULL,

                               CONSTRAINT fk_subscription_user
                                   FOREIGN KEY (user_id) REFERENCES users(id),

                               CONSTRAINT fk_subscription_theme
                                   FOREIGN KEY (theme_id) REFERENCES themes(id),

                               CONSTRAINT uk_user_theme
                                   UNIQUE (user_id, theme_id)
);

CREATE TABLE articles (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          content TEXT NOT NULL,
                          author_id BIGINT NOT NULL,
                          theme_id BIGINT NOT NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_article_author
                              FOREIGN KEY (author_id) REFERENCES users(id),

                          CONSTRAINT fk_article_theme
                              FOREIGN KEY (theme_id) REFERENCES themes(id)
);

CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          content TEXT NOT NULL,
                          author_id BIGINT NOT NULL,
                          article_id BIGINT NOT NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_comment_author
                              FOREIGN KEY (author_id) REFERENCES users(id),

                          CONSTRAINT fk_comment_article
                              FOREIGN KEY (article_id) REFERENCES articles(id)
);