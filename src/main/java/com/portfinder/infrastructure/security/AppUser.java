package com.portfinder.infrastructure.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class AppUser implements OAuth2User {

	private final Long userId;
	private final String email;
	private final String displayName;
	private final String avatarUrl;
	private final boolean admin;
	private final Map<String, Object> attributes;

	public AppUser(Long userId, String email, String displayName, String avatarUrl, boolean admin,
			Map<String, Object> attributes) {
		this.userId = userId;
		this.email = email;
		this.displayName = displayName;
		this.avatarUrl = avatarUrl;
		this.admin = admin;
		this.attributes = attributes;
	}

	public Long getUserId() {
		return userId;
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

	public boolean isAdmin() {
		return admin;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (admin) {
			return List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getName() {
		return displayName;
	}
}
