package com.portfinder.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProjectIdTest {

	@Test
	void normalizesCaseAndWhitespace() {
		assertEquals("zelda-recomp", ProjectId.of("  Zelda Recomp ").value());
	}

	@Test
	void acceptsValidKebabCase() {
		assertEquals("n64-recomp", ProjectId.of("n64-recomp").value());
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "   ", "Bad_Id!", "-leading", "trailing-", "double--dash", "Upper Case!" })
	void rejectsInvalidIds(String raw) {
		assertThrows(IllegalArgumentException.class, () -> ProjectId.of(raw));
	}

	@Test
	void rejectsNull() {
		assertThrows(NullPointerException.class, () -> ProjectId.of(null));
	}

	@Test
	void rejectsTooLong() {
		String tooLong = "a".repeat(65);
		assertThrows(IllegalArgumentException.class, () -> ProjectId.of(tooLong));
	}
}
