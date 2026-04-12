package org.example.model.database.service;

import org.example.logging.AppLog;
import org.example.model.database.ConnectionProvider;
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

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, company.getName());
            preparedStatement.setString(2, company.getIndustry());
            preparedStatement.setString(3, company.getCountry());
            preparedStatement.setString(4, company.getCurrency());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    company.setId(resultSet.getInt("id"));
                    company.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
                }
            }

            logger.info("Company created: id={} name={}", company.getId(), company.getName());
            return company;
        } catch (SQLException e) {
            logger.error("Failed to create company {}", company == null ? "<null>" : company.getName(), e);
            throw e;
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
        company.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
        return company;
    }
}

