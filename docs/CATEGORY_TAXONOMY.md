# Category taxonomy

Each catalog project has exactly one `category` slug, derived from the original console platform.

| Slug | Label | Description |
|------|-------|-------------|
| `n64` | Nintendo 64 | Decomps and recomps of N64 titles |
| `gba` | Game Boy Advance | GBA recompilation and related ports |
| `game-boy` | Game Boy | Game Boy and Game Boy Color projects |
| `xbox` | Xbox | Original Xbox static recomps and ports |
| `xbox-360` | Xbox 360 | Xbox 360 recomps and ports |
| `psp` | PlayStation Portable | PSP recompilation projects |

## Assignment rule

Map `originalPlatform` → category:

- `N64` → `n64`
- `GBA` → `gba`
- `Game Boy`, `Game Boy Color` → `game-boy`
- `Xbox` → `xbox`
- `Xbox 360` → `xbox-360`
- `PSP` → `psp`

Unknown platforms require a new category entry before catalog acceptance.
