package com.portfinder.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LegalControllerTest {

	private final LegalController controller = new LegalController();

	@Test
	void privacyAndTermsReturnViews() {
		assertEquals("privacy", controller.privacy());
		assertEquals("terms", controller.terms());
	}
}
