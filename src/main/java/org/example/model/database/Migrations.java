package org.example.model.database;

import org.flywaydb.core.Flyway;
import org.example.logging.AppLog;

public final class Migrations {
    private Migrations() {}

    public static void migrate(DatabaseConfig cfg) {
        var logger = AppLog.getLogger(Migrations.class);
        logger.info("Starting database migrations");

        Flyway flyway = Flyway.configure()
                .dataSource(cfg.url(), cfg.user(), cfg.password())
                .locations("classpath:db/migration")
                .load();
        try {
            flyway.migrate();
            logger.info("Database migrations completed");
        } catch (Exception e) {
            logger.error("Database migration failed", e);
            throw e;
        }
    }
}