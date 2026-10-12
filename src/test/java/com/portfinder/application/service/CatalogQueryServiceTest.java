package com.portfinder.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.application.query.CatalogSearchCriteria;
import com.portfinder.application.query.CatalogSort;
import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.ProjectStatus;
import com.portfinder.domain.PublicationStatus;

class CatalogQueryServiceTest {

	private CatalogQueryService service;

	@BeforeEach
	void setUp() {
		List<Project> projects = List.of(
				project("bravo", "Bravo Port", LocalDate.of(2025, 1, 1), ProjectStatus.ACTIVE, 1),
				project("alpha", "Alpha Recomp", LocalDate.of(2026, 5, 1), ProjectStatus.WIP, 5),
				project("charlie", "Charlie Decomp", LocalDate.of(2026, 5, 1), ProjectStatus.ACTIVE, 5));
		ProjectCatalog catalog = new InMemoryProjectCatalog(projects);
		service = new CatalogQueryService(catalog, new ProjectFilter());
	}

	@Test
	void listsEmptyWhenCatalogEmpty() {
		var empty = new CatalogQueryService(new InMemoryProjectCatalog(List.of()), new ProjectFilter());
		assertTrue(empty.list(CatalogSearchCriteria.empty()).isEmpty());
	}

	@Test
	void sortsByVotesDescThenUpdatedThenName() {
		List<Project> listed = service.list(CatalogSearchCriteria.empty());
		assertEquals(List.of("alpha", "charlie", "bravo"), listed.stream().map(p -> p.id().value()).toList());
	}

	@Test
	void sortsByNameAsc() {
		var criteria = new CatalogSearchCriteria(null, null, null, null, null, null, CatalogSort.NAME_ASC, 0, 20);
		List<Project> listed = service.list(criteria);
		assertEquals(List.of("alpha", "bravo", "charlie"), listed.stream().map(p -> p.id().value()).toList());
	}

	@Test
	void filtersBeforeSort() {
		var criteria = new CatalogSearchCriteria(null, null, null, ProjectStatus.ACTIVE, null, null,
				CatalogSort.NAME_ASC, 0, 20);
		List<Project> listed = service.list(criteria);
		assertEquals(List.of("bravo", "charlie"), listed.stream().map(p -> p.id().value()).toList());
	}

	@Test
	void getByIdFindsNormalizedSlug() {
		assertTrue(service.getById("Alpha").isPresent());
		assertEquals("alpha", service.getById("Alpha").orElseThrow().id().value());
		assertTrue(service.getById("missing").isEmpty());
	}

	@Test
	void getByIdReturnsEmptyForInvalidSlug() {
		assertTrue(service.getById("!!!").isEmpty());
	}

	private static Project project(String id, String name, LocalDate updatedAt, ProjectStatus status, int votes) {
		return new Project(
				ProjectId.of(id),
				name,
				"Summary for " + name,
				"Summary for " + name,
				Platform.of("N64"),
				List.of(Platform.of("PC")),
				status,
				"n64",
				Optional.of(URI.create("https://example.com/" + id)),
				Optional.empty(),
				Optional.empty(),
				List.of(),
				List.of("sample"),
				updatedAt,
				false,
				Optional.empty(),
				PublicationStatus.PUBLISHED,
				votes,
				Optional.empty());
	}

	private static final class InMemoryProjectCatalog implements ProjectCatalog {

		private final List<Project> projects;

		InMemoryProjectCatalog(List<Project> projects) {
			this.projects = List.copyOf(projects);
		}

		@Override
		public List<Project> findAll() {
			return projects;
		}

		@Override
		public Optional<Project> findById(ProjectId id) {
			return projects.stream().filter(p -> p.id().equals(id)).findFirst();
		}
	}
}
