package com.portfinder.application.query;

import com.portfinder.domain.ProjectStatus;

public record CatalogSearchCriteria(
		String query,
		String originalPlatform,
		String targetPlatform,
		ProjectStatus status,
		String tag,
		CatalogSort sort) {

	public static CatalogSearchCriteria empty() {
		return new CatalogSearchCriteria(null, null, null, null, null, CatalogSort.UPDATED_DESC);
	}

	public CatalogSearchCriteria {
		sort = sort == null ? CatalogSort.UPDATED_DESC : sort;
		query = blankToNull(query);
		originalPlatform = blankToNull(originalPlatform);
		targetPlatform = blankToNull(targetPlatform);
		tag = blankToNull(tag);
	}

	private static String blankToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	public boolean hasQuery() {
		return query != null;
	}

	public boolean hasOriginalPlatform() {
		return originalPlatform != null;
	}

	public boolean hasTargetPlatform() {
		return targetPlatform != null;
	}

	public boolean hasStatus() {
		return status != null;
	}

	public boolean hasTag() {
		return tag != null;
	}
}
