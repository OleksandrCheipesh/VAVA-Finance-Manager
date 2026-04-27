package org.example.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.SessionManager;
import org.example.model.database.entity.Account;
import org.example.model.models.BudgetModel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Predicate;

public class BudgetViewModel {

    private final BudgetModel budgetModel = new BudgetModel();
    private final int companyId = SessionManager.getInstance().getCurrentCompanyId();
    private final BooleanProperty hasAccess = new SimpleBooleanProperty(budgetModel.hasAccess());

    private final ObservableList<Account> allAccounts = FXCollections.observableArrayList();
    private final FilteredList<Account> filteredAccounts = new FilteredList<>(allAccounts, p -> true);
    private final StringProperty message = new SimpleStringProperty("");

    private BigDecimal totalBalance = BigDecimal.ZERO;
    private Optional<BigDecimal> prevMonthTotal = Optional.empty();
    private BigDecimal currentMonthTotal = BigDecimal.ZERO;

    public BudgetViewModel() {
        loadAccounts();
    }

    public void loadAccounts() {
        var logger = org.example.logging.AppLog.getLogger(BudgetViewModel.class);
        logger.info("Loading accounts for companyId={}", companyId);
        allAccounts.clear();
        try {
            allAccounts.addAll(budgetModel.getAccountsByCompanyId(companyId));
            totalBalance = budgetModel.getTotalBalance(companyId);
            prevMonthTotal = budgetModel.getPrevMonthTotal(companyId);
            currentMonthTotal = budgetModel.getCurrentMonthTotal(companyId);
            logger.info("Loaded {} accounts, totalBalance={}", allAccounts.size(), totalBalance);
        } catch (SQLException e) {
            message.set("Error: Failed to load accounts.");
            logger.error("Failed to load accounts for companyId={}", companyId, e);
        }
    }

    public void addAccount(Account account) {
        var logger = org.example.logging.AppLog.getLogger(BudgetViewModel.class);
        account.setCompanyId(companyId);
        try {
            budgetModel.addAccount(account);
            totalBalance = budgetModel.getTotalBalance(companyId);
            currentMonthTotal = budgetModel.getCurrentMonthTotal(companyId);
            allAccounts.add(account);
            message.set("Success: Account '" + account.getAccountName() + "' created successfully!");
            logger.info("Account created: id={} name={}", account.getId(), account.getAccountName());
        } catch (SQLException e) {
            message.set("Error: Failed to create account.");
            logger.error("Failed to create account name={}", account.getAccountName(), e);
        }
    }

    public void deleteAccount(Account account) {
        var logger = org.example.logging.AppLog.getLogger(BudgetViewModel.class);
        try {
            budgetModel.deleteAccount(account.getId());
            totalBalance = budgetModel.getTotalBalance(companyId);
            currentMonthTotal = budgetModel.getCurrentMonthTotal(companyId);
            allAccounts.remove(account);
            message.set("Success: Account successfully deleted.");
            logger.info("Account deleted: id={} name={}", account.getId(), account.getAccountName());
        } catch (SQLException e) {
            message.set("Error: Could not delete account.");
            logger.error("Failed to delete account id={}", account.getId(), e);
        }
    }

    public void filterAccounts(Predicate<Account> predicate) {
        filteredAccounts.setPredicate(predicate == null ? a -> true : predicate);
    }

    public FilteredList<Account> getFilteredAccounts() { return filteredAccounts; }
    public BigDecimal getTotalBalance() { return totalBalance; }
    public Optional<BigDecimal> getPrevMonthTotal() { return prevMonthTotal; }
    public BigDecimal getCurrentMonthTotal() { return currentMonthTotal; }
    public BooleanProperty hasAccessProperty() { return hasAccess; }
    public BigDecimal getTotalLimit() {
        return allAccounts.stream()
                .filter(a -> a.getLimitAmount() != null)
                .map(a -> BigDecimal.valueOf(a.getLimitAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public StringProperty messageProperty() { return message; }
}