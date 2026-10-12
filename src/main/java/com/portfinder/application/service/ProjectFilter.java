package com.portfinder.application.service;

import java.util.Locale;

import com.portfinder.application.query.CatalogSearchCriteria;
import com.portfinder.domain.Project;

/**
 * Pure matching rules for catalog search/filter. No I/O, no Spring.
 */
public final class ProjectFilter {

	public boolean matches(Project project, CatalogSearchCriteria criteria) {
		if (criteria.hasOriginalPlatform()
				&& !project.originalPlatform().matchesIgnoreCase(criteria.originalPlatform())) {
			return false;
		}
		if (criteria.hasTargetPlatform() && !project.targets(criteria.targetPlatform())) {
			return false;
		}
		if (criteria.hasStatus() && project.status() != criteria.status()) {
			return false;
		}
		if (criteria.hasTag() && !project.hasTag(criteria.tag())) {
			return false;
		}
		if (criteria.hasQuery() && !matchesQuery(project, criteria.query())) {
			return false;
		}
		return true;
	}

	private boolean matchesQuery(Project project, String query) {
		String needle = query.toLowerCase(Locale.ROOT);
		if (contains(project.name(), needle) || contains(project.summary(), needle)) {
			return true;
		}
		if (contains(project.originalPlatform().label(), needle)) {
			return true;
		}
		if (project.targetPlatforms().stream().anyMatch(p -> contains(p.label(), needle))) {
			return true;
		}
		return project.tags().stream().anyMatch(tag -> contains(tag, needle));
	}

	private static boolean contains(String haystack, String needleLower) {
		return haystack.toLowerCase(Locale.ROOT).contains(needleLower);
	}
}
