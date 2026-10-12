package com.portfinder.application.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.portfinder.application.query.CatalogSearchCriteria;
import com.portfinder.application.query.CatalogSort;
import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.ProjectStatus;
import com.portfinder.domain.PublicationStatus;

class ProjectFilterTest {

	private ProjectFilter filter;
	private Project zelda;
	private Project mario;

	@BeforeEach
	void setUp() {
		filter = new ProjectFilter();
		zelda = project("zelda-recomp", "Zelda64 Recompiled", "N64 to PC recomp", "N64",
				List.of("PC"), ProjectStatus.ACTIVE, List.of("recomp", "zelda"), LocalDate.of(2026, 3, 1));
		mario = project("sm64-port", "SM64 Port", "Classic port work", "N64",
				List.of("PC", "Dreamcast"), ProjectStatus.WIP, List.of("port"), LocalDate.of(2025, 6, 1));
	}

	@Test
	void emptyCriteriaMatchesAll() {
		assertTrue(filter.matches(zelda, CatalogSearchCriteria.empty()));
		assertTrue(filter.matches(mario, CatalogSearchCriteria.empty()));
	}

	@Test
	void filtersByOriginalPlatform() {
		var criteria = new CatalogSearchCriteria(null, "n64", null, null, null, null, CatalogSort.UPDATED_DESC, 0, 20);
		assertTrue(filter.matches(zelda, criteria));
	}

	@Test
	void filtersByCategory() {
		var criteria = new CatalogSearchCriteria(null, null, null, null, null, "n64", CatalogSort.UPDATED_DESC, 0, 20);
		assertTrue(filter.matches(zelda, criteria));
	}

	@Test
	void filtersByTargetPlatform() {
		var criteria = new CatalogSearchCriteria(null, null, "dreamcast", null, null, null, CatalogSort.UPDATED_DESC, 0,
				20);
		assertFalse(filter.matches(zelda, criteria));
		assertTrue(filter.matches(mario, criteria));
	}

	@Test
	void filtersByStatus() {
		var criteria = new CatalogSearchCriteria(null, null, null, ProjectStatus.WIP, null, null,
				CatalogSort.UPDATED_DESC, 0, 20);
		assertFalse(filter.matches(zelda, criteria));
		assertTrue(filter.matches(mario, criteria));
	}

	@Test
	void filtersByTag() {
		var criteria = new CatalogSearchCriteria(null, null, null, null, "recomp", null, CatalogSort.UPDATED_DESC, 0,
				20);
		assertTrue(filter.matches(zelda, criteria));
		assertFalse(filter.matches(mario, criteria));
	}

	@Test
	void queryMatchesNameSummaryTagsAndPlatforms() {
		assertTrue(filter.matches(zelda, criteriaQuery("zelda64")));
		assertTrue(filter.matches(zelda, criteriaQuery("RECOMP")));
		assertTrue(filter.matches(mario, criteriaQuery("dreamcast")));
		assertFalse(filter.matches(mario, criteriaQuery("zelda")));
	}

	@Test
	void combinedFiltersRequireAllConditions() {
		var criteria = new CatalogSearchCriteria("port", "N64", "PC", ProjectStatus.WIP, "port", null,
				CatalogSort.UPDATED_DESC, 0, 20);
		assertTrue(filter.matches(mario, criteria));
		assertFalse(filter.matches(zelda, criteria));
	}

	private static CatalogSearchCriteria criteriaQuery(String q) {
		return new CatalogSearchCriteria(q, null, null, null, null, null, CatalogSort.UPDATED_DESC, 0, 20);
	}

	private static Project project(String id, String name, String summary, String original, List<String> targets,
			ProjectStatus status, List<String> tags, LocalDate updatedAt) {
		return new Project(
				ProjectId.of(id),
				name,
				summary,
				summary,
				Platform.of(original),
				targets.stream().map(Platform::of).toList(),
				status,
				null,
				Optional.of(URI.create("https://example.com/" + id)),
				Optional.empty(),
				Optional.empty(),
				List.of(),
				tags,
				updatedAt,
				false,
				Optional.empty(),
				PublicationStatus.PUBLISHED,
				0,
				Optional.empty());
	}
}
