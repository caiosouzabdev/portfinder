package com.portfinder.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.portfinder.domain.Project;
import com.portfinder.infrastructure.persistence.UserEntity;
import com.portfinder.infrastructure.persistence.UserRepository;

@SpringBootTest
@Transactional
class VoteServiceIntegrationTest {

	@Autowired
	private VoteService voteService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CatalogQueryService catalogQueryService;

	@Test
	void togglesVoteAndUpdatesCount() {
		UserEntity user = userRepository.save(new UserEntity("sub-1", "voter@example.com", "Voter", null));
		Project project = catalogQueryService.list(com.portfinder.application.query.CatalogSearchCriteria.empty())
				.getFirst();

		Project afterUp = voteService.toggleVote(user.getId(), project.id().value());
		assertEquals(project.voteCount() + 1, afterUp.voteCount());
		assertTrue(voteService.hasVoted(user.getId(), project.id().value()));

		Project afterDown = voteService.toggleVote(user.getId(), project.id().value());
		assertEquals(project.voteCount(), afterDown.voteCount());
		assertFalse(voteService.hasVoted(user.getId(), project.id().value()));
	}
}
