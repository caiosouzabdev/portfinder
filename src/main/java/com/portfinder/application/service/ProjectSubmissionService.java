package com.portfinder.application.service;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfinder.domain.CategoryCatalog;
import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.ProjectStatus;
import com.portfinder.domain.PublicationStatus;
import com.portfinder.infrastructure.persistence.JpaProjectCatalog;
import com.portfinder.infrastructure.persistence.ProjectEntity;
import com.portfinder.infrastructure.persistence.ProjectEntityMapper;
import com.portfinder.infrastructure.persistence.ProjectJpaRepository;
import com.portfinder.infrastructure.persistence.UserEntity;

@Service
public class ProjectSubmissionService {

	private final JpaProjectCatalog catalog;
	private final ProjectJpaRepository projectRepository;
	private final ProjectEntityMapper mapper;

	public ProjectSubmissionService(JpaProjectCatalog catalog, ProjectJpaRepository projectRepository,
			ProjectEntityMapper mapper) {
		this.catalog = catalog;
		this.projectRepository = projectRepository;
		this.mapper = mapper;
	}

	@Transactional
	public Project submit(SubmitCommand command, UserEntity submitter) {
		Objects.requireNonNull(command, "command");
		Objects.requireNonNull(submitter, "submitter");
		ProjectId id = ProjectId.of(command.id());
		if (catalog.findById(id).isPresent()) {
			throw new IllegalArgumentException("Project id already exists: " + id.value());
		}

		String category = command.category();
		if (category == null || category.isBlank()) {
			category = CategoryCatalog.slugForOriginalPlatform(Platform.of(command.originalPlatform()));
		}
		else {
			CategoryCatalog.require(category);
		}

		Project project = new Project(
				id,
				command.name(),
				command.summary(),
				command.description(),
				Platform.of(command.originalPlatform()),
				splitPlatforms(command.targetPlatforms()),
				ProjectStatus.from(command.status()),
				category,
				parseUri(command.repoUrl()),
				parseUri(command.homepageUrl()),
				parseUri(command.imageUrl()),
				List.of(),
				splitCsv(command.tags()),
				LocalDate.now(),
				false,
				Optional.empty(),
				PublicationStatus.PENDING,
				0,
				Optional.of(submitter.getDisplayName()));

		ProjectEntity entity = mapper.toEntity(project);
		entity.setSubmittedByUserId(submitter.getId());
		entity.setSubmittedByDisplayName(submitter.getDisplayName());
		return mapper.toDomain(projectRepository.save(entity));
	}

	@Transactional
	public Project moderate(String projectId, PublicationStatus status) {
		Project project = catalog.findById(ProjectId.of(projectId))
				.orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
		if (status != PublicationStatus.PUBLISHED && status != PublicationStatus.REJECTED) {
			throw new IllegalArgumentException("Moderation must publish or reject");
		}
		return catalog.save(project.withPublicationStatus(status));
	}

	@Transactional(readOnly = true)
	public List<Project> pending() {
		return catalog.findAll().stream()
				.filter(p -> p.publicationStatus() == PublicationStatus.PENDING)
				.toList();
	}

	private static List<Platform> splitPlatforms(String raw) {
		return splitCsv(raw).stream().map(Platform::of).toList();
	}

	private static List<String> splitCsv(String raw) {
		if (raw == null || raw.isBlank()) {
			return List.of();
		}
		return Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
	}

	private static Optional<URI> parseUri(String raw) {
		if (raw == null || raw.isBlank()) {
			return Optional.empty();
		}
		return Optional.of(URI.create(raw.trim()));
	}

	public record SubmitCommand(
			String id,
			String name,
			String summary,
			String description,
			String originalPlatform,
			String targetPlatforms,
			String category,
			String status,
			String repoUrl,
			String homepageUrl,
			String imageUrl,
			String tags) {
	}
}
