# Contributing to portfinder

portfinder is a **discovery catalog** for decomp and recomp ports. The source repository is private. Public contributions go through the **in-app Submit** form after sign-in; moderators publish approved entries.

## Submit a project (public)

1. Sign in on the live site: https://portfinder.onrender.com/
2. Open **submit** in the nav (or go to `/submit`).
3. Fill the form. Entries stay pending until a moderator publishes them.

Do **not** open GitHub issues or PRs for catalog suggestions — there is no `suggest-project` issue template. Submissions are in-app only. Old `/contribute` URLs redirect to `/submit`.

## Seed catalog (maintainers)

Maintainers may still edit seed JSON under `src/main/resources/catalog/projects/{id}.json` (filename must match `id`).

### Copy and branding

Do **not** name or compare PortFinder to other product directories in commits, code, comments, docs, or UI copy. Describe features in PortFinder terms only (ranked feed, upvote, category hub, and so on).

### JSON schema

```json
{
  "id": "example-recomp",
  "name": "Example Recompiled",
  "summary": "Short tagline for list cards.",
  "description": "Longer detail-page description. May match summary.",
  "originalPlatform": "N64",
  "targetPlatforms": ["PC"],
  "category": "n64",
  "status": "ACTIVE",
  "repoUrl": "https://github.com/org/repo",
  "homepageUrl": "",
  "imageUrl": "https://raw.githubusercontent.com/org/repo/main/docs/social-preview.png",
  "screenshotUrls": [],
  "tags": ["recomp", "n64"],
  "featured": false,
  "updatedAt": "2026-10-12"
}
```

### Rules

- `id`: lowercase kebab-case, max 64 characters, unique.
- `status`: `ACTIVE` | `WIP` | `ARCHIVED` | `UNKNOWN`.
- `category`: one of the slugs in [docs/CATEGORY_TAXONOMY.md](docs/CATEGORY_TAXONOMY.md).
- `targetPlatforms`: at least one value.
- `imageUrl`: optional preview image (project screenshot/social card). Prefer images hosted by the project repo.
- `featured`: optional; highlights the project on the home feed.
- Metadata and outbound links only — **never** ROMs, ISOs, or game assets.

## Local development

Requires **JDK 21**.

```bash
./mvnw test
./mvnw spring-boot:run
```

Open http://localhost:8080
