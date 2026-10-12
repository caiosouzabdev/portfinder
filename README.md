# portfinder

A curated hub for discovering **game decompilation** and **recompilation** port projects.

Browse and filter by original platform, target platform, status, and tags. The catalog lives in git; the community contributes through GitHub Issues and Pull Requests.

**Policy:** metadata and links only. This project never hosts ROMs, ISOs, or copyrighted game assets.

## Stack

- Java 21
- Spring Boot 4
- Thymeleaf + static CSS
- JSON files on the classpath as the catalog source of truth

## Architecture

Hexagonal layout with SOLID applied pragmatically:

| Layer | Package | Role |
|-------|---------|------|
| Domain | `com.portfinder.domain` | `Project`, `ProjectId`, platforms, status |
| Application | `com.portfinder.application` | `ProjectCatalog` port, `ProjectFilter`, `CatalogQueryService` |
| Infrastructure | `com.portfinder.infrastructure.catalog` | `JsonProjectCatalog` |
| Web | `com.portfinder.web` | Controllers + Thymeleaf views |

## Run locally

Requires JDK 21.

```bash
./mvnw test
./mvnw spring-boot:run
```

App: http://localhost:8080

## Contribute

See [CONTRIBUTING.md](CONTRIBUTING.md).

- Suggest: open an Issue with the suggest-project template
- Add/edit: PR a file under `src/main/resources/catalog/projects/`

## License

Catalog metadata is contributed by the community. Individual linked projects keep their own licenses.
