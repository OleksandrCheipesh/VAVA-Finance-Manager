package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.model.database.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class TransactionsViewModel {

    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final FilteredList<Transaction> filteredTransactions = new FilteredList<>(transactions, p -> true);
    private final StringProperty message = new SimpleStringProperty("");

    // Active filters
    private String currentSearchText = "";
    private String currentTypeFilter = "ALL";

    public TransactionsViewModel() {
        loadTransactions();
    }

    public void loadTransactions() {
        transactions.clear();

        transactions.addAll(
                new Transaction(1, 1, 1, 1, "SALE", new BigDecimal("2400.00"), "Website redesign", LocalDate.now().minusDays(2)),
                new Transaction(1, 1, 1, 2, "PURCHASE", new BigDecimal("24.50"), "Creative Cloud Sub", LocalDate.now().minusDays(1))
        );
    }

    public void addTransaction(Transaction t) {
        try {
            transactions.add(0, t);
            message.set("Success: Transaction recorded successfully!");
        } catch (Exception e) {
            message.set("Error: Failed to record transaction.");
        }
    }

    // Update search filter
    public void filterBySearch(String searchText) {
        this.currentSearchText = searchText == null ? "" : searchText;
        updateFilter();
    }

    // Update tab filter
    public void filterByType(String type) {
        this.currentTypeFilter = type;
        updateFilter();
    }

    // Apply all filters together
    private void updateFilter() {
        Pattern pattern = Pattern.compile(".*" + currentSearchText + ".*", Pattern.CASE_INSENSITIVE);

        filteredTransactions.setPredicate(transaction -> {
            boolean matchesType = true;
            if ("SALE".equals(currentTypeFilter)) {
                matchesType = "SALE".equalsIgnoreCase(transaction.getType());
            } else if ("PURCHASE".equals(currentTypeFilter)) {
                matchesType = "PURCHASE".equalsIgnoreCase(transaction.getType());
            }

            boolean matchesSearch = true;
            if (!currentSearchText.isEmpty()) {
                matchesSearch = transaction.getDescription() != null && pattern.matcher(transaction.getDescription()).matches();
            }

            return matchesType && matchesSearch;
        });
    }

    public FilteredList<Transaction> getFilteredTransactions() { return filteredTransactions; }
    public StringProperty messageProperty() { return message; }
}