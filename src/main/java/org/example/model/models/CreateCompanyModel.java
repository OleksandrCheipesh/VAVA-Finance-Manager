package org.example.model.models;

import org.example.SessionManager;
import org.example.logging.AppLog;
import org.example.model.database.ConnectionProvider;
import org.example.model.database.DbTime;
import org.example.model.database.entity.Company;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.User;

import java.sql.*;
import java.util.Objects;

public class CreateCompanyModel {
    private static final String EMAIL_EXISTS_MESSAGE = "Email already registered. Please log in or use a different email.";
    private final SessionManager.PendingRegistration pendingRegistration;
    private final Company company = new Company();

    public CreateCompanyModel(SessionManager.PendingRegistration pendingRegistration,
                              String companyName,
                              String industry,
                              String country,
                              String currency) {
        this.pendingRegistration = Objects.requireNonNull(pendingRegistration, "pendingRegistration");
        this.company.setName(companyName);
        this.company.setIndustry(industry);
        this.company.setCountry(country);
        this.company.setCurrency(currency);
    }

    public void createCompany() throws SQLException {
        var logger = AppLog.getLogger(CreateCompanyModel.class);
        Connection connection = null;
        boolean originalAutoCommit = true;

        try {
            connection = ConnectionProvider.getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            Company savedCompany = insertCompany(connection);
            insertDefaultAccount(connection, savedCompany);
            User savedUser = insertUser(connection, savedCompany.getId());

            connection.commit();

            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.login(savedUser, savedCompany);
            sessionManager.clearPendingRegistration();

            logger.info("Company created: id={} name={}", savedCompany.getId(), savedCompany.getName());
            logger.info("Registration completed for user id={} email={}", savedUser.getId(), savedUser.getEmail());
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    logger.warn("Rollback failed while creating company {}: {}", company.getName(), rollbackException.getMessage());
                }
            }
            logger.error("Failed to create company {}", company.getName(), e);
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private Company insertCompany(Connection connection) throws SQLException {
        String sql = "INSERT INTO companies (name, industry, country, currency) " +
                "VALUES (?, ?, ?, ?) RETURNING id, created_at";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, company.getName());
            preparedStatement.setString(2, company.getIndustry());
            preparedStatement.setString(3, company.getCountry());
            preparedStatement.setString(4, company.getCurrency());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    company.setId(resultSet.getInt("id"));
                    company.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
                    return company;
                }
            }
        }

        throw new SQLException("Failed to insert company: no generated row returned");
    }

    private void insertDefaultAccount(Connection connection, Company savedCompany) throws SQLException {
        String accSql = "INSERT INTO accounts (company_id, account_name, currency, limit_amount, category, cycle) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";

        try (PreparedStatement psAcc = connection.prepareStatement(accSql)) {
            psAcc.setInt(1, savedCompany.getId());
            psAcc.setString(2, savedCompany.getName() + " - Default");
            psAcc.setString(3, savedCompany.getCurrency());
            psAcc.setNull(4, Types.INTEGER);
            psAcc.setString(5, "OTHER");
            psAcc.setString(6, "MONTHLY");

            try (ResultSet rs = psAcc.executeQuery()) {
                if (rs.next()) {
                    int accountId = rs.getInt("id");
                    var logger = AppLog.getLogger(CreateCompanyModel.class);
                    logger.info("Default account created for company id={} account_id={}", savedCompany.getId(), accountId);
                    return;
                }
            }
        }

        throw new SQLException("Failed to create default account for company id=" + savedCompany.getId());
    }

    private User insertUser(Connection connection, int companyId) throws SQLException {
        String sql = "INSERT INTO users (name, surname, email, password_hash, position, company_id) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";

        User user = new User(
                pendingRegistration.name(),
                pendingRegistration.surname(),
                pendingRegistration.email(),
                pendingRegistration.passwordHash(),
                Position.Director,
                companyId
        );

        assertEmailAvailable(connection, user.getEmail());

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getPosition().name());
            statement.setInt(6, companyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user.setId(resultSet.getInt("id"));
                    user.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            if (isUniqueEmailViolation(e)) {
                throw new IllegalStateException(EMAIL_EXISTS_MESSAGE, e);
            }
            throw e;
        }

        throw new SQLException("Failed to insert user for company id=" + companyId);
    }

    private void assertEmailAvailable(Connection connection, String email) throws SQLException {
        String checkSql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalStateException(EMAIL_EXISTS_MESSAGE);
                }
            }
        }
    }

    private boolean isUniqueEmailViolation(SQLException e) {
        String message = e.getMessage();
        return message != null && message.contains("users.email");
    }
}