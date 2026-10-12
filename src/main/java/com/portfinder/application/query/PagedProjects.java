package com.portfinder.application.query;

import java.util.List;

import com.portfinder.domain.Project;

public record PagedProjects(List<Project> items, int totalCount, int page, int pageSize) {

	public boolean hasNext() {
		return (page + 1) * pageSize < totalCount;
	}

	public boolean hasPrevious() {
		return page > 0;
	}

	public int nextPage() {
		return page + 1;
	}

	public int previousPage() {
		return Math.max(page - 1, 0);
	}
}
