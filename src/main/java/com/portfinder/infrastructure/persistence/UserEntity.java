package com.portfinder.infrastructure.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "google_sub", nullable = false, unique = true, length = 128)
	private String googleSub;

	@Column(nullable = false, length = 320)
	private String email;

	@Column(name = "display_name", nullable = false, length = 200)
	private String displayName;

	@Column(name = "avatar_url", length = 1000)
	private String avatarUrl;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected UserEntity() {
	}

	public UserEntity(String googleSub, String email, String displayName, String avatarUrl) {
		this.googleSub = googleSub;
		this.email = email;
		this.displayName = displayName;
		this.avatarUrl = avatarUrl;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getGoogleSub() {
		return googleSub;
	}

	public String getEmail() {
		return email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void updateProfile(String email, String displayName, String avatarUrl) {
		this.email = email;
		this.displayName = displayName;
		this.avatarUrl = avatarUrl;
	}
}
