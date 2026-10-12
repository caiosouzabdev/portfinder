package com.portfinder.infrastructure.persistence;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class ProjectEntity {

	@Id
	@Column(length = 64)
	private String id;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(nullable = false, length = 500)
	private String summary;

	@Column(nullable = false, columnDefinition = "CLOB")
	private String description;

	@Column(name = "original_platform", nullable = false, length = 100)
	private String originalPlatform;

	@Column(name = "target_platforms_json", nullable = false, columnDefinition = "CLOB")
	private String targetPlatformsJson;

	@Column(nullable = false, length = 64)
	private String category;

	@Column(nullable = false, length = 32)
	private String status;

	@Column(name = "repo_url", length = 1000)
	private String repoUrl;

	@Column(name = "homepage_url", length = 1000)
	private String homepageUrl;

	@Column(name = "image_url", length = 1000)
	private String imageUrl;

	@Column(name = "screenshot_urls_json", nullable = false, columnDefinition = "CLOB")
	private String screenshotUrlsJson;

	@Column(name = "tags_json", nullable = false, columnDefinition = "CLOB")
	private String tagsJson;

	@Column(name = "updated_at", nullable = false)
	private LocalDate updatedAt;

	@Column(nullable = false)
	private boolean featured;

	@Column(name = "trending_until")
	private LocalDate trendingUntil;

	@Column(name = "publication_status", nullable = false, length = 32)
	private String publicationStatus;

	@Column(name = "vote_count", nullable = false)
	private int voteCount;

	@Column(name = "submitted_by_user_id")
	private Long submittedByUserId;

	@Column(name = "submitted_by_display_name", length = 200)
	private String submittedByDisplayName;

	protected ProjectEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOriginalPlatform() {
		return originalPlatform;
	}

	public void setOriginalPlatform(String originalPlatform) {
		this.originalPlatform = originalPlatform;
	}

	public String getTargetPlatformsJson() {
		return targetPlatformsJson;
	}

	public void setTargetPlatformsJson(String targetPlatformsJson) {
		this.targetPlatformsJson = targetPlatformsJson;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public String getHomepageUrl() {
		return homepageUrl;
	}

	public void setHomepageUrl(String homepageUrl) {
		this.homepageUrl = homepageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getScreenshotUrlsJson() {
		return screenshotUrlsJson;
	}

	public void setScreenshotUrlsJson(String screenshotUrlsJson) {
		this.screenshotUrlsJson = screenshotUrlsJson;
	}

	public String getTagsJson() {
		return tagsJson;
	}

	public void setTagsJson(String tagsJson) {
		this.tagsJson = tagsJson;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public LocalDate getTrendingUntil() {
		return trendingUntil;
	}

	public void setTrendingUntil(LocalDate trendingUntil) {
		this.trendingUntil = trendingUntil;
	}

	public String getPublicationStatus() {
		return publicationStatus;
	}

	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public Long getSubmittedByUserId() {
		return submittedByUserId;
	}

	public void setSubmittedByUserId(Long submittedByUserId) {
		this.submittedByUserId = submittedByUserId;
	}

	public String getSubmittedByDisplayName() {
		return submittedByDisplayName;
	}

	public void setSubmittedByDisplayName(String submittedByDisplayName) {
		this.submittedByDisplayName = submittedByDisplayName;
	}
}
