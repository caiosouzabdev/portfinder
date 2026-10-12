package com.portfinder.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.application.query.CatalogSearchCriteria;
import com.portfinder.application.query.CatalogSort;
import com.portfinder.application.query.PagedProjects;
import com.portfinder.application.service.CatalogQueryService;
import com.portfinder.application.service.VoteService;
import com.portfinder.domain.Category;
import com.portfinder.domain.CategoryCatalog;
import com.portfinder.domain.Project;

@Controller
public class CategoryController {

	private final CatalogQueryService catalogQueryService;
	private final ProjectCatalog projectCatalog;
	private final VoteService voteService;
	private final CatalogController catalogController;

	public CategoryController(CatalogQueryService catalogQueryService, ProjectCatalog projectCatalog,
			VoteService voteService, CatalogController catalogController) {
		this.catalogQueryService = catalogQueryService;
		this.projectCatalog = projectCatalog;
		this.voteService = voteService;
		this.catalogController = catalogController;
	}

	@GetMapping("/categories")
	public String index(Model model) {
		List<CategoryView> categories = CategoryCatalog.all().stream()
				.map(cat -> new CategoryView(cat,
						projectCatalog.findAll().stream()
								.filter(Project::isPublished)
								.filter(p -> p.matchesCategory(cat.slug()))
								.count()))
				.toList();
		model.addAttribute("categories", categories);
		return "categories";
	}

	@GetMapping("/categories/{slug}")
	public String category(
			@PathVariable String slug,
			@RequestParam(required = false) String q,
			@RequestParam(required = false, defaultValue = "VOTES_DESC") String sort,
			@RequestParam(required = false, defaultValue = "0") int page,
			Model model) {

		Category category = CategoryCatalog.findBySlug(slug)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown category"));

		CatalogSearchCriteria criteria = new CatalogSearchCriteria(
				q, null, null, null, null, category.slug(), parseSort(sort), page,
				CatalogSearchCriteria.DEFAULT_PAGE_SIZE);
		PagedProjects paged = catalogQueryService.page(criteria);

		model.addAttribute("category", category);
		model.addAttribute("projects", paged.items());
		model.addAttribute("paged", paged);
		model.addAttribute("criteria", criteria);
		model.addAttribute("resultCount", paged.totalCount());
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("rankOffset", page * criteria.pageSize());
		catalogController.addVoteState(model, paged.items());
		return "category";
	}

	private static CatalogSort parseSort(String sort) {
		try {
			return CatalogSort.valueOf(sort.trim().toUpperCase(Locale.ROOT));
		}
		catch (Exception ex) {
			return CatalogSort.VOTES_DESC;
		}
	}

	public record CategoryView(Category category, long count) {
	}
}
