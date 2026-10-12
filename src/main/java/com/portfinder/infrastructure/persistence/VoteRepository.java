package com.portfinder.infrastructure.persistence;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {

	Optional<VoteEntity> findByUserIdAndProjectId(Long userId, String projectId);

	boolean existsByUserIdAndProjectId(Long userId, String projectId);

	void deleteByUserIdAndProjectId(Long userId, String projectId);

	long countByProjectId(String projectId);

	@Query("select v.projectId from VoteEntity v where v.userId = :userId and v.projectId in :projectIds")
	Set<String> findVotedProjectIds(@Param("userId") Long userId, @Param("projectIds") Set<String> projectIds);
}
