# Contributing to portfinder

portfinder is a **read-only catalog**. The source of truth is JSON files in git.

## Suggest a project

Open a GitHub Issue with the **Suggest a project** template.

## Add or update a catalog entry

1. Fork the repository.
2. Add or edit `src/main/resources/catalog/projects/{id}.json`.
3. Keep the filename identical to the `id` field.
4. Open a Pull Request using the PR template.

### JSON schema

```json
{
  "id": "example-recomp",
  "name": "Example Recompiled",
  "summary": "Short description of the project.",
  "originalPlatform": "N64",
  "targetPlatforms": ["PC"],
  "status": "ACTIVE",
  "repoUrl": "https://github.com/org/repo",
  "homepageUrl": "",
  "tags": ["recomp", "n64"],
  "updatedAt": "2026-10-12"
}
```

### Rules

- `id`: lowercase kebab-case, max 64 characters, unique.
- `status`: `ACTIVE` | `WIP` | `ARCHIVED` | `UNKNOWN`.
- `targetPlatforms`: at least one value.
- Metadata and outbound links only — **never** ROMs, ISOs, or game assets.

## Local development

Requires **JDK 21**.

```bash
./mvnw test
./mvnw spring-boot:run
```

Open http://localhost:8080
