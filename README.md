# portfinder

A community hub for discovering **game decompilation** and **recompilation** port projects.

Browse a ranked feed, filter by platform and tags, explore categories, vote on projects, and submit new entries for moderator review. Seed catalog data lives in git; the running app stores projects, users, and votes in a database.

**Policy:** metadata and links only. This project never hosts ROMs, ISOs, or copyrighted game assets.

## Stack

- Java 21
- Spring Boot 4
- Thymeleaf + terminal-themed CSS
- Spring Data JPA + Flyway
- H2 (local/tests) / PostgreSQL (production)
- Optional Google OAuth 2.0 for sign-in, votes, and submissions

## Architecture

| Layer | Package | Role |
|-------|---------|------|
| Domain | `com.portfinder.domain` | `Project`, categories, status, publication |
| Application | `com.portfinder.application` | query, filter, votes, submissions |
| Infrastructure | `com.portfinder.infrastructure` | JPA, JSON seed loader, security |
| Web | `com.portfinder.web` | Controllers + Thymeleaf views |

## Run locally

Requires JDK 21.

```bash
./mvnw test
./mvnw spring-boot:run
```

App: http://localhost:8080

By default the app uses in-memory H2, seeds from `src/main/resources/catalog/projects/*.json`, and leaves Google OAuth disabled (`auth offline` in the header).

## Deploy (Render from GitHub)

v1 launch is **catalog-only**: PostgreSQL persistence, JSON seed on boot, Google OAuth left **off** (`PORTFINDER_OAUTH_ENABLED=false`). Votes and in-app submit stay gated until auth is enabled later.

1. Push `main` to GitHub (this repo).
2. In [Render](https://render.com), **New → Blueprint**, connect `caiosouzabdev/portfinder`, and apply [`render.yaml`](render.yaml).
3. Wait for the first Docker build. The free web service + free Postgres are created together.
4. Open the service URL (Render also sets `RENDER_EXTERNAL_URL` for sitemap links).

Files involved: [`Dockerfile`](Dockerfile), [`render.yaml`](render.yaml). Env details: [docs/ENV.md](docs/ENV.md).

## Google OAuth and PostgreSQL (local / later)

See [docs/ENV.md](docs/ENV.md) for variable names.

Example with auth enabled:

```bash
export PORTFINDER_OAUTH_ENABLED=true
export GOOGLE_CLIENT_ID=your-client-id
export GOOGLE_CLIENT_SECRET=your-client-secret
export PORTFINDER_ADMIN_EMAILS=you@example.com
export DATABASE_URL=jdbc:postgresql://localhost:5432/portfinder
export DATABASE_USERNAME=portfinder
export DATABASE_PASSWORD=secret
export DATABASE_DRIVER=org.postgresql.Driver
./mvnw spring-boot:run
```

Create a Google OAuth client with redirect URI `http://localhost:8080/login/oauth2/code/google` (and the production callback once auth is turned on).

Admin emails listed in `PORTFINDER_ADMIN_EMAILS` can open `/moderation` to approve or reject submissions.

## Categories

See [docs/CATEGORY_TAXONOMY.md](docs/CATEGORY_TAXONOMY.md).

## Contribute

See [CONTRIBUTING.md](CONTRIBUTING.md).

- Primary: in-app **Submit** (when auth is enabled)
- Suggest: GitHub Issue template
- Seed data: PR JSON under `src/main/resources/catalog/projects/`

## License

Catalog metadata is contributed by the community. Individual linked projects keep their own licenses.
