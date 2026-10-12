# Environment variables

Used when running PortFinder with persistence and Google sign-in.

| Variable | Required | Description |
|----------|----------|-------------|
| `DATABASE_URL` | Production | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/portfinder` |
| `DATABASE_USERNAME` | Production | Database user |
| `DATABASE_PASSWORD` | Production | Database password |
| `DATABASE_DRIVER` | Production | e.g. `org.postgresql.Driver` |
| `PORTFINDER_OAUTH_ENABLED` | Auth | Set `true` to enable Google sign-in |
| `GOOGLE_CLIENT_ID` | Auth | Google OAuth 2.0 client ID |
| `GOOGLE_CLIENT_SECRET` | Auth | Google OAuth 2.0 client secret |
| `PORTFINDER_ADMIN_EMAILS` | Moderation | Comma-separated emails allowed to moderate submissions |
| `PORTFINDER_BASE_URL` | Optional | Public site URL for sitemap links |

## Local defaults

Without these variables the app uses an in-memory H2 database and leaves Google OAuth disabled until credentials are set. See the README runbook for full setup.

Rate limits (optional overrides): `portfinder.rate-limit.votes-per-minute`, `portfinder.rate-limit.submits-per-hour`.
