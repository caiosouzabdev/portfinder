package com.portfinder.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContributeController {

	private final String githubRepoUrl;

	public ContributeController(@Value("${portfinder.github.repo-url}") String githubRepoUrl) {
		this.githubRepoUrl = githubRepoUrl;
	}

	@GetMapping("/contribute")
	public String contribute(Model model) {
		model.addAttribute("githubRepoUrl", githubRepoUrl);
		model.addAttribute("suggestIssueUrl", githubRepoUrl + "/issues/new?template=suggest-project.yml");
		model.addAttribute("newPrUrl", githubRepoUrl + "/compare");
		return "contribute";
	}
}
