package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.DbTime;
import org.example.model.database.entity.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientService {

    public Client addClient(Client client) throws SQLException {
        String sql = "INSERT INTO clients (company_id, name, surname, email, phone, monthly_income) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, client.getCompanyId());
            preparedStatement.setString(2, client.getName());
            preparedStatement.setString(3, client.getSurname());
            preparedStatement.setString(4, client.getEmail());
            preparedStatement.setString(5, client.getPhone());

            if (client.getMonthlyIncome() != null)
                preparedStatement.setBigDecimal(6, client.getMonthlyIncome());
            else
                preparedStatement.setNull(6, Types.NUMERIC);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    client.setId(resultSet.getInt("id"));
                    client.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
                }
            }
        }
        return client;
    }

    public Optional<Client> getClientById(int id) throws SQLException {
        String sql = "SELECT * FROM clients WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next())
                    return Optional.of(mapRow(resultSet));
            }
        }
        return Optional.empty();
    }

    public List<Client> getClientsByCompanyId(int companyId) throws SQLException {
        String sql = "SELECT * FROM clients WHERE company_id = ? ORDER BY id";
        List<Client> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        }
        return list;
    }

    public List<Client> getAllClients() throws SQLException {
        String sql = "SELECT * FROM clients ORDER BY id";
        List<Client> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                list.add(mapRow(resultSet));
        }
        return list;
    }

    public boolean updateClient(Client client) throws SQLException {
        String sql = "UPDATE clients SET company_id = ?, name = ?, surname = ?, email = ?, phone = ?, " +
                "monthly_income = ? WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, client.getCompanyId());
            preparedStatement.setString(2, client.getName());
            preparedStatement.setString(3, client.getSurname());
            preparedStatement.setString(4, client.getEmail());
            preparedStatement.setString(5, client.getPhone());

            if (client.getMonthlyIncome() != null)
                preparedStatement.setBigDecimal(6, client.getMonthlyIncome());
            else
                preparedStatement.setNull(6, Types.NUMERIC);

            preparedStatement.setInt(7, client.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteClient(int id) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    private Client mapRow(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setId(resultSet.getInt("id"));
        client.setCompanyId(resultSet.getInt("company_id"));
        client.setName(resultSet.getString("name"));
        client.setSurname(resultSet.getString("surname"));
        client.setEmail(resultSet.getString("email"));
        client.setPhone(resultSet.getString("phone"));
        client.setMonthlyIncome(resultSet.getBigDecimal("monthly_income"));
        client.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
        return client;
    }
}

