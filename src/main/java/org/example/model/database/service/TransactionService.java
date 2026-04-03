package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionService {

    private final AccountService accountService = new AccountService();

    private void validate(Transaction t) {
        if (t == null)
            throw new IllegalArgumentException("Transaction must not be null");
        if (t.getAccountId() <= 0)
            throw new IllegalArgumentException("Account ID must be positive");
        String type = t.getType();
        if (type == null || type.isBlank() || (!type.equalsIgnoreCase("SALE") && !type.equalsIgnoreCase("PURCHASE")))
            throw new IllegalArgumentException("Type must be SALE or PURCHASE");
        if (t.getAmount() == null || t.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        if (t.getDate() == null)
            throw new IllegalArgumentException("Date is required");
        if (t.getDescription() == null || t.getDescription().isBlank())
            throw new IllegalArgumentException("Description is required");
    }

    public Transaction addTransaction(Transaction transaction) throws SQLException {
        validate(transaction);
        String sql = "INSERT INTO transactions (company_id, account_id, project_id, client_id, type, amount, description, date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, transaction.getCompanyId());
            preparedStatement.setInt(2, transaction.getAccountId());

            if (transaction.getProjectId() != null)
                preparedStatement.setInt(3, transaction.getProjectId());
            else
                preparedStatement.setNull(3, Types.INTEGER);

            if (transaction.getClientId() != null)
                preparedStatement.setInt(4, transaction.getClientId());
            else
                preparedStatement.setNull(4, Types.INTEGER);

            preparedStatement.setString(5, transaction.getType());
            preparedStatement.setBigDecimal(6, transaction.getAmount());
            preparedStatement.setString(7, transaction.getDescription());
            preparedStatement.setDate(8, Date.valueOf(transaction.getDate()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    transaction.setId(resultSet.getInt("id"));
                    transaction.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
                }
            }
        }
        BigDecimal delta = "SALE".equalsIgnoreCase(transaction.getType())
                ? transaction.getAmount()
                : transaction.getAmount().negate();
        accountService.updateBalance(transaction.getAccountId(), delta);
        return transaction;
    }

    public Optional<Transaction> getTransactionById(int id) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE id = ?";

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

    public List<Transaction> getTransactionsByCompanyId(int companyId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE company_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

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

    public List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        }
        return list;
    }

    public List<Transaction> getTransactionsByProjectId(int projectId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE project_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, projectId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        }
        return list;
    }

    public List<Transaction> getTransactionsByClientId(int clientId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE client_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, clientId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        }
        return list;
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                list.add(mapRow(resultSet));
        }
        return list;
    }

    public boolean updateTransaction(Transaction transaction) throws SQLException {
        validate(transaction);
        if (transaction.getId() <= 0)
            throw new IllegalArgumentException("Transaction ID must be positive for update");
        String sql = "UPDATE transactions SET company_id = ?, account_id = ?, project_id = ?, client_id = ?, " +
                     "type = ?, amount = ?, description = ?, date = ? WHERE id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, transaction.getCompanyId());
            preparedStatement.setInt(2, transaction.getAccountId());

            if (transaction.getProjectId() != null)
                preparedStatement.setInt(3, transaction.getProjectId());
            else
                preparedStatement.setNull(3, Types.INTEGER);

            if (transaction.getClientId() != null)
                preparedStatement.setInt(4, transaction.getClientId());
            else
                preparedStatement.setNull(4, Types.INTEGER);

            preparedStatement.setString(5, transaction.getType());
            preparedStatement.setBigDecimal(6, transaction.getAmount());
            preparedStatement.setString(7, transaction.getDescription());
            preparedStatement.setDate(8, Date.valueOf(transaction.getDate()));
            preparedStatement.setInt(9, transaction.getId());

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteTransaction(int id) throws SQLException {
        if (id <= 0)
            throw new IllegalArgumentException("Transaction ID must be positive for delete");

        Optional<Transaction> existing = getTransactionById(id);

        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            boolean deleted = preparedStatement.executeUpdate() > 0;
            if (deleted && existing.isPresent()) {
                Transaction t = existing.get();
                BigDecimal delta = "SALE".equalsIgnoreCase(t.getType())
                        ? t.getAmount().negate()
                        : t.getAmount();
                accountService.updateBalance(t.getAccountId(), delta);
            }
            return deleted;
        }
    }

    private Transaction mapRow(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setCompanyId(resultSet.getInt("company_id"));
        transaction.setAccountId(resultSet.getInt("account_id"));

        int projectId = resultSet.getInt("project_id");
        transaction.setProjectId(resultSet.wasNull() ? null : projectId);

        int clientId = resultSet.getInt("client_id");
        transaction.setClientId(resultSet.wasNull() ? null : clientId);

        transaction.setType(resultSet.getString("type"));
        transaction.setAmount(resultSet.getBigDecimal("amount"));
        transaction.setDescription(resultSet.getString("description"));

        Date date = resultSet.getDate("date");
        transaction.setDate(date != null ? date.toLocalDate() : null);

        transaction.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
        return transaction;
    }
}

