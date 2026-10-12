package com.portfinder.infrastructure.security;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfinder.infrastructure.persistence.UserEntity;
import com.portfinder.infrastructure.persistence.UserRepository;

@Service
public class GoogleUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final Set<String> adminEmails;
	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

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
		OAuth2User oauthUser = delegate.loadUser(userRequest);
		String sub = oauthUser.getAttribute("sub");
		String email = oauthUser.getAttribute("email");
		String name = oauthUser.getAttribute("name");
		String picture = oauthUser.getAttribute("picture");
		if (sub == null || email == null) {
			throw new OAuth2AuthenticationException("Google profile missing sub/email");
		}
		String displayName = (name == null || name.isBlank()) ? email : name;

		UserEntity user = userRepository.findByGoogleSub(sub)
				.map(existing -> {
					existing.updateProfile(email, displayName, picture);
					return userRepository.save(existing);
				})
				.orElseGet(() -> userRepository.save(new UserEntity(sub, email, displayName, picture)));

		boolean admin = adminEmails.contains(email.toLowerCase(Locale.ROOT));
		return new AppUser(user.getId(), user.getEmail(), user.getDisplayName(), user.getAvatarUrl(), admin,
				oauthUser.getAttributes());
	}
}
