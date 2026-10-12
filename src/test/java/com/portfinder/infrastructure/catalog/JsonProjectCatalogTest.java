package com.portfinder.infrastructure.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import tools.jackson.databind.ObjectMapper;
import com.portfinder.domain.ProjectId;

class JsonProjectCatalogTest {

	private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
			new DefaultResourceLoader());
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void loadsValidFixtureProjects() {
		JsonProjectCatalog catalog = new JsonProjectCatalog(resolver, objectMapper,
				"classpath:catalog-fixtures/valid/*.json");
		catalog.reloadForTests();

		assertEquals(2, catalog.findAll().size());
		assertTrue(catalog.findById(ProjectId.of("alpha-recomp")).isPresent());
		assertEquals("Alpha Recomp", catalog.findById(ProjectId.of("alpha-recomp")).orElseThrow().name());
	}

	@Test
	void failsFastOnDuplicateIds() {
		JsonProjectCatalog catalog = new JsonProjectCatalog(resolver, objectMapper,
				"classpath:catalog-fixtures/duplicate/*.json");
		IllegalStateException ex = assertThrows(IllegalStateException.class, catalog::reloadForTests);
		assertTrue(ex.getMessage().contains("Duplicate project id"));
	}

	@Test
	void failsFastOnInvalidDocument() {
		JsonProjectCatalog catalog = new JsonProjectCatalog(resolver, objectMapper,
				"classpath:catalog-fixtures/invalid/*.json");
		assertThrows(IllegalStateException.class, catalog::reloadForTests);
	}
}
