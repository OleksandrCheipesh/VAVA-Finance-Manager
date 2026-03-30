package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.function.Predicate;

public class BudgetViewModel {

    // Temporary Entity Class
    public static class Budget {
        private String name;
        private String category;
        private double balance;
        private double limit;
        private double utilization; // Percentage

        public Budget(String name, String category, double balance, double limit, double utilization) {
            this.name = name;
            this.category = category;
            this.balance = balance;
            this.limit = limit;
            this.utilization = utilization;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getBalance() { return balance; }
        public double getLimit() { return limit; }
        public double getUtilization() { return utilization; }
    }

    private final ObservableList<Budget> allBudgets = FXCollections.observableArrayList();
    private final FilteredList<Budget> filteredBudgets = new FilteredList<>(allBudgets, p -> true);
    private final StringProperty message = new SimpleStringProperty("");

    public BudgetViewModel() {
        loadBudgets();
    }

    public void loadBudgets() {
        allBudgets.clear();

        allBudgets.addAll(
                new Budget("Marketing Budget", "BANK ACCOUNT • CORPORATE", 142000.00, 170000.00, 82),
                new Budget("Cash Reserve", "LIQUID ASSETS • INTERNAL", 58400.00, 389000.00, 15),
                new Budget("Operating Credit", "CREDIT LINE • EXTERNAL", 228550.00, 500000.00, 44)
        );
    }

    public void addBudget(Budget newBudget) {
        try {
            allBudgets.add(newBudget);

            message.set("Success: Budget '" + newBudget.getName() + "' created successfully!");
        } catch (Exception e) {
            message.set("Error: Failed to create budget.");
        }
    }

    public void deleteBudget(Budget budget) {
        if (allBudgets.remove(budget)) {
            message.set("Success: Account successfully deleted.");
        } else {
            message.set("Error: Could not delete account.");
        }
    }

    // Regex Search
    public void filterBudgets(Predicate<Budget> predicate) {
        if (predicate == null) {
            filteredBudgets.setPredicate(budget -> true);
        } else {
            filteredBudgets.setPredicate(predicate);
        }
    }

    public FilteredList<Budget> getFilteredBudgets() {
        return filteredBudgets;
    }

    public StringProperty messageProperty() {
        return message;
    }
}