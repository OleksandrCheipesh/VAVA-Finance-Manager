package org.example.model.database.service;

import org.example.logging.AppLog;
import org.example.model.database.ConnectionProvider;
import org.example.model.database.DbTime;
import org.example.model.database.entity.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyService {

    public Company addCompany(Company company) throws SQLException {
        var logger = AppLog.getLogger(CompanyService.class);
        String sql = "INSERT INTO companies (name, industry, country, currency) " +
                     "VALUES (?, ?, ?, ?) RETURNING id, created_at";

        Connection connection = null;
        boolean originalAutoCommit = true;
        try {
            connection = ConnectionProvider.getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, company.getName());
                preparedStatement.setString(2, company.getIndustry());
                preparedStatement.setString(3, company.getCountry());
                preparedStatement.setString(4, company.getCurrency());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        company.setId(resultSet.getInt("id"));
                        company.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
                    }
                }
            }

            // create a default account for the new company to avoid null account_id issues
            String accSql = "INSERT INTO accounts (company_id, account_name, currency, limit_amount, category, cycle) " +
                            "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";
            try (PreparedStatement psAcc = connection.prepareStatement(accSql)) {
                psAcc.setInt(1, company.getId());
                String defaultAccountName = company.getName() + " - Default";
                psAcc.setString(2, defaultAccountName);
                psAcc.setString(3, company.getCurrency());
                psAcc.setNull(4, Types.INTEGER);
                psAcc.setString(5, "OTHER");
                psAcc.setString(6, "MONTHLY");

                try (ResultSet rs = psAcc.executeQuery()) {
                    if (rs.next()) {
                        int accountId = rs.getInt("id");
                        logger.info("Default account created for company id={} account_id={}", company.getId(), accountId);
                    }
                }
            }

            connection.commit();
            logger.info("Company created: id={} name={}", company.getId(), company.getName());
            return company;
        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            logger.error("Failed to create company {}", company == null ? "<null>" : company.getName(), e);
            throw e;
        } finally {
            if (connection != null) {
                try { connection.setAutoCommit(originalAutoCommit); connection.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public Optional<Company> getCompanyById(int id) throws SQLException {
        String sql = "SELECT * FROM companies WHERE id = ?";
        var logger = AppLog.getLogger(CompanyService.class);

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Company c = mapRow(resultSet);
                    logger.info("Company loaded: id={} name={}", c.getId(), c.getName());
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load company by id={}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<Company> getAllCompanies() throws SQLException {
        String sql = "SELECT * FROM companies ORDER BY id";
        List<Company> list = new ArrayList<>();
        var logger = AppLog.getLogger(CompanyService.class);

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
            logger.info("Loaded {} companies", list.size());
        } catch (SQLException e) {
            logger.error("Failed to load all companies", e);
            throw e;
        }

        return list;
    }

    public boolean updateCompany(Company company) throws SQLException {
        String sql = "UPDATE companies SET name = ?, industry = ?, country = ?, currency = ? WHERE id = ?";
        var logger = AppLog.getLogger(CompanyService.class);
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, company.getName());
            preparedStatement.setString(2, company.getIndustry());
            preparedStatement.setString(3, company.getCountry());
            preparedStatement.setString(4, company.getCurrency());
            preparedStatement.setInt(5, company.getId());
            boolean updated = preparedStatement.executeUpdate() > 0;
            if (updated) logger.info("Company updated: id={} name={}", company.getId(), company.getName());
            return updated;
        } catch (SQLException e) {
            logger.error("Failed to update company id={}", company.getId(), e);
            throw e;
        }
    }

    public boolean deleteCompany(int id) throws SQLException {
        String sql = "DELETE FROM companies WHERE id = ?";
        var logger = AppLog.getLogger(CompanyService.class);

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            boolean deleted = preparedStatement.executeUpdate() > 0;
            if (deleted) logger.info("Company deleted: id={}", id);
            return deleted;
        } catch (SQLException e) {
            logger.error("Failed to delete company id={}", id, e);
            throw e;
        }
    }

    private Company mapRow(ResultSet resultSet) throws SQLException {
        Company company = new Company();
        company.setId(resultSet.getInt("id"));
        company.setName(resultSet.getString("name"));
        company.setIndustry(resultSet.getString("industry"));
        company.setCountry(resultSet.getString("country"));
        company.setCurrency(resultSet.getString("currency"));
        company.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
        return company;
    }
}

