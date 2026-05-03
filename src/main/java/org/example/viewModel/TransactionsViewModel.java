package org.example.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.SessionManager;
import org.example.model.database.entity.Account;
import org.example.model.database.entity.Client;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.Project;
import org.example.model.database.entity.Transaction;
import org.example.model.database.service.AccountService;
import org.example.model.database.service.ClientService;
import org.example.model.database.service.ProjectService;
import org.example.model.database.service.TransactionService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.example.view.templates.CurrencyFormatter;

public class TransactionsViewModel {

    private final TransactionService transactionService = new TransactionService();
    private final ProjectService projectService = new ProjectService();
    private final AccountService accountService = new AccountService();
    private final ClientService clientService = new ClientService();

    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final FilteredList<Transaction> filteredTransactions = new FilteredList<>(transactions, p -> true);
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final ObservableList<Account> accounts = FXCollections.observableArrayList();
    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final Map<Integer, String> projectNameMap = new HashMap<>();
    private final Map<Integer, String> clientNameMap = new HashMap<>();
    private final StringProperty message = new SimpleStringProperty("");

    // Summary card properties
    private final StringProperty totalIncome = new SimpleStringProperty(CurrencyFormatter.symbol() + "0.00");
    private final StringProperty totalExpenses = new SimpleStringProperty(CurrencyFormatter.symbol() + "0.00");
    private final StringProperty netBalance = new SimpleStringProperty(CurrencyFormatter.symbol() + "0.00");
    private final StringProperty largestAmount = new SimpleStringProperty(CurrencyFormatter.symbol() + "0.00");
    private final StringProperty incomeSubtext = new SimpleStringProperty("0 sales");
    private final StringProperty expensesSubtext= new SimpleStringProperty("0 purchases");

    // Active filters
    private String currentSearchText = "";
    private String currentTypeFilter = "ALL";
    private Integer currentProjectId = null;
    private LocalDate fromDate = null;
    private LocalDate toDate = null;

    public TransactionsViewModel() {
        transactions.addListener((ListChangeListener<Transaction>) change -> recomputeStats());
        loadProjects();
        loadAccounts();
        loadClients();
        loadTransactions();
    }

    public void loadClients() {
        var logger = org.example.logging.AppLog.getLogger(TransactionsViewModel.class);
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            List<Client> list = clientService.getClientsByCompanyId(companyId);
            clients.setAll(list);
            clientNameMap.clear();
            for (Client c : list) {
                clientNameMap.put(c.getId(), c.getName() + " " + c.getSurname());
            }
        } catch (Exception e) {
            logger.warn("Could not load clients: {}", e.getMessage());
        }
    }

    public void loadAccounts() {
        var logger = org.example.logging.AppLog.getLogger(TransactionsViewModel.class);
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            accounts.setAll(accountService.getAccountsByCompanyId(companyId));
        } catch (Exception e) {
            logger.warn("Could not load accounts: {}", e.getMessage());
        }
    }

    public void loadProjects() {
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            List<Project> list = projectService.getProjectsByCompanyId(companyId);
            projects.setAll(list);
            projectNameMap.clear();
            for (Project p : list) {
                projectNameMap.put(p.getId(), p.getName());
            }
        } catch (Exception e) {
            var logger = org.example.logging.AppLog.getLogger(TransactionsViewModel.class);
            logger.warn("Could not load projects for session/company: {}", e.getMessage());
        }
    }

    public void loadTransactions() {
        var logger = org.example.logging.AppLog.getLogger(TransactionsViewModel.class);
        logger.info("Loading transactions for current session");
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            List<Transaction> list = transactionService.getTransactionsByCompanyId(companyId);
            transactions.setAll(list); // setAll replaces content atomically — no need to clear() first
            logger.info("Loaded {} transactions for companyId={}", list.size(), companyId);
        } catch (IllegalStateException e) {
            message.set("Error: No active session. Please log in.");
            logger.warn("Failed to load transactions: no active session");
        } catch (Exception e) {
            message.set("Error: Failed to load transactions — " + e.getMessage());
            logger.error("Failed to load transactions", e);
        }
    }

    public void addTransaction(Transaction t) {
        var logger = org.example.logging.AppLog.getLogger(TransactionsViewModel.class);
        logger.info("User triggered addTransaction: type={} amount={} projectId={}", t.getType(), t.getAmount(), t.getProjectId());
        message.set(""); // Reset first so the same result triggers the listener every time
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            t.setCompanyId(companyId);
            Transaction saved = transactionService.addTransaction(t);
            transactions.add(0, saved);
            message.set("Success: Transaction recorded successfully!");
            logger.info("addTransaction succeeded: id={} type={} amount={} companyId={}", saved.getId(), saved.getType(), saved.getAmount(), saved.getCompanyId());
        } catch (IllegalArgumentException e) {
            message.set("Error: " + e.getMessage());
            logger.warn("addTransaction validation failed: {}", e.getMessage());
        } catch (IllegalStateException e) {
            message.set("Error: No active session. Please log in.");
            logger.warn("addTransaction failed: no active session");
        } catch (Exception e) {
            message.set("Error: Failed to record transaction — " + e.getMessage());
            logger.error("addTransaction failed", e);
        }
    }

    public void updateTransaction(Transaction t) {
        var logger = org.example.logging.AppLog.getLogger(TransactionsViewModel.class);
        logger.info("User triggered updateTransaction: id={} type={} amount={}", t.getId(), t.getType(), t.getAmount());
        message.set("");
        try {
            boolean updated = transactionService.updateTransaction(t);
            if (updated) {
                // Find by id and replace transaction in the list
                for (int i = 0; i < transactions.size(); i++) {
                    if (transactions.get(i).getId() == t.getId()) {
                        transactions.set(i, t);
                        break;
                    }
                }
                message.set("Success: Transaction updated successfully!");
                logger.info("updateTransaction succeeded: id={}", t.getId());
            } else {
                message.set("Error: Transaction not found.");
                logger.warn("updateTransaction not found: id={}", t.getId());
            }
        } catch (IllegalArgumentException e) {
            message.set("Error: " + e.getMessage());
            logger.warn("updateTransaction validation failed: {}", e.getMessage());
        } catch (Exception e) {
            message.set("Error: Failed to update transaction — " + e.getMessage());
            logger.error("updateTransaction failed: id={}", t.getId(), e);
        }
    }

    public void deleteTransaction(Transaction t) {
        var logger = org.example.logging.AppLog.getLogger(TransactionsViewModel.class);
        logger.info("User triggered deleteTransaction: id={}", t.getId());
        message.set("");
        try {
            boolean deleted = transactionService.deleteTransaction(t.getId());
            if (deleted) {
                transactions.removeIf(tx -> tx.getId() == t.getId());
                message.set("Success: Transaction deleted successfully!");
                logger.info("deleteTransaction succeeded: id={}", t.getId());
            } else {
                message.set("Error: Transaction not found.");
                logger.warn("deleteTransaction not found: id={}", t.getId());
            }
        } catch (Exception e) {
            message.set("Error: Failed to delete transaction — " + e.getMessage());
            logger.error("deleteTransaction failed: id={}", t.getId(), e);
        }
    }

    public void filterBySearch(String searchText) {
        this.currentSearchText = searchText == null ? "" : searchText;
        updateFilter();
    }

    public void filterByType(String type) {
        this.currentTypeFilter = type == null ? "ALL" : type;
        updateFilter();
    }

    public void filterByProject(Integer projectId) {
        this.currentProjectId = projectId;
        updateFilter();
    }

    public void filterByDateRange(LocalDate from, LocalDate to) {
        this.fromDate = from;
        this.toDate = to;
        updateFilter();
    }

    public void clearFilters() {
        currentSearchText = "";
        currentTypeFilter = "ALL";
        currentProjectId = null;
        fromDate = null;
        toDate = null;
        updateFilter();
    }

    private void updateFilter() {
        Pattern pattern = Pattern.compile(".*" + Pattern.quote(currentSearchText) + ".*", Pattern.CASE_INSENSITIVE);

        filteredTransactions.setPredicate(transaction -> {
            boolean matchesType = true;
            if ("SALE".equals(currentTypeFilter)) {
                matchesType = "SALE".equalsIgnoreCase(transaction.getType());
            } else if ("PURCHASE".equals(currentTypeFilter)) {
                matchesType = "PURCHASE".equalsIgnoreCase(transaction.getType());
            }

            boolean matchesSearch = true;
            if (!currentSearchText.isEmpty()) {
                matchesSearch = transaction.getDescription() != null
                        && pattern.matcher(transaction.getDescription()).matches();
            }

            boolean matchesProject = true;
            if (currentProjectId != null) {
                matchesProject = currentProjectId.equals(transaction.getProjectId());
            }

            boolean matchesDateFrom = true;
            if (fromDate != null && transaction.getDate() != null) {
                matchesDateFrom = !transaction.getDate().isBefore(fromDate);
            }

            boolean matchesDateTo = true;
            if (toDate != null && transaction.getDate() != null) {
                matchesDateTo = !transaction.getDate().isAfter(toDate);
            }

            return matchesType && matchesSearch && matchesProject && matchesDateFrom && matchesDateTo;
        });
    }

    public String getProjectName(Integer projectId) {
        if (projectId == null) return "";
        return projectNameMap.getOrDefault(projectId, "#" + projectId);
    }

    public String getClientName(Integer clientId) {
        if (clientId == null) return "";
        return clientNameMap.getOrDefault(clientId, "#" + clientId);
    }

    private void recomputeStats() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        BigDecimal largest= BigDecimal.ZERO;
        int salesCount = 0, purchaseCount = 0;

        for (Transaction t : transactions) {
            if (t.getAmount() == null) continue;
            if ("SALE".equalsIgnoreCase(t.getType())) {
                income = income.add(t.getAmount());
                salesCount++;
            } else if ("PURCHASE".equalsIgnoreCase(t.getType())) {
                expenses = expenses.add(t.getAmount());
                purchaseCount++;
            }
            if (t.getAmount().compareTo(largest) > 0) largest = t.getAmount();
        }

        BigDecimal net = income.subtract(expenses);
        totalIncome.set(df.format(income) + org.example.view.templates.CurrencyFormatter.symbol());
        totalExpenses.set(df.format(expenses) + org.example.view.templates.CurrencyFormatter.symbol());
        netBalance.set(df.format(net) + org.example.view.templates.CurrencyFormatter.symbol());
        largestAmount.set(df.format(largest) + org.example.view.templates.CurrencyFormatter.symbol());
        incomeSubtext.set(salesCount + " sales");
        expensesSubtext.set(purchaseCount + " purchases");
    }

    public ObservableList<Account> getAccounts() { return accounts; }
    public ObservableList<Project> getProjects() { return projects; }
    public ObservableList<Client> getClients() { return clients; }
    public FilteredList<Transaction> getFilteredTransactions() { return filteredTransactions; }
    public StringProperty messageProperty() { return message; }
    public StringProperty totalIncomeProperty() { return totalIncome; }
    public StringProperty totalExpensesProperty() { return totalExpenses; }
    public StringProperty netBalanceProperty() { return netBalance; }
    public StringProperty largestAmountProperty() { return largestAmount; }
    public StringProperty incomeSubtextProperty() { return incomeSubtext; }
    public StringProperty expensesSubtextProperty(){ return expensesSubtext; }
    private final BooleanProperty hasAccess = new SimpleBooleanProperty(
            SessionManager.getInstance().getPosition() == Position.Director ||
                    SessionManager.getInstance().getPosition() == Position.Accountant
    );
    public BooleanProperty hasAccessProperty() { return hasAccess; }
}
