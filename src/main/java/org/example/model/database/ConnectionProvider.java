package org.example.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionProvider {
    private static DatabaseConfig config;

    private ConnectionProvider() {}

    public static void init(DatabaseConfig config) {
        ConnectionProvider.config = config;
    }

    public static Connection getConnection() throws SQLException {
        if (config == null) {
            throw new IllegalStateException("ConnectionProvider not initialized.");
        }
        return DriverManager.getConnection(config.url(), config.user(), config.password());
    }
}

