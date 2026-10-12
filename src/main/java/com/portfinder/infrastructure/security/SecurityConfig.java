package com.portfinder.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final GoogleUserService googleUserService;
	private final boolean oauthEnabled;

	public SecurityConfig(
			GoogleUserService googleUserService,
			@Value("${portfinder.security.oauth-enabled:false}") boolean oauthEnabled) {
		this.googleUserService = googleUserService;
		this.oauthEnabled = oauthEnabled;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers(
						"/css/**",
						"/js/**",
						"/error",
						"/sitemap.xml",
						"/robots.txt",
						"/health",
						"/favicon.ico",
						"/privacy",
						"/terms").permitAll()
				.requestMatchers("/", "/projects/**", "/categories/**").permitAll()
				.requestMatchers("/submit", "/moderation", "/moderation/**").hasRole("USER")
				.requestMatchers(HttpMethod.POST, "/projects/*/votes").hasRole("USER")
				.anyRequest().permitAll());

		if (oauthEnabled) {
			http.oauth2Login(oauth -> oauth
					.userInfoEndpoint(userInfo -> userInfo
							.userService(googleUserService)
							.oidcUserService(googleUserService.oidcUserService()))
					.defaultSuccessUrl("/", true));
		}
		else {
			http.oauth2Login(AbstractHttpConfigurer::disable);
			http.formLogin(AbstractHttpConfigurer::disable);
			http.httpBasic(AbstractHttpConfigurer::disable);
		}

		http.logout(logout -> logout.logoutSuccessUrl("/").permitAll());
		http.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
			String accept = request.getHeader("Accept");
			if (accept != null && accept.contains("application/json")) {
				response.setStatus(401);
				response.setContentType("application/json");
				response.getWriter().write("{\"error\":\"sign_in_required\"}");
			}
			else {
				response.sendRedirect("/?signin=required");
			}
		}));
		return http.build();
	}
}
