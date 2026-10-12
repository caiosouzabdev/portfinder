package com.portfinder.infrastructure.security;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfinder.infrastructure.persistence.UserEntity;
import com.portfinder.infrastructure.persistence.UserRepository;

@Service
public class GoogleUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final Set<String> adminEmails;
	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauthDelegate = new DefaultOAuth2UserService();
	private final OidcUserService oidcDelegate = new OidcUserService();

	public GoogleUserService(
			UserRepository userRepository,
			@Value("${portfinder.moderation.admin-emails:}") String adminEmails) {
		this.userRepository = userRepository;
		this.adminEmails = Arrays.stream(adminEmails.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(s -> s.toLowerCase(Locale.ROOT))
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauthUser = oauthDelegate.loadUser(userRequest);
		UserEntity user = upsert(oauthUser.getAttributes());
		return toAppUser(user, oauthUser.getAttributes());
	}

	@Transactional
	public OidcUser loadOidcUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		OidcUser oidcUser = oidcDelegate.loadUser(userRequest);
		UserEntity user = upsert(oidcUser.getAttributes());
		return toOidcUser(user, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
	}

	public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		return this::loadOidcUser;
	}

	private UserEntity upsert(Map<String, Object> attributes) {
		String sub = stringAttr(attributes.get("sub"));
		String email = stringAttr(attributes.get("email"));
		String name = stringAttr(attributes.get("name"));
		String picture = stringAttr(attributes.get("picture"));
		if (sub == null || email == null) {
			throw new OAuth2AuthenticationException("Google profile missing sub/email");
		}
		String displayName = (name == null || name.isBlank()) ? email : name;

		return userRepository.findByGoogleSub(sub)
				.map(existing -> {
					existing.updateProfile(email, displayName, picture);
					return userRepository.save(existing);
				})
				.orElseGet(() -> userRepository.save(new UserEntity(sub, email, displayName, picture)));
	}

	private AppUser toAppUser(UserEntity user, Map<String, Object> attributes) {
		return new AppUser(user.getId(), user.getEmail(), user.getDisplayName(), user.getAvatarUrl(),
				isAdmin(user.getEmail()), attributes);
	}

	private AppOidcUser toOidcUser(UserEntity user, Map<String, Object> attributes, OidcIdToken idToken,
			OidcUserInfo userInfo) {
		return new AppOidcUser(user.getId(), user.getEmail(), user.getDisplayName(), user.getAvatarUrl(),
				isAdmin(user.getEmail()), attributes, idToken, userInfo);
	}

	private boolean isAdmin(String email) {
		return adminEmails.contains(email.toLowerCase(Locale.ROOT));
	}

	private static String stringAttr(Object value) {
		return value == null ? null : String.valueOf(value);
	}
}
