package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountService {

    public Account addAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (company_id, account_name, current_balance, currency) " +
                     "VALUES (?, ?, ?, ?) RETURNING id, created_at";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, account.getCompanyId());
            preparedStatement.setString(2, account.getAccountName());
            preparedStatement.setBigDecimal(3, account.getCurrentBalance());
            preparedStatement.setString(4, account.getCurrency());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    account.setId(resultSet.getInt("id"));
                    account.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
                }
            }
        }
        return account;
    }

    public List<Account> getAllAccounts() throws SQLException {
        String sql = "SELECT * FROM accounts ORDER BY id";
        List<Account> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
        }
        return list;
    }

    public Optional<Account> getAccountById(int id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";

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

    public List<Account> getAccountsByCompanyId(int companyId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE company_id = ? ORDER BY id";
        List<Account> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapRow(resultSet));
                }
            }
        }
        return list;
    }

    public boolean updateAccount(Account account) throws SQLException {
        String sql = "UPDATE accounts SET company_id = ?, account_name = ?, " +
                "current_balance = ?, currency = ? WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, account.getCompanyId());
            preparedStatement.setString(2, account.getAccountName());
            preparedStatement.setBigDecimal(3, account.getCurrentBalance());
            preparedStatement.setString(4, account.getCurrency());
            preparedStatement.setInt(5, account.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public void updateBalance(int accountId, BigDecimal delta) throws SQLException {
        String sql = "UPDATE accounts SET current_balance = current_balance + ? WHERE id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBigDecimal(1, delta);
            preparedStatement.setInt(2, accountId);
            preparedStatement.executeUpdate();
        }
    }

    public boolean deleteAccount(int id) throws SQLException {
        String sql = "DELETE FROM accounts WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    private Account mapRow(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setId(resultSet.getInt("id"));
        account.setCompanyId(resultSet.getInt("company_id"));
        account.setAccountName(resultSet.getString("account_name"));
        account.setCurrentBalance(resultSet.getBigDecimal("current_balance"));
        account.setCurrency(resultSet.getString("currency"));
        account.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
        return account;
    }
}

