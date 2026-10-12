package com.portfinder.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.application.query.CatalogSearchCriteria;
import com.portfinder.application.query.CatalogSort;
import com.portfinder.application.query.PagedProjects;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.PublicationStatus;

@Service
public class CatalogQueryService {

	private final ProjectCatalog catalog;
	private final ProjectFilter filter;

	public CatalogQueryService(ProjectCatalog catalog, ProjectFilter filter) {
		this.catalog = Objects.requireNonNull(catalog);
		this.filter = Objects.requireNonNull(filter);
	}

	public List<Project> list(CatalogSearchCriteria criteria) {
		return page(criteria).items();
	}

	public PagedProjects page(CatalogSearchCriteria criteria) {
		Objects.requireNonNull(criteria, "criteria");
		List<Project> matched = catalog.findAll().stream()
				.filter(project -> filter.matches(project, criteria))
				.sorted(comparatorFor(criteria.sort()))
				.toList();
		int from = Math.min(criteria.page() * criteria.pageSize(), matched.size());
		int to = Math.min(from + criteria.pageSize(), matched.size());
		return new PagedProjects(matched.subList(from, to), matched.size(), criteria.page(), criteria.pageSize());
	}

	public List<Project> featured(int limit) {
		return catalog.findAll().stream()
				.filter(Project::isPublished)
				.filter(Project::featured)
				.sorted(comparatorFor(CatalogSort.VOTES_DESC))
				.limit(Math.max(limit, 0))
				.toList();
	}

	public List<Project> related(Project project, int limit) {
		Objects.requireNonNull(project, "project");
		return catalog.findAll().stream()
				.filter(Project::isPublished)
				.filter(p -> !p.id().equals(project.id()))
				.filter(p -> p.category().equals(project.category())
						|| p.tags().stream().anyMatch(project::hasTag))
				.sorted(comparatorFor(CatalogSort.VOTES_DESC))
				.limit(Math.max(limit, 0))
				.toList();
	}

	public Optional<Project> getById(String id) {
		Objects.requireNonNull(id, "id");
		try {
			return catalog.findById(ProjectId.of(id)).filter(p -> p.publicationStatus() != PublicationStatus.REJECTED);
		}
		catch (IllegalArgumentException ex) {
			return Optional.empty();
		}
	}

	public Optional<Project> getPublishedById(String id) {
		return getById(id).filter(Project::isPublished);
	}

	private static Comparator<Project> comparatorFor(CatalogSort sort) {
		return switch (sort) {
			case NAME_ASC -> Comparator.comparing(Project::name, String.CASE_INSENSITIVE_ORDER)
					.thenComparing(p -> p.id().value());
			case UPDATED_DESC -> Comparator.comparing(Project::updatedAt).reversed()
					.thenComparing(Project::name, String.CASE_INSENSITIVE_ORDER);
			case VOTES_DESC -> Comparator.comparingInt(Project::voteCount).reversed()
					.thenComparing(Project::updatedAt, Comparator.reverseOrder())
					.thenComparing(Project::name, String.CASE_INSENSITIVE_ORDER);
		};
	}
}
