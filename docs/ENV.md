# Environment variables

Used when running PortFinder with persistence and (later) Google sign-in.

| Variable | Required | Description |
|----------|----------|-------------|
| `DATABASE_URL` | Production | JDBC URL (`jdbc:postgresql://...`) or platform URL (`postgres://user:pass@host/db`). Libpq-style URLs are normalized automatically. |
| `DATABASE_USERNAME` | Local JDBC | Database user (optional when user is embedded in `DATABASE_URL`) |
| `DATABASE_PASSWORD` | Local JDBC | Database password (optional when password is embedded in `DATABASE_URL`) |
| `DATABASE_DRIVER` | Local JDBC | e.g. `org.postgresql.Driver` (set automatically for libpq-style URLs) |
| `PORT` | Cloud | HTTP listen port (defaults to `8080`) |
| `PORTFINDER_OAUTH_ENABLED` | Auth | Set `true` to enable Google sign-in (v1 launch keeps this `false`) |
| `GOOGLE_CLIENT_ID` | Auth | Google OAuth 2.0 client ID |
| `GOOGLE_CLIENT_SECRET` | Auth | Google OAuth 2.0 client secret |
| `PORTFINDER_ADMIN_EMAILS` | Moderation | Comma-separated emails allowed to moderate submissions (default: `portsfinder@gmail.com`) |
| `PORTFINDER_BASE_URL` | Optional | Public site URL for sitemap links (falls back to `RENDER_EXTERNAL_URL` on Render) |

## Local defaults

Without these variables the app uses an in-memory H2 database and leaves Google OAuth disabled until credentials are set. See the README for local and Render deploy steps.

Rate limits (optional overrides): `portfinder.rate-limit.votes-per-minute`, `portfinder.rate-limit.submits-per-hour`.
