package com.portfinder.domain;

import java.util.Objects;

/**
 * Platform label as free text (e.g. N64, PC, PS1) with case-insensitive equality for filters.
 */
public final class Platform {

	private final String label;

	private Platform(String label) {
		this.label = label;
	}

	public static Platform of(String raw) {
		Objects.requireNonNull(raw, "platform must not be null");
		String normalized = raw.trim();
		if (normalized.isEmpty()) {
			throw new IllegalArgumentException("platform must not be blank");
		}
		return new Platform(normalized);
	}

	public String label() {
		return label;
	}

	public boolean matchesIgnoreCase(String other) {
		return other != null && label.equalsIgnoreCase(other.trim());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Platform platform)) {
			return false;
		}
		return label.equalsIgnoreCase(platform.label);
	}

	@Override
	public int hashCode() {
		return label.toLowerCase().hashCode();
	}

	@Override
	public String toString() {
		return label;
	}
}
