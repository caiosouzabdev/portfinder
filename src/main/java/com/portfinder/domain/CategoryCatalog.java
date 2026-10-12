package com.portfinder.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Built-in category registry. Slugs match docs/CATEGORY_TAXONOMY.md.
 */
public final class CategoryCatalog {

	private static final Map<String, Category> BY_SLUG = new LinkedHashMap<>();

	static {
		register(new Category("n64", "Nintendo 64", "Decomps and recomps of N64 titles."));
		register(new Category("gba", "Game Boy Advance", "GBA recompilation and related ports."));
		register(new Category("game-boy", "Game Boy", "Game Boy and Game Boy Color projects."));
		register(new Category("xbox", "Xbox", "Original Xbox static recomps and ports."));
		register(new Category("xbox-360", "Xbox 360", "Xbox 360 recomps and ports."));
		register(new Category("psp", "PlayStation Portable", "PSP recompilation projects."));
	}

	private CategoryCatalog() {
	}

	private static void register(Category category) {
		BY_SLUG.put(category.slug(), category);
	}

	public static List<Category> all() {
		return List.copyOf(BY_SLUG.values());
	}

	public static Optional<Category> findBySlug(String slug) {
		if (slug == null || slug.isBlank()) {
			return Optional.empty();
		}
		return Optional.ofNullable(BY_SLUG.get(slug.trim().toLowerCase(Locale.ROOT)));
	}

	public static Category require(String slug) {
		return findBySlug(slug)
				.orElseThrow(() -> new IllegalArgumentException("Unknown category: " + slug));
	}

	public static String slugForOriginalPlatform(Platform platform) {
		String label = platform.label().trim().toLowerCase(Locale.ROOT);
		return switch (label) {
			case "n64" -> "n64";
			case "gba" -> "gba";
			case "game boy", "game boy color" -> "game-boy";
			case "xbox" -> "xbox";
			case "xbox 360" -> "xbox-360";
			case "psp" -> "psp";
			default -> throw new IllegalArgumentException(
					"No category mapping for original platform: " + platform.label());
		};
	}
}
