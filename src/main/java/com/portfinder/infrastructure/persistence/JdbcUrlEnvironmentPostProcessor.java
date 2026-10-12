package com.portfinder.infrastructure.persistence;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Converts platform {@code DATABASE_URL} values such as {@code postgres://user:pass@host/db}
 * into Spring JDBC properties ({@code jdbc:postgresql://...}).
 * Runs late so overrides win over {@code application.yml} placeholders.
 */
public class JdbcUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	static final String PROPERTY_SOURCE_NAME = "jdbcUrlNormalization";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String raw = firstNonBlank(
				environment.getProperty("DATABASE_URL"),
				environment.getProperty("spring.datasource.url"));
		if (raw == null || !isLibpqStyle(raw)) {
			return;
		}

		try {
			Map<String, Object> props = normalize(raw);
			environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, props));
		}
		catch (URISyntaxException ignored) {
			// Leave original URL for Spring to surface a clear failure.
		}
	}

	static Map<String, Object> normalize(String raw) throws URISyntaxException {
		URI uri = new URI(raw);
		String userInfo = uri.getUserInfo();
		String username = null;
		String password = null;
		if (userInfo != null) {
			int colon = userInfo.indexOf(':');
			if (colon >= 0) {
				username = decode(userInfo.substring(0, colon));
				password = decode(userInfo.substring(colon + 1));
			}
			else {
				username = decode(userInfo);
			}
		}

		String host = uri.getHost();
		if (host == null || host.isBlank()) {
			throw new URISyntaxException(raw, "Missing host");
		}
		int port = uri.getPort() > 0 ? uri.getPort() : 5432;
		String path = uri.getPath() == null || uri.getPath().isBlank() ? "/portfinder" : uri.getPath();
		String query = uri.getQuery();
		StringBuilder jdbc = new StringBuilder("jdbc:postgresql://")
				.append(host)
				.append(':')
				.append(port)
				.append(path);
		if (query == null || query.isBlank()) {
			jdbc.append("?sslmode=require");
		}
		else {
			jdbc.append('?').append(query);
			if (!query.contains("sslmode=")) {
				jdbc.append("&sslmode=require");
			}
		}

		String jdbcUrl = jdbc.toString();
		Map<String, Object> props = new HashMap<>();
		// Override both the env-style key and Spring keys so ${DATABASE_URL} resolves to JDBC.
		props.put("DATABASE_URL", jdbcUrl);
		props.put("spring.datasource.url", jdbcUrl);
		props.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
		props.put("DATABASE_DRIVER", "org.postgresql.Driver");
		if (username != null) {
			props.put("spring.datasource.username", username);
			props.put("DATABASE_USERNAME", username);
		}
		if (password != null) {
			props.put("spring.datasource.password", password);
			props.put("DATABASE_PASSWORD", password);
		}
		return props;
	}

	private static String decode(String value) {
		return URLDecoder.decode(value, StandardCharsets.UTF_8);
	}

	private static boolean isLibpqStyle(String url) {
		return url.startsWith("postgres://") || url.startsWith("postgresql://");
	}

	private static String firstNonBlank(String a, String b) {
		if (a != null && !a.isBlank()) {
			return a;
		}
		if (b != null && !b.isBlank()) {
			return b;
		}
		return null;
	}

	@Override
	public int getOrder() {
		// After application.yml is loaded so addFirst() wins over ${DATABASE_URL} placeholders.
		return Ordered.LOWEST_PRECEDENCE;
	}
}
