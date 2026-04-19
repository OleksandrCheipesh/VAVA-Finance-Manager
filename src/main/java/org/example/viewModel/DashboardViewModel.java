package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class DashboardViewModel {

    // Static Properties for KPI cards
    private static final StringProperty totalRevenue = new SimpleStringProperty("$1.24M");
    private static final StringProperty revenueGrowth = new SimpleStringProperty("+12.5%");
    private static final IntegerProperty activeProjectsCount = new SimpleIntegerProperty(42);

    // Static List of recent transactions
    private static final ObservableList<TransactionDto> recentTransactions = FXCollections.observableArrayList();

    // Static block that fills in data
    static {
        loadStubData();
    }

    private static void loadStubData() {
        recentTransactions.addAll(
                new TransactionDto(LocalDate.now(), "Server Infrastructure", "Cloudflare Inc.", -1250.00),
                new TransactionDto(LocalDate.now().minusDays(1), "Q4 Strategy Consulting", "Acme Corp", 8500.00),
                new TransactionDto(LocalDate.now().minusDays(3), "Software Licenses", "Atlassian", -450.00),
                new TransactionDto(LocalDate.now().minusDays(5), "Website Redesign", "Stark Industries", 12000.00),
                new TransactionDto(LocalDate.now().minusDays(7), "Office Supplies", "Staples", -120.50)
        );
    }

    // Static Getters
    public static StringProperty totalRevenueProperty() { return totalRevenue; }
    public static StringProperty revenueGrowthProperty() { return revenueGrowth; }
    public static IntegerProperty activeProjectsCountProperty() { return activeProjectsCount; }
    public static ObservableList<TransactionDto> getRecentTransactions() { return recentTransactions; }

    // DTO Class for Transactions
    public static class TransactionDto {
        private final LocalDate date;
        private final String description;
        private final String clientName;
        private final double amount;

        public TransactionDto(LocalDate date, String description, String clientName, double amount) {
            this.date = date;
            this.description = description;
            this.clientName = clientName;
            this.amount = amount;
        }

        public LocalDate getDate() { return date; }
        public String getDescription() { return description; }
        public String getClientName() { return clientName; }
        public double getAmount() { return amount; }
    }
}