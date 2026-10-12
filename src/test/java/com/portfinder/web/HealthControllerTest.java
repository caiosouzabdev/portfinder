package com.portfinder.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class HealthControllerTest {

	@Test
	void healthReturnsOk() {
		ResponseEntity<String> response = new HealthController().health();
		assertEquals(200, response.getStatusCode().value());
		assertEquals("ok", response.getBody());
	}
}
