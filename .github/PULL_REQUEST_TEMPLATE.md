## Summary
Briefly describe the catalog change.

## Checklist
- [ ] Added or updated `src/main/resources/catalog/projects/{id}.json`
- [ ] `id` is unique lowercase kebab-case and matches the filename
- [ ] JSON is valid and includes required fields (`name`, `summary`, `originalPlatform`, `targetPlatforms`, `category`, `status`, `updatedAt`)
- [ ] Category slug matches [docs/CATEGORY_TAXONOMY.md](../docs/CATEGORY_TAXONOMY.md)
- [ ] Links point to official project repos/homepages only
- [ ] No ROM / ISO / game-asset links
- [ ] Status is one of `ACTIVE`, `WIP`, `ARCHIVED`, `UNKNOWN`
- [ ] Commit messages and PR text do not name third-party directory sites
