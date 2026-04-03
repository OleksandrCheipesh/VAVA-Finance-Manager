package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.SessionManager;
import org.example.model.database.entity.Account;
import org.example.model.database.service.AccountService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class AccountsViewModel {

    private final AccountService accountService = new AccountService();
    private final ObservableList<Account> accounts = FXCollections.observableArrayList();
    private final FilteredList<Account> filteredAccounts = new FilteredList<>(accounts, p -> true);
    private final StringProperty message = new SimpleStringProperty("");
    private final StringProperty totalBalance = new SimpleStringProperty("0.00");

    public AccountsViewModel() {
        accounts.addListener((ListChangeListener<Account>) change -> recomputeStats());
        loadAccounts();
    }

    public void loadAccounts() {
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            List<Account> list = accountService.getAccountsByCompanyId(companyId);
            accounts.setAll(list);
        } catch (IllegalStateException e) {
            message.set("Error: No active session. Please log in.");
        } catch (Exception e) {
            message.set("Error: Failed to load accounts — " + e.getMessage());
        }
    }

    public void addAccount(Account a) {
        message.set("");
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            a.setCompanyId(companyId);
            Account saved = accountService.addAccount(a);
            accounts.add(0, saved);
            message.set("Success: Account '" + saved.getAccountName() + "' created successfully!");
        } catch (IllegalStateException e) {
            message.set("Error: No active session. Please log in.");
        } catch (Exception e) {
            message.set("Error: Failed to create account — " + e.getMessage());
        }
    }

    public void deleteAccount(int id) {
        message.set("");
        try {
            accountService.deleteAccount(id);
            accounts.removeIf(a -> a.getId() == id);
            message.set("Success: Account deleted.");
        } catch (Exception e) {
            message.set("Error: Failed to delete account — " + e.getMessage());
        }
    }

    public void filterBySearch(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            filteredAccounts.setPredicate(a -> true);
            return;
        }
        String lower = searchText.toLowerCase();
        filteredAccounts.setPredicate(a ->
                a.getAccountName() != null && a.getAccountName().toLowerCase().contains(lower));
    }

    private void recomputeStats() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        BigDecimal total = BigDecimal.ZERO;
        for (Account a : accounts) {
            if (a.getCurrentBalance() != null) {
                total = total.add(a.getCurrentBalance());
            }
        }
        totalBalance.set(df.format(total));
    }

    public FilteredList<Account> getFilteredAccounts() { return filteredAccounts; }
    public StringProperty messageProperty() { return message; }
    public StringProperty totalBalanceProperty() { return totalBalance; }
}
