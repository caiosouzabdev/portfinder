package com.portfinder.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Stable catalog slug. Normalized to lowercase kebab-case.
 */
public final class ProjectId {

	private static final Pattern VALID = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
	private static final int MAX_LENGTH = 64;

	private final String value;

	private ProjectId(String value) {
		this.value = value;
	}

	public static ProjectId of(String raw) {
		Objects.requireNonNull(raw, "id must not be null");
		String normalized = normalize(raw);
		if (normalized.isEmpty()) {
			throw new IllegalArgumentException("id must not be blank");
		}
		if (normalized.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("id must be at most " + MAX_LENGTH + " characters");
		}
		if (!VALID.matcher(normalized).matches()) {
			throw new IllegalArgumentException(
					"id must be lowercase kebab-case (letters, digits, single hyphens): " + raw);
		}
		return new ProjectId(normalized);
	}

	public static String normalize(String raw) {
		return raw.trim().toLowerCase().replace('_', '-').replaceAll("\\s+", "-");
	}

	public String value() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProjectId projectId)) {
			return false;
		}
		return value.equals(projectId.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return value;
	}
}
