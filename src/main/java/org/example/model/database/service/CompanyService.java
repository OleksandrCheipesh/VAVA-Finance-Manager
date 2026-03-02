package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyService {

    public Company addCompany(Company company) throws SQLException {
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
        }
        return company;
    }

    public Optional<Company> getCompanyById(int id) throws SQLException {
        String sql = "SELECT * FROM companies WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapRow(resultSet));
            }
        }
        return Optional.empty();
    }

    public List<Company> getAllCompanies() throws SQLException {
        String sql = "SELECT * FROM companies ORDER BY id";
        List<Company> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
        }

        return list;
    }

    public boolean updateCompany(Company company) throws SQLException {
        String sql = "UPDATE companies SET name = ?, industry = ?, country = ?, currency = ? WHERE id = ?";
        
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, company.getName());
            preparedStatement.setString(2, company.getIndustry());
            preparedStatement.setString(3, company.getCountry());
            preparedStatement.setString(4, company.getCurrency());
            preparedStatement.setInt(5, company.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteCompany(int id) throws SQLException {
        String sql = "DELETE FROM companies WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
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

