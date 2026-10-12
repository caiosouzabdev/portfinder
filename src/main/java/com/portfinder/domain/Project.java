package com.portfinder.domain;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record Project(
		ProjectId id,
		String name,
		String summary,
		Platform originalPlatform,
		List<Platform> targetPlatforms,
		ProjectStatus status,
		Optional<URI> repoUrl,
		Optional<URI> homepageUrl,
		List<String> tags,
		LocalDate updatedAt) {

	public Project {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(summary, "summary");
		Objects.requireNonNull(originalPlatform, "originalPlatform");
		Objects.requireNonNull(targetPlatforms, "targetPlatforms");
		Objects.requireNonNull(status, "status");
		Objects.requireNonNull(repoUrl, "repoUrl");
		Objects.requireNonNull(homepageUrl, "homepageUrl");
		Objects.requireNonNull(tags, "tags");
		Objects.requireNonNull(updatedAt, "updatedAt");

		name = name.trim();
		summary = summary.trim();
		if (name.isEmpty()) {
			throw new IllegalArgumentException("name must not be blank");
		}
		if (summary.isEmpty()) {
			throw new IllegalArgumentException("summary must not be blank");
		}
		if (targetPlatforms.isEmpty()) {
			throw new IllegalArgumentException("targetPlatforms must not be empty");
		}

		targetPlatforms = List.copyOf(targetPlatforms);
		tags = tags.stream().map(String::trim).filter(t -> !t.isEmpty()).map(String::toLowerCase).distinct().toList();
	}

	public boolean hasTag(String tag) {
		if (tag == null || tag.isBlank()) {
			return false;
		}
		String needle = tag.trim().toLowerCase();
		return tags.contains(needle);
	}

	public boolean targets(String platformLabel) {
		return targetPlatforms.stream().anyMatch(p -> p.matchesIgnoreCase(platformLabel));
	}
}
