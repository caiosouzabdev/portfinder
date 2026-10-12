package com.portfinder.application.query;

import com.portfinder.domain.ProjectStatus;

public record CatalogSearchCriteria(
		String query,
		String originalPlatform,
		String targetPlatform,
		ProjectStatus status,
		String tag,
		String category,
		CatalogSort sort,
		int page,
		int pageSize) {

	public static final int DEFAULT_PAGE_SIZE = 20;

	public static CatalogSearchCriteria empty() {
		return new CatalogSearchCriteria(null, null, null, null, null, null, CatalogSort.VOTES_DESC, 0,
				DEFAULT_PAGE_SIZE);
	}

	public CatalogSearchCriteria {
		sort = sort == null ? CatalogSort.VOTES_DESC : sort;
		query = blankToNull(query);
		originalPlatform = blankToNull(originalPlatform);
		targetPlatform = blankToNull(targetPlatform);
		tag = blankToNull(tag);
		category = blankToNull(category);
		if (page < 0) {
			page = 0;
		}
		if (pageSize < 1) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		if (pageSize > 100) {
			pageSize = 100;
		}
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

	public boolean hasCategory() {
		return category != null;
	}
}
