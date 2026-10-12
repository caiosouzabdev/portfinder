package com.portfinder.infrastructure.security;

import java.util.Map;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public final class AppOidcUser extends AppUser implements OidcUser {

	private final OidcIdToken idToken;
	private final OidcUserInfo userInfo;

	public AppOidcUser(Long userId, String email, String displayName, String avatarUrl, boolean admin,
			Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
		super(userId, email, displayName, avatarUrl, admin, attributes);
		this.idToken = idToken;
		this.userInfo = userInfo;
	}

	@Override
	public Map<String, Object> getClaims() {
		return getAttributes();
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return userInfo;
	}

	@Override
	public OidcIdToken getIdToken() {
		return idToken;
	}
}
