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

    public void filterBySearch(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredTransactions.setPredicate(transaction -> true);
            return;
        }

        Pattern pattern = Pattern.compile(".*" + searchText + ".*", Pattern.CASE_INSENSITIVE);

        filteredTransactions.setPredicate(transaction ->
                transaction.getDescription() != null && pattern.matcher(transaction.getDescription()).matches()
        );
    }

    public FilteredList<Transaction> getFilteredTransactions() { return filteredTransactions; }
    public StringProperty messageProperty() { return message; }
}