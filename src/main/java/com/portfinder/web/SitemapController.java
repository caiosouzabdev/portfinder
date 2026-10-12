package com.portfinder.web;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.domain.CategoryCatalog;
import com.portfinder.domain.Project;

@Controller
public class SitemapController {

	private final ProjectCatalog projectCatalog;
	private final String baseUrl;

	public SitemapController(
			ProjectCatalog projectCatalog,
			@Value("${portfinder.base-url:http://localhost:8080}") String baseUrl) {
		this.projectCatalog = projectCatalog;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public String sitemap() {
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
		url(xml, "/", "1.0");
		url(xml, "/categories", "0.8");
		url(xml, "/contribute", "0.5");
		url(xml, "/submit", "0.5");
		for (var category : CategoryCatalog.all()) {
			url(xml, "/categories/" + category.slug(), "0.7");
		}
		for (Project project : projectCatalog.findAll().stream().filter(Project::isPublished).collect(Collectors.toList())) {
			url(xml, "/projects/" + project.id().value(), "0.9");
		}
		xml.append("</urlset>");
		return xml.toString();
	}

	private void url(StringBuilder xml, String path, String priority) {
		xml.append("  <url><loc>").append(baseUrl).append(path).append("</loc><priority>")
				.append(priority).append("</priority></url>\n");
	}
}
