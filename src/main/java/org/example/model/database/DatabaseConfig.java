package org.example.model.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DatabaseConfig {
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig fromClasspath(String resourcePath) {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalStateException("Config not found: " + resourcePath);
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String url = require(props, "db.url");
        String user = require(props, "db.user");
        String password = require(props, "db.password");
        return new DatabaseConfig(url, user, password);
    }

    private static String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing property: " + key);
        return value;
    }

    public String url() { return url; }
    public String user() { return user; }
    public String password() { return password; }
}