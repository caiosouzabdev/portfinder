package com.portfinder.application.service;

import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;
import com.portfinder.domain.PublicationStatus;
import com.portfinder.infrastructure.persistence.JpaProjectCatalog;
import com.portfinder.infrastructure.persistence.ProjectEntity;
import com.portfinder.infrastructure.persistence.ProjectJpaRepository;
import com.portfinder.infrastructure.persistence.VoteEntity;
import com.portfinder.infrastructure.persistence.VoteRepository;

@Service
public class VoteService {

	private final VoteRepository voteRepository;
	private final ProjectJpaRepository projectRepository;
	private final JpaProjectCatalog catalog;

	public VoteService(VoteRepository voteRepository, ProjectJpaRepository projectRepository,
			JpaProjectCatalog catalog) {
		this.voteRepository = voteRepository;
		this.projectRepository = projectRepository;
		this.catalog = catalog;
	}

	@Transactional
	public Project toggleVote(Long userId, String projectId) {
		Objects.requireNonNull(userId, "userId");
		ProjectId id = ProjectId.of(projectId);
		ProjectEntity entity = projectRepository.findById(id.value())
				.orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
		if (!PublicationStatus.PUBLISHED.name().equals(entity.getPublicationStatus())) {
			throw new IllegalStateException("Only published projects can be voted");
		}

		var existing = voteRepository.findByUserIdAndProjectId(userId, id.value());
		if (existing.isPresent()) {
			voteRepository.delete(existing.get());
			entity.setVoteCount(Math.max(0, entity.getVoteCount() - 1));
		}
		else {
			voteRepository.save(new VoteEntity(userId, id.value()));
			entity.setVoteCount(entity.getVoteCount() + 1);
		}
		projectRepository.save(entity);
		return catalog.findById(id).orElseThrow();
	}

	@Transactional(readOnly = true)
	public boolean hasVoted(Long userId, String projectId) {
		if (userId == null) {
			return false;
		}
		return voteRepository.existsByUserIdAndProjectId(userId, projectId);
	}

	@Transactional(readOnly = true)
	public Set<String> votedProjectIds(Long userId, Set<String> projectIds) {
		if (userId == null || projectIds == null || projectIds.isEmpty()) {
			return Set.of();
		}
		return voteRepository.findVotedProjectIds(userId, projectIds);
	}
}
