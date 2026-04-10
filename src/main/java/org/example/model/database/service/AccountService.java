package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountService {

    public Account addAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (company_id, account_name, current_balance, currency, limit_amount, category, cycle) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, account.getCompanyId());
            preparedStatement.setString(2, account.getAccountName());
            preparedStatement.setBigDecimal(3, account.getCurrentBalance());
            preparedStatement.setString(4, account.getCurrency());

            if (account.getLimitAmount() != null) {
                preparedStatement.setInt(5, account.getLimitAmount());
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }

            preparedStatement.setString(6, account.getCategory() != null ? account.getCategory().name() : "OTHER");
            preparedStatement.setString(7, account.getCycle() != null ? account.getCycle().name() : "MONTHLY");

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
                "current_balance = ?, currency = ?, limit_amount = ?, category = ?, cycle = ? WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, account.getCompanyId());
            preparedStatement.setString(2, account.getAccountName());
            preparedStatement.setBigDecimal(3, account.getCurrentBalance());
            preparedStatement.setString(4, account.getCurrency());

            if (account.getLimitAmount() != null) {
                preparedStatement.setInt(5, account.getLimitAmount());
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }
            preparedStatement.setString(6, account.getCategory() != null ? account.getCategory().name() : "OTHER");
            preparedStatement.setString(7, account.getCycle() != null ? account.getCycle().name() : "MONTHLY");
            preparedStatement.setInt(8, account.getId());
            return preparedStatement.executeUpdate() > 0;
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
        int lim = resultSet.getInt("limit_amount");
        if (!resultSet.wasNull()) {
            account.setLimitAmount(lim);
        }

        String category = resultSet.getString("category");
        if (category != null) {
            try {
                account.setCategory(org.example.model.database.entity.AccountCategory.valueOf(category));
            } catch (IllegalArgumentException _) {}
        }

        String cycle = resultSet.getString("cycle");
        if (cycle != null) {
            try {
                account.setCycle(org.example.model.database.entity.AccountCycle.valueOf(cycle));
            } catch (IllegalArgumentException _) {}
        }
        return account;
    }
}

