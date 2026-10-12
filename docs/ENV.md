# Environment variables

Used when running PortFinder with persistence and Google sign-in.

| Variable | Required | Description |
|----------|----------|-------------|
| `DATABASE_URL` | Production | JDBC URL (`jdbc:postgresql://...`) or platform URL (`postgres://user:pass@host/db`). Libpq-style URLs are normalized automatically. |
| `DATABASE_USERNAME` | Local JDBC | Database user (optional when user is embedded in `DATABASE_URL`) |
| `DATABASE_PASSWORD` | Local JDBC | Database password (optional when password is embedded in `DATABASE_URL`) |
| `DATABASE_DRIVER` | Local JDBC | e.g. `org.postgresql.Driver` (set automatically for libpq-style URLs) |
| `PORT` | Cloud | HTTP listen port (defaults to `8080`) |
| `PORTFINDER_OAUTH_ENABLED` | Auth | Set `true` to enable Google sign-in (`render.yaml` sets this for production) |
| `GOOGLE_CLIENT_ID` | Auth | Google OAuth 2.0 client ID |
| `GOOGLE_CLIENT_SECRET` | Auth | Google OAuth 2.0 client secret |
| `PORTFINDER_ADMIN_EMAILS` | Moderation | Comma-separated emails allowed to moderate submissions (default: `portsfinder@gmail.com`) |
| `PORTFINDER_BASE_URL` | Optional | Public site URL for sitemap `<loc>` values (falls back to `RENDER_EXTERNAL_URL` on Render) |
| `THYMELEAF_CACHE` | Optional | Template cache (`true` by default; set `false` for local template iteration) |

Google Cloud Console must list redirect URI `https://<public-host>/login/oauth2/code/google` (plus `http://localhost:8080/login/oauth2/code/google` for local). Render terminates TLS; the app uses `server.forward-headers-strategy=framework` so the OAuth callback is built with `https` and the public host.

Public discoverability endpoints: `/sitemap.xml`, `/robots.txt`, `/health`.

## Local defaults

Without these variables the app uses an in-memory H2 database and leaves Google OAuth disabled until credentials are set. See the README for local and Render deploy steps.

Rate limits (optional overrides): `portfinder.rate-limit.votes-per-minute`, `portfinder.rate-limit.submits-per-hour`.
