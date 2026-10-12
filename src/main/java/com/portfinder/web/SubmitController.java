package com.portfinder.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portfinder.application.service.ProjectSubmissionService;
import com.portfinder.application.service.ProjectSubmissionService.SubmitCommand;
import com.portfinder.application.service.RateLimitService;
import com.portfinder.domain.CategoryCatalog;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectStatus;
import com.portfinder.infrastructure.persistence.UserEntity;
import com.portfinder.infrastructure.persistence.UserRepository;
import com.portfinder.infrastructure.security.AppUser;
import com.portfinder.infrastructure.security.SecurityUtils;

@Controller
public class SubmitController {

	private final ProjectSubmissionService submissionService;
	private final UserRepository userRepository;
	private final RateLimitService rateLimitService;

	public SubmitController(ProjectSubmissionService submissionService, UserRepository userRepository,
			RateLimitService rateLimitService) {
		this.submissionService = submissionService;
		this.userRepository = userRepository;
		this.rateLimitService = rateLimitService;
	}

	@GetMapping("/contribute")
	public String legacyContributeRedirect() {
		return "redirect:/submit";
	}

	@GetMapping("/submit")
	public String form(Model model) {
		model.addAttribute("categories", CategoryCatalog.all());
		model.addAttribute("statuses", ProjectStatus.values());
		return "submit";
	}

	@PostMapping("/submit")
	public String submit(
			@RequestParam String id,
			@RequestParam String name,
			@RequestParam String summary,
			@RequestParam(required = false) String description,
			@RequestParam String originalPlatform,
			@RequestParam String targetPlatforms,
			@RequestParam String category,
			@RequestParam String status,
			@RequestParam String repoUrl,
			@RequestParam(required = false) String homepageUrl,
			@RequestParam(required = false) String imageUrl,
			@RequestParam(required = false) String tags,
			@RequestParam(defaultValue = "false") boolean policyAccepted,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (!policyAccepted) {
			model.addAttribute("error", "You must confirm the ROM/asset policy.");
			model.addAttribute("categories", CategoryCatalog.all());
			model.addAttribute("statuses", ProjectStatus.values());
			return "submit";
		}

		AppUser appUser = SecurityUtils.currentUser()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		rateLimitService.checkSubmit(appUser.getUserId());
		UserEntity user = userRepository.findById(appUser.getUserId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

		try {
			Project created = submissionService.submit(
					new SubmitCommand(id, name, summary, description, originalPlatform, targetPlatforms, category,
							status, repoUrl, homepageUrl, imageUrl, tags),
					user);
			redirectAttributes.addFlashAttribute("submittedId", created.id().value());
			return "redirect:/submit?ok=1";
		}
		catch (IllegalArgumentException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("categories", CategoryCatalog.all());
			model.addAttribute("statuses", ProjectStatus.values());
			return "submit";
		}
	}
}
