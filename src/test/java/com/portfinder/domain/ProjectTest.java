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

	private static Builder sample() {
		return new Builder();
	}

	private static final class Builder {
		private String name = "Sample";
		private List<Platform> targets = List.of(Platform.of("PC"));
		private List<String> tags = List.of(" Recomp ", "n64", "recomp", "");

		Builder name(String name) {
			this.name = name;
			return this;
		}

		Builder targets(List<Platform> targets) {
			this.targets = targets;
			return this;
		}

		Project build() {
			return new Project(
					ProjectId.of("sample"),
					name,
					"A sample project",
					Platform.of("N64"),
					targets,
					ProjectStatus.ACTIVE,
					Optional.of(URI.create("https://example.com/repo")),
					Optional.empty(),
					tags,
					LocalDate.of(2026, 1, 1));
		}
	}
}
