package com.portfinder.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.portfinder.application.service.ProjectSubmissionService.SubmitCommand;
import com.portfinder.domain.Project;
import com.portfinder.domain.PublicationStatus;
import com.portfinder.infrastructure.persistence.UserEntity;
import com.portfinder.infrastructure.persistence.UserRepository;

@SpringBootTest
@Transactional
class ProjectSubmissionServiceIntegrationTest {

	@Autowired
	private ProjectSubmissionService submissionService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CatalogQueryService catalogQueryService;

	@Test
	void submitCreatesPendingThenPublishMakesVisible() {
		UserEntity user = userRepository.save(new UserEntity("sub-2", "author@example.com", "Author", null));
		Project pending = submissionService.submit(new SubmitCommand(
				"integration-sample",
				"Integration Sample",
				"Short summary",
				"Longer description",
				"N64",
				"PC",
				"n64",
				"WIP",
				"https://example.com/repo",
				"",
				"",
				"recomp,test"), user);

		assertEquals(PublicationStatus.PENDING, pending.publicationStatus());
		assertTrue(catalogQueryService.getPublishedById("integration-sample").isEmpty());

		submissionService.moderate("integration-sample", PublicationStatus.PUBLISHED);
		assertTrue(catalogQueryService.getPublishedById("integration-sample").isPresent());
	}
}
