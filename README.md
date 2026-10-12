# portfinder

A community hub for discovering **game decompilation** and **recompilation** port projects.

Browse a ranked feed, filter by platform and tags, explore categories, vote on projects, and submit new entries for moderator review. Seed catalog data lives in git; the running app stores projects, users, and votes in a database.

**Policy:** metadata and links only. This project never hosts ROMs, ISOs, or copyrighted game assets. See [Privacy](https://portfinder.onrender.com/privacy) and [Terms](https://portfinder.onrender.com/terms).

Live site: https://portfinder.onrender.com/

## Stack

- Java 21
- Spring Boot 4
- Thymeleaf + terminal-themed CSS
- Spring Data JPA + Flyway
- H2 (local/tests) / PostgreSQL (production)
- Google OAuth 2.0 for sign-in, votes, and submissions (enabled on Render)

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
THYMELEAF_CACHE=false ./mvnw spring-boot:run
```

App: http://localhost:8080

By default the app uses in-memory H2, seeds from `src/main/resources/catalog/projects/*.json`, and leaves Google OAuth disabled (`auth offline` in the header) until you set the env vars below.

## Deploy (Render from GitHub)

Production uses PostgreSQL, JSON seed on first boot, and **Google OAuth on** (`PORTFINDER_OAUTH_ENABLED=true` in [`render.yaml`](render.yaml)). Health checks hit `/health`.

1. Push `main` to GitHub (this repo).
2. In [Render](https://render.com), **New → Blueprint**, connect `caiosouzabdev/portfinder`, and apply [`render.yaml`](render.yaml).
3. Ensure the **Google OAuth 2.0** env group provides `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`.
4. Wait for the first Docker build. The free web service + free Postgres are created together.
5. Open the service URL (Render also sets `RENDER_EXTERNAL_URL` for sitemap links).

Files involved: [`Dockerfile`](Dockerfile), [`render.yaml`](render.yaml). Env details: [docs/ENV.md](docs/ENV.md).

## Google OAuth and PostgreSQL (local)

See [docs/ENV.md](docs/ENV.md) for variable names.

Example with auth enabled:

```bash
export PORTFINDER_OAUTH_ENABLED=true
export GOOGLE_CLIENT_ID=your-client-id
export GOOGLE_CLIENT_SECRET=your-client-secret
export PORTFINDER_ADMIN_EMAILS=portsfinder@gmail.com
export THYMELEAF_CACHE=false
export DATABASE_URL=jdbc:postgresql://localhost:5432/portfinder
export DATABASE_USERNAME=portfinder
export DATABASE_PASSWORD=secret
export DATABASE_DRIVER=org.postgresql.Driver
./mvnw spring-boot:run
```

Create a Google OAuth **Web application** client and add these **Authorized redirect URIs** (exact match, including `https`):

- Local: `http://localhost:8080/login/oauth2/code/google`
- Production: `https://portfinder.onrender.com/login/oauth2/code/google`

Also add the same hosts under **Authorized JavaScript origins**. A `redirect_uri_mismatch` error almost always means the production URI is missing or uses `http` instead of `https`.

Admin emails listed in `PORTFINDER_ADMIN_EMAILS` can open `/moderation` to approve or reject submissions.

## Categories

See [docs/CATEGORY_TAXONOMY.md](docs/CATEGORY_TAXONOMY.md).

## Submit a project

The git repository is private. Catalog suggestions from the community go through **in-app Submit** after sign-in. Moderators publish approved entries from `/moderation`.

Maintainers: see [CONTRIBUTING.md](CONTRIBUTING.md) for seed JSON format.

## License

This application is under the [MIT License](LICENSE). Catalog metadata is contributed by the community. Individual linked projects keep their own licenses.
