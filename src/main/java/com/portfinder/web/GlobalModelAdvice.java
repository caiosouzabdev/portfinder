package com.portfinder.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.portfinder.infrastructure.security.AppUser;

@ControllerAdvice
public class GlobalModelAdvice {

	private final boolean oauthEnabled;

	public GlobalModelAdvice(@Value("${portfinder.security.oauth-enabled:false}") boolean oauthEnabled) {
		this.oauthEnabled = oauthEnabled;
	}

	@ModelAttribute("currentUser")
	public AppUser currentUser(@AuthenticationPrincipal AppUser user) {
		return user;
	}

	@ModelAttribute("oauthEnabled")
	public boolean oauthEnabled() {
		return oauthEnabled;
	}
}
