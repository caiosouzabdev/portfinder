package com.portfinder.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.portfinder.application.service.ProjectSubmissionService;
import com.portfinder.domain.PublicationStatus;
import com.portfinder.infrastructure.security.AppUser;
import com.portfinder.infrastructure.security.SecurityUtils;

@Controller
public class ModerationController {

	private final ProjectSubmissionService submissionService;

	public ModerationController(ProjectSubmissionService submissionService) {
		this.submissionService = submissionService;
	}

	@GetMapping("/moderation")
	public String queue(Model model) {
		requireAdmin();
		model.addAttribute("pending", submissionService.pending());
		return "moderation";
	}

	@PostMapping("/moderation/{id}")
	public String decide(@PathVariable String id, @RequestParam String decision) {
		requireAdmin();
		PublicationStatus status = switch (decision.toLowerCase()) {
			case "approve" -> PublicationStatus.PUBLISHED;
			case "reject" -> PublicationStatus.REJECTED;
			default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown decision");
		};
		submissionService.moderate(id, status);
		return "redirect:/moderation";
	}

	private static void requireAdmin() {
		AppUser user = SecurityUtils.currentUser()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		if (!user.isAdmin()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
		}
	}
}
