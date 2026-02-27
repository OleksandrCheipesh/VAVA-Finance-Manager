package org.example.model.database;

import org.flywaydb.core.Flyway;

public final class Migrations {
    private Migrations() {}

    public static void migrate(DatabaseConfig cfg) {
        Flyway flyway = Flyway.configure()
                .dataSource(cfg.url(), cfg.user(), cfg.password())
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }
}