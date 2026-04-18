package org.example.model.models;

import org.example.model.database.entity.Account;
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
}
