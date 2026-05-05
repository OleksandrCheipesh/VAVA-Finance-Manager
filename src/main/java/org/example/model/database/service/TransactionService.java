package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.DbTime;
import org.example.model.database.entity.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionService {

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
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        validate(transaction);
        String sql = "INSERT INTO transactions (company_id, account_id, project_id, client_id, type, amount, description, date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        logger.info("Creating transaction: companyId={} accountId={} projectId={} clientId={} type={} amount={}",
                transaction.getCompanyId(), transaction.getAccountId(), transaction.getProjectId(), transaction.getClientId(), transaction.getType(), transaction.getAmount());
        try {
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
                        transaction.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
                    }
                }
            }
            logger.info("Transaction created: id={} type={} amount={} projectId={} companyId={}",
                    transaction.getId(), transaction.getType(), transaction.getAmount(), transaction.getProjectId(), transaction.getCompanyId());
            return transaction;
        } catch (SQLException e) {
            logger.error("Failed to create transaction for companyId={} accountId={}", transaction.getCompanyId(), transaction.getAccountId(), e);
            throw e;
        }
    }

    public Optional<Transaction> getTransactionById(int id) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next())
                    return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load transaction by id={}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<Transaction> getTransactionsByCompanyId(int companyId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT * FROM transactions WHERE company_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load transactions for companyId={}", companyId, e);
            throw e;
        }
        return list;
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load transactions for accountId={}", accountId, e);
            throw e;
        }
        return list;
    }

    public List<Transaction> getTransactionsByProjectId(int projectId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT * FROM transactions WHERE project_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, projectId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load transactions for projectId={}", projectId, e);
            throw e;
        }
        return list;
    }

    public List<Transaction> getTransactionsByClientId(int clientId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT * FROM transactions WHERE client_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, clientId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load transactions for clientId={}", clientId, e);
            throw e;
        }
        return list;
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT * FROM transactions ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                list.add(mapRow(resultSet));
        } catch (SQLException e) {
            logger.error("Failed to load all transactions", e);
            throw e;
        }
        return list;
    }

    public boolean updateTransaction(Transaction transaction) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        validate(transaction);
        if (transaction.getId() <= 0)
            throw new IllegalArgumentException("Transaction ID must be positive for update");
        Transaction before = getTransactionById(transaction.getId()).orElse(null);
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

            boolean updated = preparedStatement.executeUpdate() > 0;
            if (updated) {
                StringBuilder changes = new StringBuilder();
                if (before != null) {
                    if (!java.util.Objects.equals(before.getType(), transaction.getType())) changes.append(String.format("type: %s->%s; ", before.getType(), transaction.getType()));
                    if (before.getAmount() == null || transaction.getAmount() == null || before.getAmount().compareTo(transaction.getAmount()) != 0)
                        changes.append(String.format("amount: %s->%s; ", before.getAmount(), transaction.getAmount()));
                    if (!java.util.Objects.equals(before.getProjectId(), transaction.getProjectId())) changes.append(String.format("projectId: %s->%s; ", before.getProjectId(), transaction.getProjectId()));
                    if (!java.util.Objects.equals(before.getClientId(), transaction.getClientId())) changes.append(String.format("clientId: %s->%s; ", before.getClientId(), transaction.getClientId()));
                    if (!java.util.Objects.equals(before.getDescription(), transaction.getDescription())) changes.append("description changed; ");
                    if (!java.util.Objects.equals(before.getDate(), transaction.getDate())) changes.append(String.format("date: %s->%s; ", before.getDate(), transaction.getDate()));
                }
                logger.info("Transaction updated: id={} changes={}", transaction.getId(), changes.toString());
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Failed to update transaction id={}", transaction.getId(), e);
            throw e;
        }
    }

    public boolean deleteTransaction(int id) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        if (id <= 0)
            throw new IllegalArgumentException("Transaction ID must be positive for delete");
        Transaction before = getTransactionById(id).orElse(null);
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            boolean deleted = preparedStatement.executeUpdate() > 0;
            if (deleted) {
                if (before != null) {
                    logger.info("Transaction deleted: id={} amount={} type={} projectId={} companyId={}", before.getId(), before.getAmount(), before.getType(), before.getProjectId(), before.getCompanyId());
                } else {
                    logger.info("Transaction deleted: id={}", id);
                }
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Failed to delete transaction id={}", id, e);
            throw e;
        }
    }

    public java.util.Map<Integer, BigDecimal> getBalancesByCompanyId(int companyId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT account_id, COALESCE(SUM(CASE WHEN type = 'SALE' THEN amount ELSE -amount END), 0) " +
                     "FROM transactions WHERE company_id = ? GROUP BY account_id";
        java.util.Map<Integer, BigDecimal> map = new java.util.HashMap<>();
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getInt(1), rs.getBigDecimal(2));
            }
        } catch (SQLException e) {
            logger.error("Failed to compute per-account balances for companyId={}", companyId, e);
            throw e;
        }
        return map;
    }

    public BigDecimal getTotalBalanceByCompanyId(int companyId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT COALESCE(SUM(CASE WHEN type = 'SALE' THEN amount ELSE -amount END), 0) " +
                     "FROM transactions WHERE company_id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            logger.error("Failed to compute total balance for companyId={}", companyId, e);
            throw e;
        }
        return BigDecimal.ZERO;
    }

    public java.util.Optional<BigDecimal> getPrevMonthTotalByCompanyId(int companyId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT COALESCE(SUM(CASE WHEN type = 'SALE' THEN amount ELSE -amount END), 0), " +
                     "COUNT(*) FROM transactions WHERE company_id = ? AND date < date('now','start of month')";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getLong(2) > 0) {
                    return java.util.Optional.of(rs.getBigDecimal(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to compute prev-month total for companyId={}", companyId, e);
            throw e;
        }
        return java.util.Optional.empty();
    }

    public BigDecimal getCurrentMonthTotalByCompanyId(int companyId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        String sql = "SELECT COALESCE(SUM(CASE WHEN type = 'SALE' THEN amount ELSE -amount END), 0) " +
                     "FROM transactions WHERE company_id = ? AND date >= date('now','start of month')";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            logger.error("Failed to compute current-month total for companyId={}", companyId, e);
            throw e;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getCreditUtilizationByAccountId(int accountId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(TransactionService.class);
        if (accountId <= 0) {
            throw new IllegalArgumentException("Account ID must be positive");
        }

        String sql = "SELECT COALESCE(SUM(t.amount), 0) " +
                "FROM transactions t " +
                "JOIN accounts a ON a.id = t.account_id " +
                "WHERE t.account_id = ? " +
                "AND t.type = 'PURCHASE' " +
                "AND t.date >= CASE " +
                "    WHEN a.cycle = 'WEEKLY' THEN date('now','weekday 1','-7 days') " +
                "    ELSE date('now','start of month') " +
                "END";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to compute credit utilization for accountId={}", accountId, e);
            throw e;
        }
        return BigDecimal.ZERO;
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

        transaction.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
        return transaction;
    }


}

