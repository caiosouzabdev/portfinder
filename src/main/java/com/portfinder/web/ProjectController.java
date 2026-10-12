package com.portfinder.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.portfinder.application.service.CatalogQueryService;
import com.portfinder.application.service.RateLimitService;
import com.portfinder.application.service.VoteService;
import com.portfinder.domain.CategoryCatalog;
import com.portfinder.domain.Project;
import com.portfinder.infrastructure.security.AppUser;
import com.portfinder.infrastructure.security.SecurityUtils;

@Controller
public class ProjectController {

	private final CatalogQueryService catalogQueryService;
	private final VoteService voteService;
	private final RateLimitService rateLimitService;

	public ProjectController(CatalogQueryService catalogQueryService, VoteService voteService,
			RateLimitService rateLimitService) {
		this.catalogQueryService = catalogQueryService;
		this.voteService = voteService;
		this.rateLimitService = rateLimitService;
	}

	@GetMapping("/projects/{id}")
	public String detail(@PathVariable String id, Model model) {
		Project project = catalogQueryService.getPublishedById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found: " + id));
		List<Project> related = catalogQueryService.related(project, 4);
		AppUser user = SecurityUtils.currentUser().orElse(null);

		model.addAttribute("project", project);
		model.addAttribute("category", CategoryCatalog.findBySlug(project.category()).orElse(null));
		model.addAttribute("related", related);
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("signedIn", user != null);
		model.addAttribute("hasVoted", user != null && voteService.hasVoted(user.getUserId(), project.id().value()));
		return "project";
	}

	@PostMapping("/projects/{id}/votes")
	@ResponseBody
	public VoteResponse toggleVote(@PathVariable String id) {
		AppUser user = SecurityUtils.currentUser()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in required"));
		rateLimitService.checkVote(user.getUserId());
		Project updated = voteService.toggleVote(user.getUserId(), id);
		boolean voted = voteService.hasVoted(user.getUserId(), id);
		return new VoteResponse(updated.id().value(), updated.voteCount(), voted);
	}

	public record VoteResponse(String projectId, int voteCount, boolean voted) {
	}
}
