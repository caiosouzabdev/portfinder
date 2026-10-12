package com.portfinder.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.portfinder.application.service.CatalogQueryService;
import com.portfinder.domain.Project;

@Controller
public class ProjectController {

	private final CatalogQueryService catalogQueryService;

	public ProjectController(CatalogQueryService catalogQueryService) {
		this.catalogQueryService = catalogQueryService;
	}

	@GetMapping("/projects/{id}")
	public String detail(@PathVariable String id, Model model) {
		Project project = catalogQueryService.getById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found: " + id));
		model.addAttribute("project", project);
		return "project";
	}
}
