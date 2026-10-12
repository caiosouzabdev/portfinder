package com.portfinder.web;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.application.query.CatalogSearchCriteria;
import com.portfinder.application.query.CatalogSort;
import com.portfinder.application.query.PagedProjects;
import com.portfinder.application.service.CatalogQueryService;
import com.portfinder.application.service.VoteService;
import com.portfinder.domain.Platform;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectStatus;
import com.portfinder.infrastructure.security.SecurityUtils;

@Controller
public class CatalogController {

	private final CatalogQueryService catalogQueryService;
	private final ProjectCatalog projectCatalog;
	private final VoteService voteService;

	public CatalogController(CatalogQueryService catalogQueryService, ProjectCatalog projectCatalog,
			VoteService voteService) {
		this.catalogQueryService = catalogQueryService;
		this.projectCatalog = projectCatalog;
		this.voteService = voteService;
	}

	@GetMapping("/")
	public String home(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) String original,
			@RequestParam(required = false) String target,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String tag,
			@RequestParam(required = false) String category,
			@RequestParam(required = false, defaultValue = "VOTES_DESC") String sort,
			@RequestParam(required = false, defaultValue = "0") int page,
			Model model) {

		CatalogSearchCriteria criteria = new CatalogSearchCriteria(
				q, original, target, parseStatus(status), tag, category, parseSort(sort), page,
				CatalogSearchCriteria.DEFAULT_PAGE_SIZE);

		PagedProjects paged = catalogQueryService.page(criteria);
		List<Project> all = projectCatalog.findAll().stream().filter(Project::isPublished).toList();
		List<Project> featured = catalogQueryService.featured(6);

		model.addAttribute("projects", paged.items());
		model.addAttribute("paged", paged);
		model.addAttribute("featured", featured);
		model.addAttribute("criteria", criteria);
		model.addAttribute("statuses", ProjectStatus.values());
		model.addAttribute("originalPlatforms", distinctOriginals(all));
		model.addAttribute("targetPlatforms", distinctTargets(all));
		model.addAttribute("tags", distinctTags(all));
		model.addAttribute("resultCount", paged.totalCount());
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("rankOffset", page * criteria.pageSize());
		addVoteState(model, paged.items());
		return "home";
	}

	void addVoteState(Model model, List<Project> projects) {
		Long userId = SecurityUtils.currentUser().map(u -> u.getUserId()).orElse(null);
		Set<String> ids = projects.stream().map(p -> p.id().value()).collect(Collectors.toSet());
		model.addAttribute("votedIds", voteService.votedProjectIds(userId, ids));
		model.addAttribute("signedIn", userId != null);
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
			return CatalogSort.VOTES_DESC;
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
