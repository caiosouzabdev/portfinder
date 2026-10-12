package com.portfinder.infrastructure.persistence;

import java.net.URI;
import java.net.URISyntaxException;
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
 */
public class JdbcUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String raw = firstNonBlank(
				environment.getProperty("DATABASE_URL"),
				environment.getProperty("spring.datasource.url"));
		if (raw == null || !isLibpqStyle(raw)) {
			return;
		}

		try {
			URI uri = new URI(raw);
			String userInfo = uri.getUserInfo();
			String username = null;
			String password = null;
			if (userInfo != null) {
				int colon = userInfo.indexOf(':');
				if (colon >= 0) {
					username = userInfo.substring(0, colon);
					password = userInfo.substring(colon + 1);
				}
				else {
					username = userInfo;
				}
			}

			String host = uri.getHost();
			int port = uri.getPort() > 0 ? uri.getPort() : 5432;
			String path = uri.getPath() == null ? "" : uri.getPath();
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

			Map<String, Object> props = new HashMap<>();
			props.put("spring.datasource.url", jdbc.toString());
			props.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
			if (username != null) {
				props.put("spring.datasource.username", username);
			}
			if (password != null) {
				props.put("spring.datasource.password", password);
			}
			environment.getPropertySources()
					.addFirst(new MapPropertySource("jdbcUrlNormalization", props));
		}
		catch (URISyntaxException ignored) {
			// Leave original URL for Spring to surface a clear failure.
		}
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
		return Ordered.HIGHEST_PRECEDENCE + 10;
	}
}
