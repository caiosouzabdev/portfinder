package com.portfinder.application.port;

import java.util.List;
import java.util.Optional;

import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;

/**
 * Read port for the project catalog. Implementations may load from files, DB, etc.
 */
public interface ProjectCatalog {

	List<Project> findAll();

	Optional<Project> findById(ProjectId id);
}
