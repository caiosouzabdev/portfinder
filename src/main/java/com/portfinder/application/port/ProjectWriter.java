package com.portfinder.application.port;

import com.portfinder.domain.Project;

/**
 * Write port for catalog mutations (submit, moderation, vote count).
 */
public interface ProjectWriter {

	Project save(Project project);
}
