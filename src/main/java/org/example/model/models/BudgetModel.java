package org.example.model.models;

import org.example.SessionManager;
import org.example.model.database.entity.Account;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.AccountCategory;
import org.example.model.database.entity.Transaction;
import org.example.model.database.service.AccountService;
import org.example.model.database.service.TransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BudgetModel {

    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    public Account addAccount(Account account) throws SQLException {
        accountService.addAccount(account);

        if (account.getCurrentBalance() != null && account.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
            Transaction startBalance = new Transaction(
                    account.getCompanyId(),
                    account.getId(),
                    null,
                    null,
                    "SALE",
                    account.getCurrentBalance(),
                    account.getAccountName() + " start balance",
                    LocalDate.now()
            );
            transactionService.addTransaction(startBalance);
        }

        return account;
    }

    public List<Account> getAccountsByCompanyId(int companyId) throws SQLException {
        List<Account> accounts = accountService.getAccountsByCompanyId(companyId);
        Map<Integer, BigDecimal> balances = transactionService.getBalancesByCompanyId(companyId);
        for (Account account : accounts) {
            account.setCurrentBalance(balances.getOrDefault(account.getId(), BigDecimal.ZERO));
        }
        return accounts;
    }

    public boolean updateAccount(Account account) throws SQLException {
        return accountService.updateAccount(account);
    }

    public boolean deleteAccount(int id) throws SQLException {
        return accountService.deleteAccount(id);
    }

    public BigDecimal getTotalBalance(int companyId) throws SQLException {
        return transactionService.getTotalBalanceByCompanyId(companyId);
    }

    public Optional<BigDecimal> getPrevMonthTotal(int companyId) throws SQLException {
        return transactionService.getPrevMonthTotalByCompanyId(companyId);
    }

    public BigDecimal getCurrentMonthTotal(int companyId) throws SQLException {
        return transactionService.getCurrentMonthTotalByCompanyId(companyId);
    }

    public boolean hasAccess() {
        return SessionManager.getInstance().getPosition() == Position.Director;
    public BigDecimal getTotalAssets(int companyId) throws SQLException {
        return getAccountsByCompanyId(companyId).stream()
                .filter(a -> a.getCategory() == AccountCategory.BANK_ACCOUNT
                        || a.getCategory() == AccountCategory.CASH
                        || a.getCategory() == AccountCategory.SAVINGS
                        || a.getCategory() == AccountCategory.INVESTMENT)
                .map(a -> a.getCurrentBalance() != null ? a.getCurrentBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLiabilities(int companyId) throws SQLException {
        return getAccountsByCompanyId(companyId).stream()
                .filter(a -> a.getCategory() == AccountCategory.CREDIT_LINE)
                .filter(a -> a.getCurrentBalance() != null && a.getCurrentBalance().compareTo(BigDecimal.ZERO) < 0)
                .map(a -> a.getCurrentBalance() != null ? a.getCurrentBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getNetPosition(int companyId) throws SQLException {
        return getTotalAssets(companyId).add(getTotalLiabilities(companyId));
    }

    public BigDecimal getCreditUtilizationByAccountId(int accountId) throws SQLException {
        return transactionService.getCreditUtilizationByAccountId(accountId);
    }

    public BigDecimal getRemainingCredit(Account account) throws SQLException {
        if (account == null || account.getLimitAmount() == null) {
            return null;
        }

        BigDecimal limit = BigDecimal.valueOf(account.getLimitAmount());

        BigDecimal balance = account.getCurrentBalance() != null ? account.getCurrentBalance() : BigDecimal.ZERO;
        return limit.add(balance);
    }
}
