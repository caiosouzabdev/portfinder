package com.portfinder.infrastructure.persistence;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.ProjectStatus;
import com.portfinder.domain.PublicationStatus;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProjectEntityMapper {

	private final ObjectMapper objectMapper;

	public ProjectEntityMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public Project toDomain(ProjectEntity entity) {
		return new Project(
				ProjectId.of(entity.getId()),
				entity.getName(),
				entity.getSummary(),
				entity.getDescription(),
				Platform.of(entity.getOriginalPlatform()),
				readStringList(entity.getTargetPlatformsJson()).stream().map(Platform::of).toList(),
				ProjectStatus.from(entity.getStatus()),
				entity.getCategory(),
				parseUri(entity.getRepoUrl()),
				parseUri(entity.getHomepageUrl()),
				parseUri(entity.getImageUrl()),
				readStringList(entity.getScreenshotUrlsJson()).stream().map(URI::create).toList(),
				readStringList(entity.getTagsJson()),
				entity.getUpdatedAt(),
				entity.isFeatured(),
				Optional.ofNullable(entity.getTrendingUntil()),
				PublicationStatus.from(entity.getPublicationStatus()),
				entity.getVoteCount(),
				Optional.ofNullable(entity.getSubmittedByDisplayName()));
	}

	public ProjectEntity toEntity(Project project) {
		ProjectEntity entity = new ProjectEntity();
		apply(project, entity);
		return entity;
	}

	public void apply(Project project, ProjectEntity entity) {
		entity.setId(project.id().value());
		entity.setName(project.name());
		entity.setSummary(project.summary());
		entity.setDescription(project.description());
		entity.setOriginalPlatform(project.originalPlatform().label());
		entity.setTargetPlatformsJson(writeStringList(project.targetPlatforms().stream().map(Platform::label).toList()));
		entity.setCategory(project.category());
		entity.setStatus(project.status().name());
		entity.setRepoUrl(project.repoUrl().map(URI::toString).orElse(null));
		entity.setHomepageUrl(project.homepageUrl().map(URI::toString).orElse(null));
		entity.setImageUrl(project.imageUrl().map(URI::toString).orElse(null));
		entity.setScreenshotUrlsJson(writeStringList(project.screenshotUrls().stream().map(URI::toString).toList()));
		entity.setTagsJson(writeStringList(project.tags()));
		entity.setUpdatedAt(project.updatedAt());
		entity.setFeatured(project.featured());
		entity.setTrendingUntil(project.trendingUntil().orElse(null));
		entity.setPublicationStatus(project.publicationStatus().name());
		entity.setVoteCount(project.voteCount());
		entity.setSubmittedByDisplayName(project.submittedByDisplayName().orElse(null));
	}

	private List<String> readStringList(String json) {
		if (json == null || json.isBlank()) {
			return List.of();
		}
		try {
			return objectMapper.readValue(json, new TypeReference<List<String>>() {
			});
		}
		catch (JacksonException ex) {
			// tolerate comma-separated legacy values
			return Arrays.stream(json.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
		}
	}

	private String writeStringList(List<String> values) {
		try {
			return objectMapper.writeValueAsString(values == null ? List.of() : values);
		}
		catch (JacksonException ex) {
			throw new IllegalStateException("Failed to serialize JSON list", ex);
		}
	}

	private static Optional<URI> parseUri(String raw) {
		if (raw == null || raw.isBlank()) {
			return Optional.empty();
		}
		return Optional.of(URI.create(raw.trim()));
	}
}
