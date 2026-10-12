package com.portfinder.infrastructure.catalog;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.ProjectStatus;
import com.portfinder.domain.PublicationStatus;

final class ProjectDocumentMapper {

	Project toDomain(ProjectDocument document) {
		try {
			Platform original = Platform.of(document.originalPlatform());
			return new Project(
					ProjectId.of(document.id()),
					document.name(),
					document.summary(),
					document.description(),
					original,
					mapPlatforms(document.targetPlatforms()),
					ProjectStatus.from(document.status()),
					document.category(),
					parseUri(document.repoUrl()),
					parseUri(document.homepageUrl()),
					parseUri(document.imageUrl()),
					mapUris(document.screenshotUrls()),
					document.tags() == null ? List.of() : document.tags(),
					LocalDate.parse(document.updatedAt()),
					Boolean.TRUE.equals(document.featured()),
					parseDate(document.trendingUntil()),
					PublicationStatus.PUBLISHED,
					0,
					Optional.empty());
		}
		catch (RuntimeException ex) {
			throw new IllegalArgumentException("Invalid project document id=" + document.id() + ": " + ex.getMessage(),
					ex);
		}
	}

	private static List<Platform> mapPlatforms(List<String> labels) {
		if (labels == null) {
			return List.of();
		}
		return labels.stream().map(Platform::of).toList();
	}

	private static List<URI> mapUris(List<String> raw) {
		if (raw == null || raw.isEmpty()) {
			return List.of();
		}
		return raw.stream()
				.filter(s -> s != null && !s.isBlank())
				.map(s -> URI.create(s.trim()))
				.toList();
	}

	private static Optional<URI> parseUri(String raw) {
		if (raw == null || raw.isBlank()) {
			return Optional.empty();
		}
		return Optional.of(URI.create(raw.trim()));
	}

	private static Optional<LocalDate> parseDate(String raw) {
		if (raw == null || raw.isBlank()) {
			return Optional.empty();
		}
		return Optional.of(LocalDate.parse(raw.trim()));
	}
}
