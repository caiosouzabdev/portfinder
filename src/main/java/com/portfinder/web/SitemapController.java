package com.portfinder.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.domain.CategoryCatalog;
import com.portfinder.domain.Project;

@Controller
public class SitemapController {

	private static final MediaType XML_UTF8 = MediaType.parseMediaType("application/xml;charset=UTF-8");

	private final ProjectCatalog projectCatalog;
	private final String baseUrl;

	public SitemapController(
			ProjectCatalog projectCatalog,
			@Value("${portfinder.base-url:http://localhost:8080}") String baseUrl) {
		this.projectCatalog = projectCatalog;
		this.baseUrl = normalizeBaseUrl(baseUrl);
	}

	@GetMapping("/sitemap.xml")
	public ResponseEntity<String> sitemap() {
		StringBuilder xml = new StringBuilder(2048);
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
		url(xml, "/", "1.0");
		url(xml, "/categories", "0.8");
		url(xml, "/submit", "0.5");
		url(xml, "/privacy", "0.3");
		url(xml, "/terms", "0.3");
		for (var category : CategoryCatalog.all()) {
			url(xml, "/categories/" + category.slug(), "0.7");
		}
		for (Project project : projectCatalog.findAll()) {
			if (project.isPublished()) {
				url(xml, "/projects/" + project.id().value(), "0.9");
			}
		}
		xml.append("</urlset>");
		return ResponseEntity.ok().contentType(XML_UTF8).body(xml.toString());
	}

	private void url(StringBuilder xml, String path, String priority) {
		xml.append("  <url><loc>")
				.append(escapeXml(baseUrl))
				.append(escapeXml(path))
				.append("</loc><priority>")
				.append(priority)
				.append("</priority></url>\n");
	}

	static String normalizeBaseUrl(String raw) {
		if (raw == null || raw.isBlank()) {
			return "http://localhost:8080";
		}
		String trimmed = raw.trim();
		while (trimmed.endsWith("/")) {
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}
		return trimmed;
	}

	static String escapeXml(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		StringBuilder out = new StringBuilder(value.length() + 16);
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
				case '&' -> out.append("&amp;");
				case '<' -> out.append("&lt;");
				case '>' -> out.append("&gt;");
				case '"' -> out.append("&quot;");
				case '\'' -> out.append("&apos;");
				default -> out.append(c);
			}
		}
		return out.toString();
	}
}
