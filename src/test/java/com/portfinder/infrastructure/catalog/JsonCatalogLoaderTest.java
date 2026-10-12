package com.portfinder.infrastructure.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;

import tools.jackson.databind.ObjectMapper;

class JsonCatalogLoaderTest {

	private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
			new DefaultResourceLoader());
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void loadsValidFixtureProjects() {
		JsonCatalogLoader loader = new JsonCatalogLoader(resolver, objectMapper,
				"classpath:catalog-fixtures/valid/*.json");
		List<Project> projects = loader.loadAll();

		assertEquals(2, projects.size());
		assertTrue(projects.stream().anyMatch(p -> p.id().equals(ProjectId.of("alpha-recomp"))));
		assertEquals("Alpha Recomp",
				projects.stream().filter(p -> p.id().equals(ProjectId.of("alpha-recomp"))).findFirst().orElseThrow()
						.name());
	}

	@Test
	void failsFastOnDuplicateIds() {
		JsonCatalogLoader loader = new JsonCatalogLoader(resolver, objectMapper,
				"classpath:catalog-fixtures/duplicate/*.json");
		IllegalStateException ex = assertThrows(IllegalStateException.class, loader::loadAll);
		assertTrue(ex.getMessage().contains("Duplicate project id"));
	}

	@Test
	void failsFastOnInvalidDocument() {
		JsonCatalogLoader loader = new JsonCatalogLoader(resolver, objectMapper,
				"classpath:catalog-fixtures/invalid/*.json");
		assertThrows(RuntimeException.class, loader::loadAll);
	}
}
