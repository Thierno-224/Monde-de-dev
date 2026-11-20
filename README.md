
# MDD — Monde de Dev

Application web full-stack permettant à des développeurs de partager des articles autour de thèmes techniques, de s'abonner aux thèmes qui les intéressent et d'échanger dans les commentaires.

Projet réalisé dans le cadre du parcours **Développeur Java / Angular d'OpenClassrooms**.

## Fonctionnalités

- Inscription, connexion et authentification par JWT.
- Consultation et modification du profil utilisateur.
- Consultation des thèmes et gestion des abonnements.
- Publication et consultation d'articles.
- Fil d'actualité basé sur les thèmes suivis, avec tri chronologique.
- Consultation et publication de commentaires.

## Stack technique

| Partie | Technologies |
| --- | --- |
| Backend | Java 21, Spring Boot 3.5.7, Spring Web, Spring Data JPA, Spring Security, Bean Validation |
| Authentification | JWT — JJWT 0.13.0 |
| Base de développement | MySQL |
| Frontend | Angular 20, TypeScript, RxJS |
| Tests backend | JUnit, Mockito, Spring Boot Test, Spring Security Test, H2 |
| Tests frontend | Jasmine, Karma |
| Tests end-to-end | Cypress 16 |
| Outils | Maven Wrapper, npm, Postman |

## Organisation du dépôt

```text
.
├── back/                          # API Spring Boot
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-e2e.properties
│   │   └── sql/
│   │       ├── schema.sql         # Schéma MySQL de développement
│   │       ├── data.sql           # Données fictives de développement
│   │       └── data-e2e.sql       # Données des tests E2E
│   └── src/test/resources/
│       └── application-test.properties
├── front/                         # Application Angular et tests Cypress
├── postman/                       # Collection Postman de l'API
└── README.md
```

## Prérequis

Installer et configurer :

- **Git** ;
- **JDK 21** (`java -version`) ;
- **MySQL**, accessible sur `localhost:3306` ;
- **Node.js** et **npm**, dans des versions compatibles avec Angular 20 ;
- un navigateur compatible avec les tests Angular/Cypress ;
- **Postman**, facultatif pour tester l'API manuellement.

Le projet inclut le **Maven Wrapper** : une installation globale de Maven n'est pas nécessaire.

## Installation

### 1. Cloner le dépôt

```bash
git clone git@github.com:Thierno-224/Monde-de-dev.git
cd Monde-de-dev
```

Le clonage SSH nécessite une clé SSH GitHub configurée. Vous pouvez également cloner le dépôt en HTTPS :

```bash
git clone https://github.com/Thierno-224/Monde-de-dev.git
cd Monde-de-dev
```

### 2. Préparer MySQL

Le backend utilise la base **`mdd`** et l'utilisateur MySQL **`mdd_app`**.

1. Ouvrir MySQL Workbench ou un autre client MySQL avec un compte autorisé à créer des bases et des utilisateurs.
2. Ouvrir `back/src/main/resources/sql/schema.sql`, copier son contenu dans l'éditeur SQL, puis l'exécuter. Le script crée la base `mdd` et les tables `users`, `themes`, `subscriptions`, `articles` et `comments`.
3. Créer l'utilisateur applicatif et lui attribuer les droits sur `mdd` (adapter le mot de passe) :

```sql
CREATE USER IF NOT EXISTS 'mdd_app'@'localhost' IDENTIFIED BY 'choisir-un-mot-de-passe-local';
GRANT ALL PRIVILEGES ON mdd.* TO 'mdd_app'@'localhost';
```

4. Ouvrir `back/src/main/resources/sql/data.sql`, **copier-coller son contenu dans le client MySQL**, sélectionner la base `mdd` et exécuter le script pour ajouter les utilisateurs, thèmes, articles, abonnements et commentaires fictifs.

> `schema.sql` sert à initialiser une base neuve. Il contient des instructions `CREATE TABLE` qui échoueront si les tables existent déjà. Les données de `data.sql` sont réservées au développement et à la démonstration.

### 3. Configurer le backend

Le fichier `back/src/main/resources/application.properties` contient notamment :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mdd
spring.datasource.username=mdd_app
spring.datasource.password=${MDD_DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=never
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=3600000
```

Dans un terminal macOS/Linux, définir les variables d'environnement **avant de démarrer** le backend :

```bash
export MDD_DB_PASSWORD='choisir-un-mot-de-passe-local'
export JWT_SECRET='remplacer-par-une-cle-secrete-aleatoire-et-robuste'
```

Le mot de passe doit correspondre à celui configuré pour `mdd_app`. Utiliser un secret JWT suffisamment long et aléatoire, compatible avec l'algorithme de signature de l'application. Ne jamais committer de vrais secrets ni les intégrer à la collection Postman.

Avec `ddl-auto=validate`, Hibernate **vérifie** le schéma mais ne crée pas les tables. Avec `spring.sql.init.mode=never`, Spring Boot **n'importe pas** automatiquement `data.sql` en développement.

### 4. Démarrer le backend

Dans le terminal où les variables d'environnement sont définies :

```bash
cd back
./mvnw spring-boot:run
```

API disponible par défaut sur **`http://localhost:8080`**.

### 5. Installer et démarrer le frontend

Dans un **autre terminal**, depuis la racine du dépôt :

```bash
cd front
npm ci
npm start
```

Application accessible sur **`http://localhost:4200`**.

En développement, Angular utilise l'URL d'API `http://localhost:8080/api` définie dans `front/src/environments/environment.development.ts`. Les deux serveurs doivent donc être lancés.

## API REST

URL de base : **`http://localhost:8080`**.

| Méthode | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Inscrire un utilisateur |
| `POST` | `/api/auth/login` | Se connecter et obtenir un JWT |
| `GET` | `/api/auth/me` | Consulter son profil |
| `PUT` | `/api/users/me` | Modifier son profil |
| `GET` | `/api/themes` | Lister les thèmes |
| `POST` | `/api/themes/{themeId}/subscriptions` | S'abonner à un thème |
| `DELETE` | `/api/themes/{themeId}/subscriptions` | Se désabonner d'un thème |
| `GET` | `/api/users/me/subscriptions` | Consulter ses abonnements |
| `POST` | `/api/articles` | Publier un article |
| `GET` | `/api/articles` | Lister les articles |
| `GET` | `/api/articles/{id}` | Consulter un article |
| `GET` | `/api/articles/feed?sort=DESC` | Consulter son fil d'actualité (`ASC` ou `DESC`) |
| `POST` | `/api/articles/{articleId}/comments` | Ajouter un commentaire |
| `GET` | `/api/articles/{articleId}/comments` | Consulter les commentaires d'un article |

Les opérations nécessitant une authentification utilisent un JWT, généralement transmis dans l'en-tête `Authorization: Bearer <token>`. Pour les exemples de requêtes et de réponses, utiliser la **collection Postman fournie** plutôt que de recopier les contrats dans ce README.

### Utiliser la collection Postman

1. Dans Postman, importer le fichier de collection présent dans `postman/`.
2. Créer un environnement local Postman.
3. Définir la variable d'URL de base utilisée par la collection, par exemple `base_url = http://localhost:8080`, **si la collection référence cette variable**.
4. Exécuter la requête de connexion et récupérer le JWT retourné.
5. Renseigner **la variable d'authentification réellement référencée par la collection** dans l'environnement Postman, puis sélectionner cet environnement avant d'exécuter les requêtes protégées.

Chaque utilisateur configure ses propres variables et identifiants. **Aucun JWT personnel ni secret ne doit être partagé dans le dépôt.** Les noms des variables doivent correspondre exactement aux références `{{...}}` du fichier Postman ; si des URL sont encore codées en dur, les adapter dans Postman.

## Tests

### Tests backend : JUnit, Mockito et intégration

Depuis `back/` :

```bash
./mvnw test
```

Les classes d'intégration configurées avec `@ActiveProfiles("test")` utilisent `back/src/test/resources/application-test.properties` :

- Base **H2 en mémoire** (`mdd_test`) avec mode de compatibilité MySQL ;
- Schéma créé puis supprimé automatiquement (`create-drop`) ;
- Pas d'import SQL automatique ;
- Secret JWT réservé aux tests.

Ces tests n'ont pas besoin de la base MySQL de développement.

Pour générer le fichier JAR :

```bash
./mvnw clean package
```

### Tests unitaires Angular

Depuis `front/` :

```bash
npm test -- --watch=false
```

Le projet utilise **Jasmine/Karma**. Selon l'environnement d'exécution, un navigateur compatible doit être installé et accessible.

### Tests end-to-end avec Cypress

Les tests E2E utilisent un backend Spring Boot démarré avec le profil **`e2e`** et une base **H2 distincte**, nommée `mdd_e2e`.

La configuration `back/src/main/resources/application-e2e.properties` prévoit :

- `spring.jpa.hibernate.ddl-auto=create-drop` : création et suppression du schéma ;
- `spring.sql.init.mode=always` : activation de l'initialisation SQL ;
- `spring.sql.init.data-locations=classpath:sql/data-e2e.sql` : chargement **automatique** des données E2E ;
- `spring.jpa.defer-datasource-initialization=true` : initialisation des données après la création du schéma.

**Terminal 1 — Backend E2E** (depuis `back/`) :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=e2e
```

**Terminal 2 — Frontend** (depuis `front/`) :

```bash
npm start
```

**Terminal 3 — Cypress** (depuis `front/`) :

```bash
npx cypress open
```

Ou en mode sans interface :

```bash
npx cypress run
```

Cypress cible **`http://localhost:4200`** via `front/cypress.config.ts`. Le frontend appelle le backend sur le port **8080**. Ne pas démarrer en parallèle deux instances du backend sur ce même port.

> Contrairement à `data.sql`, le script `data-e2e.sql` est chargé automatiquement **uniquement** lorsque le profil `e2e` est activé.

## Points de dépannage

- **Échec de connexion MySQL** : vérifier que MySQL fonctionne, que `mdd` existe, que `mdd_app` possède les droits nécessaires et que `MDD_DB_PASSWORD` est défini dans le terminal du backend.
- **Erreur Hibernate au démarrage** : exécuter `schema.sql` sur une base neuve ; le mode `validate` ne crée pas les tables.
- **Erreur JWT** : vérifier la présence et la robustesse de `JWT_SECRET` pour le profil de développement.
- **Réponse `401 Unauthorized`** : vérifier le jeton JWT et la configuration des variables Postman.
- **Frontend inaccessible** : vérifier que `npm start` fonctionne sur le port `4200`.
- **Échec des tests Cypress** : vérifier que le backend est lancé avec le profil `e2e`, que le frontend est disponible et que les ports `8080` et `4200` ne sont pas occupés par d'autres applications.

