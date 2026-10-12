package com.portfinder.infrastructure.catalog;

import java.util.List;

/**
 * Jackson DTO for catalog JSON files. Kept inside infrastructure.
 */
record ProjectDocument(
		String id,
		String name,
		String summary,
		String description,
		String originalPlatform,
		List<String> targetPlatforms,
		String category,
		String status,
		String repoUrl,
		String homepageUrl,
		String imageUrl,
		List<String> screenshotUrls,
		List<String> tags,
		Boolean featured,
		String trendingUntil,
		String updatedAt) {
}
