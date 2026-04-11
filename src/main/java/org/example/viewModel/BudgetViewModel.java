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
        var logger = org.example.logging.AppLog.getLogger(BudgetViewModel.class);
        logger.info("Loading budgets for view");
        allBudgets.clear();

        allBudgets.addAll(
                new Budget("Marketing Budget", "BANK ACCOUNT • CORPORATE", 142000.00, 170000.00, 82),
                new Budget("Cash Reserve", "LIQUID ASSETS • INTERNAL", 58400.00, 389000.00, 15),
                new Budget("Operating Credit", "CREDIT LINE • EXTERNAL", 228550.00, 500000.00, 44)
        );
        logger.info("Loaded {} budgets into view", allBudgets.size());
    }

    public void addBudget(Budget newBudget) {
        var logger = org.example.logging.AppLog.getLogger(BudgetViewModel.class);
        logger.info("Add budget request: name={} category={} limit={} balance={}", newBudget.getName(), newBudget.getCategory(), newBudget.getLimit(), newBudget.getBalance());
        try {
            allBudgets.add(newBudget);
            message.set("Success: Budget '" + newBudget.getName() + "' created successfully!");
            logger.info("Budget created: name={} category={} limit={} balance={}", newBudget.getName(), newBudget.getCategory(), newBudget.getLimit(), newBudget.getBalance());
        } catch (Exception e) {
            message.set("Error: Failed to create budget.");
            logger.error("Failed to create budget name={}", newBudget.getName(), e);
        }
    }

    public void deleteBudget(Budget budget) {
        var logger = org.example.logging.AppLog.getLogger(BudgetViewModel.class);
        logger.info("Delete budget request: name={}", budget.getName());
        if (allBudgets.remove(budget)) {
            message.set("Success: Account successfully deleted.");
            logger.info("Budget deleted: name={}", budget.getName());
        } else {
            message.set("Error: Could not delete account.");
            logger.warn("Budget delete failed: name={}", budget.getName());
        }
    }

    public void updateBudget(Budget existing, Budget updated) {
        var logger = org.example.logging.AppLog.getLogger(BudgetViewModel.class);
        logger.info("Update budget request: name={}", existing.getName());
        int idx = -1;
        for (int i = 0; i < allBudgets.size(); i++) {
            if (allBudgets.get(i).getName().equals(existing.getName())) {
                idx = i;
                break;
            }
        }
        if (idx < 0) {
            message.set("Error: Budget not found.");
            logger.warn("Update budget failed: not found name={}", existing.getName());
            return;
        }
        StringBuilder changes = new StringBuilder();
        if (!java.util.Objects.equals(existing.getName(), updated.getName())) changes.append(String.format("name: %s->%s; ", existing.getName(), updated.getName()));
        if (!java.util.Objects.equals(existing.getCategory(), updated.getCategory())) changes.append(String.format("category: %s->%s; ", existing.getCategory(), updated.getCategory()));
        if (existing.getBalance() != updated.getBalance()) changes.append(String.format("balance: %s->%s; ", existing.getBalance(), updated.getBalance()));
        if (existing.getLimit() != updated.getLimit()) changes.append(String.format("limit: %s->%s; ", existing.getLimit(), updated.getLimit()));
        if (existing.getUtilization() != updated.getUtilization()) changes.append(String.format("utilization: %s->%s; ", existing.getUtilization(), updated.getUtilization()));
        allBudgets.set(idx, updated);
        message.set("Success: Budget updated successfully!");
        logger.info("Budget updated: name={} changes={}", existing.getName(), changes.toString());
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