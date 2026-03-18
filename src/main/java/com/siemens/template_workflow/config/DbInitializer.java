package com.siemens.template_workflow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Configuration
public class DbInitializer {
    private static final Logger log = LoggerFactory.getLogger(DbInitializer.class);

    private final DataSource dataSource;

    @Value("${app.db.init:false}")
    private boolean initDb;

    public DbInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        if (!initDb) {
            log.info("app.db.init is false — skipping custom DB initialization");
            return;
        }

        try (Connection c = dataSource.getConnection()) {
            log.info("Running custom DB initializer using V1__init.sql");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("db/migration/V1__init.sql"));
            populator.execute(dataSource);

            log.info("Listing public tables after initialization:");
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='public' ORDER BY table_name")) {
                while (rs.next()) {
                    log.info("table: {}", rs.getString(1));
                }
            }
        } catch (Exception e) {
            log.error("Error during DB initialization", e);
            throw new RuntimeException(e);
        }
    }
}

