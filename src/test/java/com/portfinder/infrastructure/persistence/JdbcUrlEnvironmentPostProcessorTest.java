package com.portfinder.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

class JdbcUrlEnvironmentPostProcessorTest {

	@Test
	void convertsLibpqDatabaseUrlToJdbc() {
		StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource(
				"test",
				Map.of("DATABASE_URL", "postgres://portfinder:s3cret@dpg-xyz.render.com:5432/portfinder")));

		new JdbcUrlEnvironmentPostProcessor().postProcessEnvironment(environment, new SpringApplication());

		assertThat(environment.getProperty("DATABASE_URL"))
				.isEqualTo("jdbc:postgresql://dpg-xyz.render.com:5432/portfinder?sslmode=require");
		assertThat(environment.getProperty("spring.datasource.url"))
				.isEqualTo("jdbc:postgresql://dpg-xyz.render.com:5432/portfinder?sslmode=require");
		assertThat(environment.getProperty("spring.datasource.username")).isEqualTo("portfinder");
		assertThat(environment.getProperty("spring.datasource.password")).isEqualTo("s3cret");
		assertThat(environment.getProperty("spring.datasource.driver-class-name"))
				.isEqualTo("org.postgresql.Driver");
		assertThat(environment.getProperty("DATABASE_DRIVER")).isEqualTo("org.postgresql.Driver");
	}

	@Test
	void convertsInternalHostWithoutPort() throws Exception {
		Map<String, Object> props = JdbcUrlEnvironmentPostProcessor.normalize(
				"postgresql://portfinder:secret@dpg-abc-a/portfinder");

		assertThat(props.get("spring.datasource.url"))
				.isEqualTo("jdbc:postgresql://dpg-abc-a:5432/portfinder?sslmode=require");
		assertThat(props.get("DATABASE_URL"))
				.isEqualTo("jdbc:postgresql://dpg-abc-a:5432/portfinder?sslmode=require");
	}

	@Test
	void leavesJdbcUrlsUntouched() {
		StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource(
				"test",
				Map.of(
						"DATABASE_URL",
						"jdbc:postgresql://localhost:5432/portfinder",
						"spring.datasource.username",
						"local")));

		new JdbcUrlEnvironmentPostProcessor().postProcessEnvironment(environment, new SpringApplication());

		assertThat(environment.getProperty("DATABASE_URL")).isEqualTo("jdbc:postgresql://localhost:5432/portfinder");
		assertThat(environment.getProperty("spring.datasource.username")).isEqualTo("local");
	}
}
