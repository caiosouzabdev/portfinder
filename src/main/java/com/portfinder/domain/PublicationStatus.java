package com.portfinder.domain;

import java.util.Locale;

public enum PublicationStatus {
	PENDING,
	PUBLISHED,
	REJECTED;

	public static PublicationStatus from(String raw) {
		if (raw == null || raw.isBlank()) {
			return PUBLISHED;
		}
		return PublicationStatus.valueOf(raw.trim().toUpperCase(Locale.ROOT));
	}
}
