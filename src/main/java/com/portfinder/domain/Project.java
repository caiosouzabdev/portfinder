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
		String description,
		Platform originalPlatform,
		List<Platform> targetPlatforms,
		ProjectStatus status,
		String category,
		Optional<URI> repoUrl,
		Optional<URI> homepageUrl,
		Optional<URI> imageUrl,
		List<URI> screenshotUrls,
		List<String> tags,
		LocalDate updatedAt,
		boolean featured,
		Optional<LocalDate> trendingUntil,
		PublicationStatus publicationStatus,
		int voteCount,
		Optional<String> submittedByDisplayName) {

	public Project {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(summary, "summary");
		Objects.requireNonNull(originalPlatform, "originalPlatform");
		Objects.requireNonNull(targetPlatforms, "targetPlatforms");
		Objects.requireNonNull(status, "status");
		Objects.requireNonNull(repoUrl, "repoUrl");
		Objects.requireNonNull(homepageUrl, "homepageUrl");
		Objects.requireNonNull(imageUrl, "imageUrl");
		Objects.requireNonNull(screenshotUrls, "screenshotUrls");
		Objects.requireNonNull(tags, "tags");
		Objects.requireNonNull(updatedAt, "updatedAt");
		Objects.requireNonNull(trendingUntil, "trendingUntil");
		Objects.requireNonNull(publicationStatus, "publicationStatus");
		Objects.requireNonNull(submittedByDisplayName, "submittedByDisplayName");

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

		if (description == null || description.isBlank()) {
			description = summary;
		}
		else {
			description = description.trim();
		}

		if (category == null || category.isBlank()) {
			category = CategoryCatalog.slugForOriginalPlatform(originalPlatform);
		}
		else {
			category = CategoryCatalog.require(category).slug();
		}

		if (voteCount < 0) {
			throw new IllegalArgumentException("voteCount must not be negative");
		}

		targetPlatforms = List.copyOf(targetPlatforms);
		screenshotUrls = List.copyOf(screenshotUrls);
		tags = tags.stream().map(String::trim).filter(t -> !t.isEmpty()).map(String::toLowerCase).distinct().toList();
		submittedByDisplayName = submittedByDisplayName.map(String::trim).filter(s -> !s.isEmpty());
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

	public boolean hasImage() {
		return imageUrl.isPresent();
	}

	public boolean hasScreenshots() {
		return !screenshotUrls.isEmpty();
	}

	public boolean isPublished() {
		return publicationStatus == PublicationStatus.PUBLISHED;
	}

	public boolean isTrending(LocalDate today) {
		return trendingUntil.filter(until -> !until.isBefore(today)).isPresent();
	}

	public boolean matchesCategory(String slug) {
		return category.equalsIgnoreCase(slug == null ? "" : slug.trim());
	}

	public Project withVoteCount(int newVoteCount) {
		return new Project(
				id,
				name,
				summary,
				description,
				originalPlatform,
				targetPlatforms,
				status,
				category,
				repoUrl,
				homepageUrl,
				imageUrl,
				screenshotUrls,
				tags,
				updatedAt,
				featured,
				trendingUntil,
				publicationStatus,
				newVoteCount,
				submittedByDisplayName);
	}

	public Project withPublicationStatus(PublicationStatus newStatus) {
		return new Project(
				id,
				name,
				summary,
				description,
				originalPlatform,
				targetPlatforms,
				status,
				category,
				repoUrl,
				homepageUrl,
				imageUrl,
				screenshotUrls,
				tags,
				updatedAt,
				featured,
				trendingUntil,
				newStatus,
				voteCount,
				submittedByDisplayName);
	}
}
