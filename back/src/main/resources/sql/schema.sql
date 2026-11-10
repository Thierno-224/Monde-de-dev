CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE topics (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL UNIQUE,
                        description TEXT NOT NULL
);

CREATE TABLE subscriptions (
                               user_id BIGINT NOT NULL,
                               topic_id BIGINT NOT NULL,

                               PRIMARY KEY (user_id, topic_id),

                               CONSTRAINT fk_subscription_user
                                   FOREIGN KEY (user_id) REFERENCES users(id),

                               CONSTRAINT fk_subscription_topic
                                   FOREIGN KEY (topic_id) REFERENCES topics(id)
);

CREATE TABLE articles (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          content TEXT NOT NULL,
                          author_id BIGINT NOT NULL,
                          topic_id BIGINT NOT NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_article_author
                              FOREIGN KEY (author_id) REFERENCES users(id),

                          CONSTRAINT fk_article_topic
                              FOREIGN KEY (topic_id) REFERENCES topics(id)
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
