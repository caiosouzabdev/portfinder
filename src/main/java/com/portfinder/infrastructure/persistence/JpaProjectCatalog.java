package com.portfinder.infrastructure.persistence;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.portfinder.application.port.ProjectCatalog;
import com.portfinder.application.port.ProjectWriter;
import com.portfinder.domain.Project;
import com.portfinder.domain.ProjectId;

@Component
public class JpaProjectCatalog implements ProjectCatalog, ProjectWriter {

	private final ProjectJpaRepository repository;
	private final ProjectEntityMapper mapper;

	public JpaProjectCatalog(ProjectJpaRepository repository, ProjectEntityMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Project> findAll() {
		return repository.findAll().stream()
				.map(mapper::toDomain)
				.sorted(Comparator.comparing(p -> p.id().value()))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Project> findById(ProjectId id) {
		return repository.findById(id.value()).map(mapper::toDomain);
	}

	@Transactional
	public Project save(Project project) {
		ProjectEntity entity = repository.findById(project.id().value())
				.orElseGet(ProjectEntity::new);
		mapper.apply(project, entity);
		return mapper.toDomain(repository.save(entity));
	}

	@Transactional(readOnly = true)
	public long count() {
		return repository.count();
	}
}
