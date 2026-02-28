package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    // create user and return it
    public User addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, surname, email, password_hash, position, company_id) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";

        try (Connection connection = ConnectionProvider.getConnection();
             // prepares a statement with the SQL query, allowing to set parameters and execute it
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getPosition());

            if (user.getCompanyId() != null) {
                statement.setInt(6, user.getCompanyId());
            } else {
                statement.setNull(6, Types.INTEGER);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                // checks if the result set has a row (the insert was successful and returned generated keys)
                if (resultSet.next()) {
                    user.setId(resultSet.getInt("id"));
                    user.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
                }
            }
        }
        return user;
    }

    // get all users
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
        }

        return list;
    }

    // get user by id
    public Optional<User> getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    // get user by email
    public Optional<User> getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    // update user
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, surname = ?, email = ?, password_hash = ?, " +
                     "position = ?, company_id = ? WHERE id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getSurname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPasswordHash());
            preparedStatement.setString(5, user.getPosition());

            if (user.getCompanyId() != null) {
                preparedStatement.setInt(6, user.getCompanyId());
            } else {
                preparedStatement.setNull(6, Types.INTEGER);
            }

            preparedStatement.setInt(7, user.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    // delete user by id
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
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
        user.setPosition(resultSet.getString("position"));

        int companyId = resultSet.getInt("company_id");
        user.setCompanyId(resultSet.wasNull() ? null : companyId);
        user.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
        return user;
    }
}

