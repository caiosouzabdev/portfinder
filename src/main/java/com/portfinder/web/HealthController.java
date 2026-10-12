package com.portfinder.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthController {

	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("ok");
	}
}
