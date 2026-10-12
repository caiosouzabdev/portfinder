package com.portfinder.domain;

import java.util.Locale;
import java.util.Objects;

/**
 * First-class browse category identified by a kebab-case slug.
 */
public record Category(String slug, String label, String description) {

	public Category {
		Objects.requireNonNull(slug, "slug");
		Objects.requireNonNull(label, "label");
		Objects.requireNonNull(description, "description");
		slug = slug.trim().toLowerCase(Locale.ROOT);
		label = label.trim();
		description = description.trim();
		if (slug.isEmpty() || !slug.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
			throw new IllegalArgumentException("category slug must be kebab-case: " + slug);
		}
		if (label.isEmpty()) {
			throw new IllegalArgumentException("category label must not be blank");
		}
		if (description.isEmpty()) {
			throw new IllegalArgumentException("category description must not be blank");
		}
	}

	public boolean matches(String otherSlug) {
		return otherSlug != null && slug.equalsIgnoreCase(otherSlug.trim());
	}
}
