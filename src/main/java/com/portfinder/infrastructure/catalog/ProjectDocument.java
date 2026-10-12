package com.portfinder.infrastructure.catalog;

import java.util.List;

/**
 * Jackson DTO for catalog JSON files. Kept inside infrastructure.
 */
record ProjectDocument(
		String id,
		String name,
		String summary,
		String originalPlatform,
		List<String> targetPlatforms,
		String status,
		String repoUrl,
		String homepageUrl,
		List<String> tags,
		String updatedAt) {
}
