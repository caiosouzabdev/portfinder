# Contributing to portfinder

portfinder is a **discovery catalog** for decomp and recomp ports. Seed data lives as JSON in git; the running app may also accept in-app submissions for moderator review.

## Suggest a project

Prefer the in-app **Submit** form when the site is running with auth enabled. Otherwise open a GitHub Issue with the **Suggest a project** template.

## Add or update a catalog entry

1. Fork the repository.
2. Add or edit `src/main/resources/catalog/projects/{id}.json`.
3. Keep the filename identical to the `id` field.
4. Open a Pull Request using the PR template.

### Copy and branding

Do **not** name or compare PortFinder to other product directories in commits, code, comments, docs, issue text, or UI copy. Describe features in PortFinder terms only (ranked feed, upvote, category hub, and so on).

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
