package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.database.entity.Project;
import org.example.model.database.service.ProjectService;
import org.example.model.reports.*;
import org.example.model.models.ReportsModel;
import javafx.concurrent.Task;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.example.SessionManager;
import org.example.model.database.entity.Position;

public class ReportsViewModel {

    // ----------------------------------------------------------------
    // Filter state — bound to ReportFilterBar in the view
    // ----------------------------------------------------------------
    private final ObjectProperty<Project>    selectedProject = new SimpleObjectProperty<>(null);
    private final ObjectProperty<PeriodType> selectedPeriod  = new SimpleObjectProperty<>(PeriodType.THIS_YEAR);
    private final ObjectProperty<LocalDate>  customFrom      = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate>  customTo        = new SimpleObjectProperty<>();

    // "Month" or "Quarter" — used by bar/line chart modals
    private final StringProperty grouping       = new SimpleStringProperty("Month");
    private final StringProperty selectedStatus = new SimpleStringProperty(null);
    private       String         selectedProjectName = null;

    private final StringProperty totalProfit = new SimpleStringProperty("$0.00");
    private final StringProperty grossIncome = new SimpleStringProperty("$0.00");
    private final StringProperty netExpense  = new SimpleStringProperty("$0.00");
    private final StringProperty netProfitTrend = new SimpleStringProperty("$0.00");

    private final ReportsModel model = new ReportsModel();
    private final ProjectService projectService = new ProjectService();
    private final javafx.collections.ObservableList<Project> availableProjects = javafx.collections.FXCollections.observableArrayList();

    // ----------------------------------------------------------------
    // Chart data — ObservableLists that charts listen to
    // ----------------------------------------------------------------
    private final ObservableList<ProjectSummaryDTO>  projectSummaries  = FXCollections.observableArrayList();
    private final ObservableList<MonthlySnapshotDTO> monthlySummaries  = FXCollections.observableArrayList();
    private final ObservableList<ExpenseCategoryDTO> expenseCategories = FXCollections.observableArrayList();
    private final ObservableList<IncomeBreakdownDTO> incomeBreakdown   = FXCollections.observableArrayList();

    // ----------------------------------------------------------------
    // UI state
    // ----------------------------------------------------------------
    private final BooleanProperty loading      = new SimpleBooleanProperty(false);
    private final StringProperty  errorMessage = new SimpleStringProperty("");

    public ReportsViewModel() {
        loadAvailableProjects();
        recompute();

        selectedPeriod.addListener((obs, oldV, newV) -> recompute());
        selectedProject.addListener((obs, oldV, newV) -> recompute());
        customFrom.addListener((obs, oldV, newV) -> recompute());
        customTo.addListener((obs, oldV, newV) -> recompute());
        selectedStatus.addListener((obs, oldV, newV) -> recompute());
    }

    private void loadAvailableProjects() {
        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        javafx.concurrent.Task<java.util.List<Project>> task = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<Project> call() throws Exception {
                return projectService.getProjectsByCompanyId(companyId);
            }
        };
        task.setOnSucceeded(e -> javafx.application.Platform.runLater(() -> {
            availableProjects.setAll(task.getValue());
        }));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    // ----------------------------------------------------------------
    // recompute() — real data loading
    // ----------------------------------------------------------------
    public void recompute() {
        LocalDate from = resolvedFrom();
        LocalDate to   = resolvedTo();
        if (from == null || to == null) return;

        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        Integer pId = selectedProject.get() != null ? selectedProject.get().getId() : null;
        String status = selectedStatus.get();
        if (status != null && status.isBlank()) status = null;

        loading.set(true);
        errorMessage.set("");

        String finalStatus = status;
        Task<List<?>[]> task = new Task<>() {
            @Override
            protected List<?>[] call() throws Exception {
                List<ProjectSummaryDTO> pSums = model.getProjectSummaries(companyId, from, to, pId, finalStatus);
                List<MonthlySnapshotDTO> mSums = model.getMonthlySummaries(companyId, from, to, pId, finalStatus);
                List<ExpenseCategoryDTO> eCats = model.getExpenseBreakdown(companyId, from, to, pId);
                List<IncomeBreakdownDTO> iBrk  = model.getIncomeBreakdown(companyId, from, to, pId, finalStatus);

                if ("Quarter".equals(grouping.get())) {
                    mSums = groupByQuarter(mSums);
                }

                return new List<?>[]{pSums, mSums, eCats, iBrk};
            }
        };

        task.setOnSucceeded(e -> {
            List<?>[] result = task.getValue();

            List<ProjectSummaryDTO> pSumsList = (List<ProjectSummaryDTO>) result[0];
            List<MonthlySnapshotDTO> mSumsList = (List<MonthlySnapshotDTO>) result[1];

            BigDecimal tProfit = pSumsList.stream().map(ProjectSummaryDTO::getNetProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal gInc = mSumsList.stream().map(MonthlySnapshotDTO::getIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal nExp = mSumsList.stream().map(MonthlySnapshotDTO::getExpense).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal nTrend = gInc.subtract(nExp);

            java.text.NumberFormat fmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US);

            javafx.application.Platform.runLater(() -> {
                projectSummaries.setAll(pSumsList);
                monthlySummaries.setAll(mSumsList);
                expenseCategories.setAll((List<ExpenseCategoryDTO>) result[2]);
                incomeBreakdown.setAll((List<IncomeBreakdownDTO>) result[3]);

                totalProfit.set(fmt.format(tProfit));
                grossIncome.set(fmt.format(gInc));
                netExpense.set(fmt.format(nExp));
                netProfitTrend.set(fmt.format(nTrend));

                loading.set(false);
            });
        });

        task.setOnFailed(e -> {
            loading.set(false);
            errorMessage.set("Failed to load reports: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    /**
     * Aggregates a list of monthly snapshots into quarters.
     * Q1=Jan-Mar, Q2=Apr-Jun, Q3=Jul-Sep, Q4=Oct-Dec.
     * Each resulting entry uses the YearMonth of the first month in the quarter
     * as its period label.
     */
    private List<MonthlySnapshotDTO> groupByQuarter(List<MonthlySnapshotDTO> monthly) {
        // Group by (year, quarter-number)
        java.util.Map<String, List<MonthlySnapshotDTO>> buckets = new java.util.LinkedHashMap<>();
        for (MonthlySnapshotDTO s : monthly) {
            int q    = (s.getPeriod().getMonthValue() - 1) / 3 + 1;
            String key = s.getPeriod().getYear() + "-Q" + q;
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        List<MonthlySnapshotDTO> quarters = new ArrayList<>();
        for (var entry : buckets.entrySet()) {
            List<MonthlySnapshotDTO> bucket = entry.getValue();
            BigDecimal income  = bucket.stream().map(MonthlySnapshotDTO::getIncome) .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expense = bucket.stream().map(MonthlySnapshotDTO::getExpense).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal net     = income.subtract(expense);
            YearMonth  period  = bucket.get(0).getPeriod();
            quarters.add(new MonthlySnapshotDTO(period, income, expense, net));
        }
        return quarters;
    }


    // ----------------------------------------------------------------
    // Resolved date helpers
    // ----------------------------------------------------------------
    public LocalDate resolvedFrom() {
        if (selectedPeriod.get() == null) return null;
        return switch (selectedPeriod.get()) {
            case THIS_MONTH   -> LocalDate.now().withDayOfMonth(1);
            case LAST_MONTH   -> LocalDate.now().minusMonths(1).withDayOfMonth(1);
            case THIS_QUARTER -> {
                int startMonth = ((LocalDate.now().getMonthValue() - 1) / 3) * 3 + 1;
                yield LocalDate.now().withMonth(startMonth).withDayOfMonth(1);
            }
            case THIS_YEAR    -> LocalDate.now().withDayOfYear(1);
            case CUSTOM       -> customFrom.get();
        };
    }

    public LocalDate resolvedTo() {
        if (selectedPeriod.get() == null) return null;
        return switch (selectedPeriod.get()) {
            case THIS_MONTH   -> LocalDate.now();
            case LAST_MONTH   -> {
                LocalDate lm = LocalDate.now().minusMonths(1);
                yield lm.withDayOfMonth(lm.lengthOfMonth());
            }
            case THIS_QUARTER -> LocalDate.now();
            case THIS_YEAR    -> LocalDate.now();
            case CUSTOM       -> customTo.get();
        };
    }

    // ----------------------------------------------------------------
    // Period enum
    // ----------------------------------------------------------------
    public enum PeriodType {
        THIS_MONTH, LAST_MONTH, THIS_QUARTER, THIS_YEAR, CUSTOM
    }

    // ----------------------------------------------------------------
    // Getters / setters
    // ----------------------------------------------------------------
    public ObjectProperty<Project>            selectedProjectProperty()  { return selectedProject; }
    public ObjectProperty<PeriodType>         selectedPeriodProperty()   { return selectedPeriod; }
    public ObjectProperty<LocalDate>          customFromProperty()       { return customFrom; }
    public ObjectProperty<LocalDate>          customToProperty()         { return customTo; }
    public StringProperty                     groupingProperty()         { return grouping; }
    public ObservableList<ProjectSummaryDTO>  getProjectSummaries()      { return projectSummaries; }
    public ObservableList<MonthlySnapshotDTO> getMonthlySummaries()      { return monthlySummaries; }
    public ObservableList<ExpenseCategoryDTO> getExpenseCategories()     { return expenseCategories; }
    public ObservableList<IncomeBreakdownDTO> getIncomeBreakdown()       { return incomeBreakdown; }
    public BooleanProperty                    loadingProperty()          { return loading; }
    public StringProperty                     errorMessageProperty()     { return errorMessage; }
    public StringProperty                     selectedStatusProperty()   { return selectedStatus; }

    public StringProperty totalProfitProperty() { return totalProfit; }
    public StringProperty grossIncomeProperty() { return grossIncome; }
    public StringProperty netExpenseProperty() { return netExpense; }
    public StringProperty netProfitTrendProperty() { return netProfitTrend; }

    public void setSelectedProject(String name) {
        selectedProjectName = name;
        if (name == null || name.isBlank()) {
            selectedProject.set(null);
            return;
        }
        if ("All Projects".equals(name)) {
            selectedProject.set(null);
            return;
        }
        Project match = availableProjects.stream().filter(p -> name.equals(p.getName())).findFirst().orElse(null);
        selectedProject.set(match);
    }

    public javafx.collections.ObservableList<Project> getAvailableProjects() { return availableProjects; }

    public void setSelectedStatus(String status) {
        selectedStatus.set(status);
    }

    /** Maps UI string → PeriodType enum. */
    public void setPeriodFromString(String label) {
        if (label == null) return;
        selectedPeriod.set(switch (label) {
            case "This Month"   -> PeriodType.THIS_MONTH;
            case "Last Month"   -> PeriodType.LAST_MONTH;
            case "This Quarter" -> PeriodType.THIS_QUARTER;
            case "This Year"    -> PeriodType.THIS_YEAR;
            case "Custom"       -> PeriodType.CUSTOM;
            default             -> PeriodType.THIS_YEAR;
        });
    }

    /** Returns current period as UI display string. */
    public String getPeriodAsString() {
        if (selectedPeriod.get() == null) return "This Year";
        return switch (selectedPeriod.get()) {
            case THIS_MONTH   -> "This Month";
            case LAST_MONTH   -> "Last Month";
            case THIS_QUARTER -> "This Quarter";
            case THIS_YEAR    -> "This Year";
            case CUSTOM       -> "Custom";
        };
    }

    /** Sets Month/Quarter grouping and immediately recomputes. */
    public void setGrouping(String value) {
        grouping.set(value);
        recompute();
    }

    private final BooleanProperty hasAccess = new SimpleBooleanProperty(
            SessionManager.getInstance().getPosition() == Position.Director ||
                    SessionManager.getInstance().getPosition() == Position.Analyst
    );
    public BooleanProperty hasAccessProperty() { return hasAccess; }
}