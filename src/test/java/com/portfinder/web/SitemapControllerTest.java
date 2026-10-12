package com.portfinder.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.ProjectStatus;
import com.portfinder.domain.PublicationStatus;

class SitemapControllerTest {

	@Test
	void sitemapReturnsXmlWithHomeAndProjects() {
		ProjectCatalog catalog = new ProjectCatalog() {
			@Override
			public List<Project> findAll() {
				return List.of(sample("alpha-recomp", PublicationStatus.PUBLISHED),
						sample("pending-port", PublicationStatus.PENDING));
			}

			@Override
			public Optional<Project> findById(ProjectId id) {
				return findAll().stream().filter(p -> p.id().equals(id)).findFirst();
			}
		};
		SitemapController controller = new SitemapController(catalog, "https://portfinder.onrender.com/");

		ResponseEntity<String> response = controller.sitemap();
		assertEquals(200, response.getStatusCode().value());
		assertTrue(String.valueOf(response.getHeaders().getContentType()).contains("application/xml"));

		String body = response.getBody();
		assertTrue(body.contains("<urlset"));
		assertTrue(body.contains("<loc>https://portfinder.onrender.com/</loc>"));
		assertTrue(body.contains("<loc>https://portfinder.onrender.com/categories</loc>"));
		assertTrue(body.contains("<loc>https://portfinder.onrender.com/projects/alpha-recomp</loc>"));
		assertTrue(body.contains("/privacy"));
		assertTrue(!body.contains("pending-port"));
	}

	@Test
	void normalizeBaseUrlStripsTrailingSlash() {
		assertEquals("https://portfinder.onrender.com",
				SitemapController.normalizeBaseUrl("https://portfinder.onrender.com/"));
		assertEquals("http://localhost:8080", SitemapController.normalizeBaseUrl("  "));
	}

	@Test
	void escapeXmlEncodesSpecialCharacters() {
		assertEquals("a&amp;b&lt;c&gt;&quot;d&apos;", SitemapController.escapeXml("a&b<c>\"d'"));
	}

	private static Project sample(String id, PublicationStatus publicationStatus) {
		return new Project(
				ProjectId.of(id),
				"Sample",
				"Summary",
				"Description",
				Platform.of("N64"),
				List.of(Platform.of("PC")),
				ProjectStatus.ACTIVE,
				"n64",
				Optional.of(URI.create("https://example.com/repo")),
				Optional.empty(),
				Optional.empty(),
				List.of(),
				List.of("n64"),
				LocalDate.of(2026, 10, 12),
				false,
				Optional.empty(),
				publicationStatus,
				0,
				Optional.empty());
	}
}
