package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.DbTime;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    // create user and return it
    public User addUser(User user) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(UserService.class);
        try {
            String sql = "INSERT INTO users (name, surname, email, password_hash, position, company_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";

            try (Connection connection = ConnectionProvider.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, user.getName());
                statement.setString(2, user.getSurname());
                statement.setString(3, user.getEmail());
                statement.setString(4, user.getPasswordHash());
                statement.setString(5, user.getPosition().name());

                if (user.getCompanyId() != null)
                    statement.setInt(6, user.getCompanyId());
                else
                    statement.setNull(6, Types.INTEGER);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        user.setId(resultSet.getInt("id"));
                        user.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
                    }
                }
            }
            logger.info("User created: id={} email={}", user.getId(), user.getEmail());
            return user;
        } catch (SQLException e) {
            logger.error("Failed to create user {}", user == null ? "<null>" : user.getEmail(), e);
            throw e;
        }
    }
    // get all users
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> list = new ArrayList<>();
        var logger = org.example.logging.AppLog.getLogger(UserService.class);
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load all users", e);
            throw e;
        }

        return list;
    }

    // get user by id
    public Optional<User> getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        var logger = org.example.logging.AppLog.getLogger(UserService.class);
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load user by id={}", id, e);
            throw e;
        }

        return Optional.empty();
    }

    // get user by email
    public Optional<User> getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        var logger = org.example.logging.AppLog.getLogger(UserService.class);
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load user by email={}", email, e);
            throw e;
        }

        return Optional.empty();
    }

    // get users by company id
    public List<User> getUsersByCompanyId(int companyId) throws SQLException {
        String sql = "SELECT * FROM users WHERE company_id = ? ORDER BY id";
        List<User> list = new ArrayList<>();
        var logger = org.example.logging.AppLog.getLogger(UserService.class);
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load users by companyId={}", companyId, e);
            throw e;
        }
        return list;
    }

    // update user
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, surname = ?, email = ?, password_hash = ?, " +
                     "position = ?, company_id = ? WHERE id = ?";
        var logger = org.example.logging.AppLog.getLogger(UserService.class);
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getSurname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPasswordHash());
            preparedStatement.setString(5, user.getPosition().name());

            if (user.getCompanyId() != null) {
                preparedStatement.setInt(6, user.getCompanyId());
            } else {
                preparedStatement.setNull(6, Types.INTEGER);
            }

            preparedStatement.setInt(7, user.getId());
            boolean updated = preparedStatement.executeUpdate() > 0;
            if (updated) logger.info("User updated: id={} email={}", user.getId(), user.getEmail());
            return updated;
        } catch (SQLException e) {
            logger.error("Failed to update user id={}", user.getId(), e);
            throw e;
        }
    }

    // delete user by id
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        var logger = org.example.logging.AppLog.getLogger(UserService.class);
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            boolean deleted = preparedStatement.executeUpdate() > 0;
            if (deleted) logger.info("User deleted: id={}", id);
            return deleted;
        } catch (SQLException e) {
            logger.error("Failed to delete user id={}", id, e);
            throw e;
        }
    }

    public boolean updateLanguage(int userId, String language) throws SQLException {
        String sql = "UPDATE users SET language = ? WHERE id = ?";
        var logger = org.example.logging.AppLog.getLogger(UserService.class);
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, language);
            preparedStatement.setInt(2, userId);
            boolean updated = preparedStatement.executeUpdate() > 0;
            if (updated) logger.info("User language updated: id={} language={}", userId, language);
            return updated;
        } catch (SQLException e) {
            logger.error("Failed to update language for user id={}", userId, e);
            throw e;
        }
    }

    // maps ResultSet row to User object
    private User mapRow(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setSurname(resultSet.getString("surname"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setPosition(Position.valueOf(resultSet.getString("position")));

        int companyId = resultSet.getInt("company_id");
        user.setCompanyId(resultSet.wasNull() ? null : companyId);
        user.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
        user.setLanguage(resultSet.getString("language"));
        return user;
    }
}
