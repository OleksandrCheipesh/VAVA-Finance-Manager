package org.example.model.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DbTime {
    private static final DateTimeFormatter SQLITE_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DbTime() {}

    public static OffsetDateTime readOffsetDateTime(ResultSet resultSet, String column) throws SQLException {
        Timestamp ts = resultSet.getTimestamp(column);
        if (ts != null) {
            return ts.toInstant().atOffset(ZoneOffset.UTC);
        }

        String raw = resultSet.getString(column);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw);
        } catch (DateTimeParseException ignored) {
            return OffsetDateTime.of(LocalDateTime.parse(raw, SQLITE_DATETIME), ZoneOffset.UTC);
        }
    }
}

