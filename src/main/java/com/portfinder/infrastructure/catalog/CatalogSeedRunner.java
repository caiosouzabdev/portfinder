package com.portfinder.infrastructure.catalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.portfinder.domain.Project;
import com.portfinder.infrastructure.persistence.JpaProjectCatalog;

@Component
public class CatalogSeedRunner implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(CatalogSeedRunner.class);

	private final JsonCatalogLoader loader;
	private final JpaProjectCatalog catalog;

	public CatalogSeedRunner(JsonCatalogLoader loader, JpaProjectCatalog catalog) {
		this.loader = loader;
		this.catalog = catalog;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (catalog.count() > 0) {
			log.info("Catalog already seeded ({} projects)", catalog.count());
			return;
		}
		var projects = loader.loadAll();
		for (Project project : projects) {
			catalog.save(project);
		}
		log.info("Seeded {} projects from JSON catalog", projects.size());
	}
}
