package com.portfinder.infrastructure.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;

import tools.jackson.databind.ObjectMapper;

/**
 * Loads seed projects from classpath JSON. Not the runtime catalog source of truth.
 */
@Component
public class JsonCatalogLoader {

	private final ResourcePatternResolver resources;
	private final ObjectMapper objectMapper;
	private final ProjectDocumentMapper mapper = new ProjectDocumentMapper();
	private final String locationPattern;

	public JsonCatalogLoader(
			ResourcePatternResolver resources,
			ObjectMapper objectMapper,
			@Value("${portfinder.catalog.location-pattern:classpath:catalog/projects/*.json}") String locationPattern) {
		this.resources = resources;
		this.objectMapper = objectMapper;
		this.locationPattern = locationPattern;
	}

	public List<Project> loadAll() {
		try {
			Resource[] files = resources.getResources(locationPattern);
			Map<ProjectId, Project> loaded = new HashMap<>();
			for (Resource file : files) {
				if (!file.isReadable()) {
					continue;
				}
				Project project = readProject(file);
				Project previous = loaded.put(project.id(), project);
				if (previous != null) {
					throw new IllegalStateException("Duplicate project id: " + project.id().value());
				}
			}
			return loaded.values().stream()
					.sorted(Comparator.comparing(p -> p.id().value()))
					.toList();
		}
		catch (IOException ex) {
			throw new UncheckedIOException("Failed to scan catalog at " + locationPattern, ex);
		}
	}

	public List<Project> loadFromPattern(String pattern) {
		try {
			Resource[] files = resources.getResources(pattern);
			List<Project> loaded = new ArrayList<>();
			for (Resource file : files) {
				if (file.isReadable()) {
					loaded.add(readProject(file));
				}
			}
			return List.copyOf(loaded);
		}
		catch (IOException ex) {
			throw new UncheckedIOException("Failed to scan catalog at " + pattern, ex);
		}
	}

	private Project readProject(Resource file) {
		try (InputStream in = file.getInputStream()) {
			ProjectDocument document = objectMapper.readValue(in, ProjectDocument.class);
			return mapper.toDomain(document);
		}
		catch (IOException ex) {
			throw new UncheckedIOException("Failed to read catalog file: " + describe(file), ex);
		}
		catch (RuntimeException ex) {
			throw new IllegalStateException("Invalid catalog file: " + describe(file) + " — " + ex.getMessage(), ex);
		}
	}

	private static String describe(Resource file) {
		try {
			return file.getURI().toString();
		}
		catch (IOException ex) {
			return file.getDescription();
		}
	}
}
