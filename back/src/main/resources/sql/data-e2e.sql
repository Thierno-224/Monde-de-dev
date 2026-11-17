
INSERT INTO users (username, email, password)
VALUES (
           'cypress_user',
           'cypress@test.com',
           '$2a$10$SotrgEIjA1obwLJUQz8kBO7J4QHCqkhcQYvx7/LSZxxk1AcjoGnW6'
       );

INSERT INTO themes (title, description)
VALUES
    ('Java', 'Développement Java et Spring Boot'),
    ('Angular', 'Développement frontend avec Angular'),
    ('DevOps', 'Docker, CI/CD et déploiement');

INSERT INTO subscriptions (user_id, theme_id)
SELECT u.id, t.id
FROM users u
         CROSS JOIN themes t
WHERE u.email = 'cypress@test.com'
  AND t.title IN ('Java', 'Angular');

INSERT INTO articles (title, content, theme_id, author_id, created_at)
SELECT
    'Introduction à Spring Boot',
    'Découvrez les fondamentaux de Spring Boot.',
    t.id,
    u.id,
    '2026-01-10 10:00:00'
FROM themes t
         CROSS JOIN users u
WHERE t.title = 'Java'
  AND u.email = 'cypress@test.com';

INSERT INTO articles (title, content, theme_id, author_id, created_at)
SELECT
    'Les composants Angular',
    'Comprendre les composants standalone et les signals.',
    t.id,
    u.id,
    '2026-02-15 10:00:00'
FROM themes t
         CROSS JOIN users u
WHERE t.title = 'Angular'
  AND u.email = 'cypress@test.com';

INSERT INTO comments (content, article_id, author_id, created_at)
SELECT
    'Excellent article pour découvrir Spring Boot !',
    a.id,
    u.id,
    CURRENT_TIMESTAMP
FROM articles a
         CROSS JOIN users u
WHERE a.title = 'Introduction à Spring Boot'
  AND u.email = 'cypress@test.com';

