package com.portfinder.infrastructure.catalog;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.ProjectStatus;

final class ProjectDocumentMapper {

	Project toDomain(ProjectDocument document) {
		try {
			return new Project(
					ProjectId.of(document.id()),
					document.name(),
					document.summary(),
					Platform.of(document.originalPlatform()),
					mapPlatforms(document.targetPlatforms()),
					ProjectStatus.from(document.status()),
					parseUri(document.repoUrl()),
					parseUri(document.homepageUrl()),
					document.tags() == null ? List.of() : document.tags(),
					LocalDate.parse(document.updatedAt()));
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

	private static Optional<URI> parseUri(String raw) {
		if (raw == null || raw.isBlank()) {
			return Optional.empty();
		}
		return Optional.of(URI.create(raw.trim()));
	}
}
