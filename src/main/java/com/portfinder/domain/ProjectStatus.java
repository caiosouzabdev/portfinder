package com.portfinder.domain;

import java.util.Locale;

public enum ProjectStatus {
	ACTIVE,
	WIP,
	ARCHIVED,
	UNKNOWN;

	public static ProjectStatus from(String raw) {
		if (raw == null || raw.isBlank()) {
			return UNKNOWN;
		}
		try {
			return ProjectStatus.valueOf(raw.trim().toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Unknown project status: " + raw);
		}
	}
}
