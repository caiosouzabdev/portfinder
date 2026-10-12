package com.portfinder.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, String> {

	List<ProjectEntity> findByPublicationStatusOrderByVoteCountDescUpdatedAtDesc(String publicationStatus);

	List<ProjectEntity> findByPublicationStatus(String publicationStatus);

	long countByCategoryAndPublicationStatus(String category, String publicationStatus);
}
