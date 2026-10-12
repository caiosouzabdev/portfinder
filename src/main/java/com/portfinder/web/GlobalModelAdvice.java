package com.portfinder.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.portfinder.infrastructure.security.AppUser;
import com.portfinder.infrastructure.security.SecurityUtils;

@ControllerAdvice
public class GlobalModelAdvice {

	private final boolean oauthEnabled;

	public GlobalModelAdvice(@Value("${portfinder.security.oauth-enabled:false}") boolean oauthEnabled) {
		this.oauthEnabled = oauthEnabled;
	}

	@ModelAttribute("currentUser")
	public AppUser currentUser() {
		return SecurityUtils.currentUser().orElse(null);
	}

	@ModelAttribute("oauthEnabled")
	public boolean oauthEnabled() {
		return oauthEnabled;
	}
}
