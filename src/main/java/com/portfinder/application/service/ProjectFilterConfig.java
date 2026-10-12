package com.portfinder.application.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectFilterConfig {

	@Bean
	ProjectFilter projectFilter() {
		return new ProjectFilter();
	}
}
