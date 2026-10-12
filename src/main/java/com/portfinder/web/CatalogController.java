package com.portfinder.web;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.application.query.CatalogSearchCriteria;
import com.portfinder.application.query.CatalogSort;
import com.portfinder.application.service.CatalogQueryService;
import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectStatus;

@Controller
public class CatalogController {

	private final CatalogQueryService catalogQueryService;
	private final ProjectCatalog projectCatalog;

	public CatalogController(CatalogQueryService catalogQueryService, ProjectCatalog projectCatalog) {
		this.catalogQueryService = catalogQueryService;
		this.projectCatalog = projectCatalog;
	}

	@GetMapping("/")
	public String home(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) String original,
			@RequestParam(required = false) String target,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String tag,
			@RequestParam(required = false, defaultValue = "UPDATED_DESC") String sort,
			Model model) {

		CatalogSearchCriteria criteria = new CatalogSearchCriteria(
				q,
				original,
				target,
				parseStatus(status),
				tag,
				parseSort(sort));

		List<Project> projects = catalogQueryService.list(criteria);
		List<Project> all = projectCatalog.findAll();

		model.addAttribute("projects", projects);
		model.addAttribute("criteria", criteria);
		model.addAttribute("statuses", ProjectStatus.values());
		model.addAttribute("originalPlatforms", distinctOriginals(all));
		model.addAttribute("targetPlatforms", distinctTargets(all));
		model.addAttribute("tags", distinctTags(all));
		model.addAttribute("resultCount", projects.size());
		return "home";
	}

	private static ProjectStatus parseStatus(String status) {
		if (status == null || status.isBlank()) {
			return null;
		}
		return ProjectStatus.from(status);
	}

	private static CatalogSort parseSort(String sort) {
		try {
			return CatalogSort.valueOf(sort.trim().toUpperCase(Locale.ROOT));
		}
		catch (Exception ex) {
			return CatalogSort.UPDATED_DESC;
		}
	}

	private static List<String> distinctOriginals(List<Project> projects) {
		return projects.stream()
				.map(p -> p.originalPlatform().label())
				.distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();
	}

	private static List<String> distinctTargets(List<Project> projects) {
		Set<String> labels = new LinkedHashSet<>();
		for (Project project : projects) {
			for (Platform platform : project.targetPlatforms()) {
				labels.add(platform.label());
			}
		}
		return labels.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
	}

	private static List<String> distinctTags(List<Project> projects) {
		return projects.stream()
				.flatMap(p -> p.tags().stream())
				.distinct()
				.sorted()
				.toList();
	}
}
