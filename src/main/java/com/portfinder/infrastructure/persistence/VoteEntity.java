package com.portfinder.infrastructure.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "votes", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "project_id" }))
public class VoteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "project_id", nullable = false, length = 64)
	private String projectId;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected VoteEntity() {
	}

	public VoteEntity(Long userId, String projectId) {
		this.userId = userId;
		this.projectId = projectId;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public String getProjectId() {
		return projectId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
