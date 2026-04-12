package org.example.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.example.logging.AppLog;

public final class ConnectionProvider {
    private static DatabaseConfig config;

    private ConnectionProvider() {}

    public static void init(DatabaseConfig config) {
        var logger = AppLog.getLogger(ConnectionProvider.class);
        ConnectionProvider.config = config;
        logger.info("ConnectionProvider initialized");
    }

    public static Connection getConnection() throws SQLException {
        var logger = AppLog.getLogger(ConnectionProvider.class);
        if (config == null) {
            logger.error("ConnectionProvider not initialized.");
            throw new IllegalStateException("ConnectionProvider not initialized.");
        }
        try {
            Connection conn = DriverManager.getConnection(config.url(), config.user(), config.password());
            logger.debug("Obtained new database connection");
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to obtain database connection", e);
            throw e;
        }
    }
}

