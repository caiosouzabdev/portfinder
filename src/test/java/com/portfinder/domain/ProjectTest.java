package com.portfinder.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class ProjectTest {

	@Test
	void normalizesTagsAndRejectsBlankName() {
		Project project = sample().build();
		assertEquals(List.of("recomp", "n64"), project.tags());
		assertTrue(project.hasTag("RECOMP"));
		assertFalse(project.hasTag("missing"));
		assertEquals("n64", project.category());
		assertEquals(project.summary(), project.description());
	}

	@Test
	void rejectsEmptyTargetPlatforms() {
		assertThrows(IllegalArgumentException.class, () -> sample().targets(List.of()).build());
	}

	@Test
	void targetsIsCaseInsensitive() {
		Project project = sample().build();
		assertTrue(project.targets("pc"));
		assertFalse(project.targets("Switch"));
	}

	@Test
	void rejectsBlankName() {
		assertThrows(IllegalArgumentException.class, () -> sample().name("  ").build());
	}

	@Test
	void rejectsUnknownCategory() {
		assertThrows(IllegalArgumentException.class, () -> sample().category("nope").build());
	}

	private static Builder sample() {
		return new Builder();
	}

	private static final class Builder {
		private String name = "Sample";
		private List<Platform> targets = List.of(Platform.of("PC"));
		private List<String> tags = List.of(" Recomp ", "n64", "recomp", "");
		private String category = "n64";

		Builder name(String name) {
			this.name = name;
			return this;
		}

		Builder targets(List<Platform> targets) {
			this.targets = targets;
			return this;
		}

		Builder category(String category) {
			this.category = category;
			return this;
		}

		Project build() {
			return new Project(
					ProjectId.of("sample"),
					name,
					"A sample project",
					null,
					Platform.of("N64"),
					targets,
					ProjectStatus.ACTIVE,
					category,
					Optional.of(URI.create("https://example.com/repo")),
					Optional.empty(),
					Optional.of(URI.create("https://example.com/preview.png")),
					List.of(),
					tags,
					LocalDate.of(2026, 1, 1),
					false,
					Optional.empty(),
					PublicationStatus.PUBLISHED,
					0,
					Optional.empty());
		}
	}
}
