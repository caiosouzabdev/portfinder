package com.portfinder.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.application.query.CatalogSearchCriteria;
import com.portfinder.application.query.CatalogSort;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;

@Service
public class CatalogQueryService {

	private final ProjectCatalog catalog;
	private final ProjectFilter filter;

	public CatalogQueryService(ProjectCatalog catalog, ProjectFilter filter) {
		this.catalog = Objects.requireNonNull(catalog);
		this.filter = Objects.requireNonNull(filter);
	}

	public List<Project> list(CatalogSearchCriteria criteria) {
		Objects.requireNonNull(criteria, "criteria");
		return catalog.findAll().stream()
				.filter(project -> filter.matches(project, criteria))
				.sorted(comparatorFor(criteria.sort()))
				.toList();
	}

	public Optional<Project> getById(String id) {
		Objects.requireNonNull(id, "id");
		try {
			return catalog.findById(ProjectId.of(id));
		}
		catch (IllegalArgumentException ex) {
			return Optional.empty();
		}
	}

	private static Comparator<Project> comparatorFor(CatalogSort sort) {
		return switch (sort) {
			case NAME_ASC -> Comparator.comparing(Project::name, String.CASE_INSENSITIVE_ORDER)
					.thenComparing(p -> p.id().value());
			case UPDATED_DESC -> Comparator.comparing(Project::updatedAt).reversed()
					.thenComparing(Project::name, String.CASE_INSENSITIVE_ORDER);
		};
	}
}
