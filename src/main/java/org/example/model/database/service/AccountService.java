package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.DbTime;
import org.example.model.database.entity.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountService {

    public Account addAccount(Account account) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(AccountService.class);
        String sql = "INSERT INTO accounts (company_id, account_name, currency, limit_amount, category, cycle) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        logger.info("Adding account: name={} companyId={} currency={}", account.getAccountName(), account.getCompanyId(), account.getCurrency());
        try {
            try (Connection connection = ConnectionProvider.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, account.getCompanyId());
                preparedStatement.setString(2, account.getAccountName());
                preparedStatement.setString(3, account.getCurrency());

                if (account.getLimitAmount() != null) {
                    preparedStatement.setInt(4, account.getLimitAmount());
                } else {
                    preparedStatement.setNull(4, Types.INTEGER);
                }

                preparedStatement.setString(5, account.getCategory() != null ? account.getCategory().name() : "OTHER");
                preparedStatement.setString(6, account.getCycle() != null ? account.getCycle().name() : "MONTHLY");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        account.setId(resultSet.getInt("id"));
                        account.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
                    }
                }
            }
            logger.info("Account created: id={} name={} companyId={}", account.getId(), account.getAccountName(), account.getCompanyId());
            return account;
        } catch (SQLException e) {
            logger.error("Failed to create account name={} companyId={}", account.getAccountName(), account.getCompanyId(), e);
            throw e;
        }
    }

    public List<Account> getAllAccounts() throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(AccountService.class);
        String sql = "SELECT * FROM accounts ORDER BY id";
        List<Account> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load all accounts", e);
            throw e;
        }
        return list;
    }

    public Optional<Account> getAccountById(int id) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(AccountService.class);
        String sql = "SELECT * FROM accounts WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load account by id={}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<Account> getAccountsByCompanyId(int companyId) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(AccountService.class);
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
        } catch (SQLException e) {
            logger.error("Failed to load accounts for companyId={}", companyId, e);
            throw e;
        }
        return list;
    }

    public boolean updateAccount(Account account) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(AccountService.class);
        Account before = getAccountById(account.getId()).orElse(null);
        String sql = "UPDATE accounts SET company_id = ?, account_name = ?, " +
                "currency = ?, limit_amount = ?, category = ?, cycle = ? WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, account.getCompanyId());
            preparedStatement.setString(2, account.getAccountName());
            preparedStatement.setString(3, account.getCurrency());

            if (account.getLimitAmount() != null) {
                preparedStatement.setInt(4, account.getLimitAmount());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }
            preparedStatement.setString(5, account.getCategory() != null ? account.getCategory().name() : "OTHER");
            preparedStatement.setString(6, account.getCycle() != null ? account.getCycle().name() : "MONTHLY");
            preparedStatement.setInt(7, account.getId());
            boolean updated = preparedStatement.executeUpdate() > 0;
            if (updated && before != null) {
                StringBuilder changes = new StringBuilder();
                if (!java.util.Objects.equals(before.getAccountName(), account.getAccountName())) changes.append(String.format("account_name: %s->%s; ", before.getAccountName(), account.getAccountName()));
                if (before.getCurrentBalance() == null || account.getCurrentBalance() == null || before.getCurrentBalance().compareTo(account.getCurrentBalance()) != 0)
                    changes.append(String.format("current_balance: %s->%s; ", before.getCurrentBalance(), account.getCurrentBalance()));
                if (!java.util.Objects.equals(before.getCurrency(), account.getCurrency())) changes.append(String.format("currency: %s->%s; ", before.getCurrency(), account.getCurrency()));
                if (!java.util.Objects.equals(before.getLimitAmount(), account.getLimitAmount())) changes.append(String.format("limit_amount: %s->%s; ", before.getLimitAmount(), account.getLimitAmount()));
                if (!java.util.Objects.equals(before.getCategory(), account.getCategory())) changes.append(String.format("category: %s->%s; ", before.getCategory(), account.getCategory()));
                if (!java.util.Objects.equals(before.getCycle(), account.getCycle())) changes.append(String.format("cycle: %s->%s; ", before.getCycle(), account.getCycle()));
                logger.info("Account updated: id={} changes={}", account.getId(), changes.toString());
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Failed to update account id={}", account.getId(), e);
            throw e;
        }
    }

    public boolean deleteAccount(int id) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(AccountService.class);
        Account before = getAccountById(id).orElse(null);
        String sql = "DELETE FROM accounts WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            boolean deleted = preparedStatement.executeUpdate() > 0;
            if (deleted) {
                if (before != null) logger.info("Account deleted: id={} name={} companyId={}", before.getId(), before.getAccountName(), before.getCompanyId());
                else logger.info("Account deleted: id={}", id);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Failed to delete account id={}", id, e);
            throw e;
        }
    }

    private Account mapRow(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setId(resultSet.getInt("id"));
        account.setCompanyId(resultSet.getInt("company_id"));
        account.setAccountName(resultSet.getString("account_name"));
        account.setCurrency(resultSet.getString("currency"));
        account.setCreatedAt(DbTime.readOffsetDateTime(resultSet, "created_at"));
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

