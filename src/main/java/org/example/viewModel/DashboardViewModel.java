package org.example.viewModel;

import ch.qos.logback.classic.spi.ILoggingEvent;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.SessionManager;
import org.example.model.database.entity.Account;
import org.example.model.database.entity.Client;
import org.example.model.database.entity.Project;
import org.example.model.database.entity.Transaction;
import org.example.model.database.service.AccountService;
import org.example.model.database.service.ClientService;
import org.example.model.database.service.ProjectService;
import org.example.model.database.service.TransactionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel {

    // Static Properties for KPI cards
    private static final StringProperty totalRevenue = new SimpleStringProperty("$1.24M");
    private static final StringProperty revenueGrowth = new SimpleStringProperty("+12.5%");
    private static final IntegerProperty activeProjectsCount = new SimpleIntegerProperty(42);
    private static final TransactionService transactionService = new TransactionService();
    private static final AccountService clientService = new AccountService();
    private static final ProjectService projectService = new ProjectService();
    private static final ObservableList<Project> projects = FXCollections.observableArrayList();

    // Static List of recent transactions
    private static final ObservableList<TransactionDto> recentTransactions = FXCollections.observableArrayList();

    // Static block that fills in data
    static {
//        loadStubData();
        loadProjects();
        loadRealData();
        loadRecentTransactions();
        loadRevenueGrowth();
    }
    public static void reload(){
        loadProjects();
        loadRealData();
        loadRecentTransactions();
        loadRevenueGrowth();
    }
    private static void loadRealData() {
        int companyId = SessionManager.getInstance().getCurrentCompanyId();

        try {
            BigDecimal balance = transactionService.getTotalBalanceByCompanyId(companyId);
            totalRevenue.set(formatCurrency(balance));
                long count = projects.stream()
                        .filter(Project::isActive)
                        .count();

                activeProjectsCount.set((int) count);
        } catch (SQLException e) {
            // TODO: logging
        }
    }

    private static void loadRevenueGrowth() {
        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        try {
            List<Transaction> all = transactionService.getTransactionsByCompanyId(companyId);

            LocalDate now = LocalDate.now();

            // текущий месяц
            BigDecimal current = all.stream()
                    .filter(tx -> tx.getAmount().compareTo(BigDecimal.ZERO) > 0) // только доход
                    .filter(tx -> tx.getDate().getMonth() == now.getMonth()
                            && tx.getDate().getYear() == now.getYear())
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // прошлый месяц
            LocalDate prevMonth = now.minusMonths(1);

            BigDecimal previous = all.stream()
                    .filter(tx -> tx.getAmount().compareTo(BigDecimal.ZERO) > 0)
                    .filter(tx -> tx.getDate().getMonth() == prevMonth.getMonth()
                            && tx.getDate().getYear() == prevMonth.getYear())
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // защита от деления на 0
            if (previous.compareTo(BigDecimal.ZERO) == 0) {
                revenueGrowth.set("0%");
                return;
            }

            BigDecimal growth = current.subtract(previous)
                    .divide(previous, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            revenueGrowth.set(formatPercent(growth));

        } catch (SQLException e) {
            revenueGrowth.set("0%");
        }
    }
    private static String formatPercent(BigDecimal value) {
        return (value.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") +
                value.setScale(1, RoundingMode.HALF_UP) + "%";
    }
    private static void loadRecentTransactions() {
        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        try {
            List<Transaction> all = transactionService.getTransactionsByCompanyId(companyId);
            List<TransactionDto> fin = new ArrayList<>();
            String clientName;
            List<Transaction> sorted = all.stream()
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .limit(5)
                    .toList();
            for (int i = 0; i < sorted.size(); i++) {
                clientName = clientService.getAccountById(sorted.get(i).getAccountId() )
                            .map(Account::getAccountName)
                            .orElse("Unknown");
                fin.add(new TransactionDto(sorted.get(i).getDate(), sorted.get(i).getDescription(), clientName, sorted.get(i).getAmount().doubleValue()));
            }
            recentTransactions.clear();
            recentTransactions.addAll(fin);
        } catch (SQLException e) {
            // TODO: logging
        }
    }
    private static void loadProjects() {
        int companyId = SessionManager.getInstance().getCurrentCompanyId();

        try {
            List<Project> all = projectService.getProjectsByCompanyId(companyId);
            projects.setAll(all);

        } catch (SQLException e) {
            // TODO logging
        }
    }

    private static String formatCurrency(BigDecimal value) {
        if (value == null) return "$0.00";

        BigDecimal abs = value.abs();

        if (abs.compareTo(BigDecimal.valueOf(1_000_000)) >= 0) {
            return "$" + value.divide(BigDecimal.valueOf(1_000_000))
                    .setScale(2, RoundingMode.HALF_UP) + "M";
        }

        if (abs.compareTo(BigDecimal.valueOf(1_000)) >= 0) {
            return "$" + value.divide(BigDecimal.valueOf(1_000))
                    .setScale(2, RoundingMode.HALF_UP) + "K";
        }

        return "$" + value.setScale(2, RoundingMode.HALF_UP);
    }
    public static ObservableList<Project> getProjects() {
        return projects;
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