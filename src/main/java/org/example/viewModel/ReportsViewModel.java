package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import org.example.SessionManager;
import org.example.model.database.entity.Employee;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.Project;
import org.example.model.database.service.ProjectService;
import org.example.model.models.ReportsModel;
import org.example.model.reports.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportsViewModel {

    // ----------------------------------------------------------------
    // Filter state
    // ----------------------------------------------------------------
    private final ObjectProperty<Project>    selectedProject = new SimpleObjectProperty<>(null);
    private final ObjectProperty<PeriodType> selectedPeriod  = new SimpleObjectProperty<>(PeriodType.THIS_YEAR);
    private final ObjectProperty<LocalDate>  customFrom      = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate>  customTo        = new SimpleObjectProperty<>();

    private final StringProperty grouping       = new SimpleStringProperty("Month");
    private final StringProperty selectedStatus = new SimpleStringProperty(null); // project status (Active/Inactive)
    private       String         selectedProjectName = null;

    // Per-panel filters
    private final ObjectProperty<BigDecimal> incomeMinAmount    = new SimpleObjectProperty<>(null);
    private final ObjectProperty<BigDecimal> expenseMinAmount   = new SimpleObjectProperty<>(null);
    private final StringProperty             employeeStatus     = new SimpleStringProperty(null);
    private final BooleanProperty            showOnlyProfitable = new SimpleBooleanProperty(false);
    private final BooleanProperty            showOnlyLoss       = new SimpleBooleanProperty(false);

    // ----------------------------------------------------------------
    // Computed display strings (bound to summary cards)
    // ----------------------------------------------------------------
    private final StringProperty totalProfit    = new SimpleStringProperty("$0.00");
    private final StringProperty grossIncome    = new SimpleStringProperty("$0.00");
    private final StringProperty netExpense     = new SimpleStringProperty("$0.00");
    private final StringProperty netProfitTrend = new SimpleStringProperty("$0.00");

    private final ReportsModel   model          = new ReportsModel();
    private final ProjectService projectService = new ProjectService();

    // ----------------------------------------------------------------
    // Observable data
    // ----------------------------------------------------------------
    private final ObservableList<Project>            availableProjects = FXCollections.observableArrayList();
    private final ObservableList<ProjectSummaryDTO>  projectSummaries  = FXCollections.observableArrayList();
    private final ObservableList<MonthlySnapshotDTO> monthlySummaries  = FXCollections.observableArrayList();
    private final ObservableList<ExpenseCategoryDTO> expenseCategories = FXCollections.observableArrayList();
    private final ObservableList<IncomeBreakdownDTO> incomeBreakdown   = FXCollections.observableArrayList();
    private final ObservableList<Employee>           employees         = FXCollections.observableArrayList();

    private final FilteredList<Employee> filteredEmployees = new FilteredList<>(employees);

    // ----------------------------------------------------------------
    // UI state
    // ----------------------------------------------------------------
    private final BooleanProperty loading      = new SimpleBooleanProperty(false);
    private final StringProperty  errorMessage = new SimpleStringProperty("");

    public ReportsViewModel() {
        loadAvailableProjects();
        recompute();

        selectedPeriod.addListener((obs, o, n)  -> recompute());
        selectedProject.addListener((obs, o, n) -> recompute());
        customFrom.addListener((obs, o, n)      -> recompute());
        customTo.addListener((obs, o, n)        -> recompute());
        selectedStatus.addListener((obs, o, n)      -> recompute());
        showOnlyProfitable.addListener((obs, o, n) -> recompute());
        showOnlyLoss.addListener((obs, o, n)       -> recompute());
        incomeMinAmount.addListener((obs, o, n)    -> recompute());
        expenseMinAmount.addListener((obs, o, n)   -> recompute());

        employeeStatus.addListener((obs, o, n)     -> updateEmployeeFilter());
    }

    private void updateEmployeeFilter() {
        String s = employeeStatus.get();
        filteredEmployees.setPredicate(
            (s == null || s.isBlank()) ? null : e -> s.equalsIgnoreCase(e.getStatus())
        );
    }

    private void loadAvailableProjects() {
        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        Task<List<Project>> task = new Task<>() {
            @Override protected List<Project> call() throws Exception {
                return projectService.getProjectsByCompanyId(companyId);
            }
        };
        task.setOnSucceeded(e -> javafx.application.Platform.runLater(() ->
                availableProjects.setAll(task.getValue())));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    // ----------------------------------------------------------------
    // recompute() — async data load
    // ----------------------------------------------------------------
    public void recompute() {
        LocalDate from = resolvedFrom();
        LocalDate to   = resolvedTo();
        if (from == null || to == null) return;

        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        Integer pId   = selectedProject.get() != null ? selectedProject.get().getId() : null;
        String  status = selectedStatus.get();
        if (status != null && status.isBlank()) status = null;
        BigDecimal minInc = incomeMinAmount.get();
        BigDecimal minExp = expenseMinAmount.get();

        loading.set(true);
        errorMessage.set("");

        String finalStatus = status;
        Task<List<?>[]> task = new Task<>() {
            @Override
            protected List<?>[] call() throws Exception {
                List<ProjectSummaryDTO>  ps   = model.getProjectSummaries(companyId, from, to, pId);
                List<MonthlySnapshotDTO> ms   = model.getMonthlySnapshots(companyId, from, to, pId);
                List<ExpenseCategoryDTO> ec   = model.getExpenseBreakdown(companyId, from, to, pId, minExp);
                List<IncomeBreakdownDTO> ib   = model.getIncomeBreakdown(companyId, from, to, pId, minInc);
                List<Employee>           emps = model.getEmployees(companyId, pId);

                if ("Quarter".equals(grouping.get())) ms = groupByQuarter(ms);

                return new List<?>[]{ps, ms, ec, ib, emps};
            }
        };

        task.setOnSucceeded(e -> {
            List<?>[] r = task.getValue();
            List<ProjectSummaryDTO>  psList  = (List<ProjectSummaryDTO>)  r[0];
            List<MonthlySnapshotDTO> msList  = (List<MonthlySnapshotDTO>) r[1];

            BigDecimal tProfit = psList.stream().map(ProjectSummaryDTO::getNetProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal gInc    = msList.stream().map(MonthlySnapshotDTO::getIncome)  .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal nExp    = msList.stream().map(MonthlySnapshotDTO::getExpense) .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal nTrend  = gInc.subtract(nExp);
            java.text.NumberFormat fmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US);

            javafx.application.Platform.runLater(() -> {
                projectSummaries.setAll(psList);
                monthlySummaries.setAll(applyMonthlyFilters(msList));
                expenseCategories.setAll((List<ExpenseCategoryDTO>) r[2]);
                incomeBreakdown.setAll((List<IncomeBreakdownDTO>)   r[3]);
                employees.setAll((List<Employee>) r[4]);
                updateEmployeeFilter();

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

    private List<MonthlySnapshotDTO> applyMonthlyFilters(List<MonthlySnapshotDTO> raw) {
        return raw.stream()
            .filter(m -> !showOnlyProfitable.get() || m.getNetProfit().compareTo(BigDecimal.ZERO) > 0)
            .filter(m -> !showOnlyLoss.get()       || m.getNetProfit().compareTo(BigDecimal.ZERO) < 0)
            .collect(Collectors.toList());
    }

    private List<MonthlySnapshotDTO> groupByQuarter(List<MonthlySnapshotDTO> monthly) {
        java.util.Map<String, List<MonthlySnapshotDTO>> buckets = new java.util.LinkedHashMap<>();
        for (MonthlySnapshotDTO s : monthly) {
            int q = (s.getPeriod().getMonthValue() - 1) / 3 + 1;
            buckets.computeIfAbsent(s.getPeriod().getYear() + "-Q" + q, k -> new ArrayList<>()).add(s);
        }
        List<MonthlySnapshotDTO> quarters = new ArrayList<>();
        for (var entry : buckets.entrySet()) {
            List<MonthlySnapshotDTO> b = entry.getValue();
            BigDecimal income  = b.stream().map(MonthlySnapshotDTO::getIncome) .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expense = b.stream().map(MonthlySnapshotDTO::getExpense).reduce(BigDecimal.ZERO, BigDecimal::add);
            quarters.add(new MonthlySnapshotDTO(b.get(0).getPeriod(), income, expense, income.subtract(expense)));
        }
        return quarters;
    }

    // ----------------------------------------------------------------
    // Date helpers
    // ----------------------------------------------------------------
    public LocalDate resolvedFrom() {
        if (selectedPeriod.get() == null) return null;
        return switch (selectedPeriod.get()) {
            case THIS_MONTH   -> LocalDate.now().withDayOfMonth(1);
            case LAST_MONTH   -> LocalDate.now().minusMonths(1).withDayOfMonth(1);
            case THIS_QUARTER -> {
                int sm = ((LocalDate.now().getMonthValue() - 1) / 3) * 3 + 1;
                yield LocalDate.now().withMonth(sm).withDayOfMonth(1);
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
            case THIS_QUARTER, THIS_YEAR -> LocalDate.now();
            case CUSTOM -> customTo.get();
        };
    }

    // ----------------------------------------------------------------
    // Period enum
    // ----------------------------------------------------------------
    public enum PeriodType { THIS_MONTH, LAST_MONTH, THIS_QUARTER, THIS_YEAR, CUSTOM }

    // ----------------------------------------------------------------
    // Getters / setters
    // ----------------------------------------------------------------
    public ObjectProperty<Project>            selectedProjectProperty()    { return selectedProject; }
    public ObjectProperty<PeriodType>         selectedPeriodProperty()     { return selectedPeriod; }
    public ObjectProperty<LocalDate>          customFromProperty()         { return customFrom; }
    public ObjectProperty<LocalDate>          customToProperty()           { return customTo; }
    public StringProperty                     groupingProperty()           { return grouping; }
    public StringProperty                     selectedStatusProperty()     { return selectedStatus; }

    public ObjectProperty<BigDecimal>         incomeMinAmountProperty()    { return incomeMinAmount; }
    public ObjectProperty<BigDecimal>         expenseMinAmountProperty()   { return expenseMinAmount; }
    public StringProperty                     employeeStatusProperty()     { return employeeStatus; }
    public BooleanProperty                    showOnlyProfitableProperty() { return showOnlyProfitable; }
    public BooleanProperty                    showOnlyLossProperty()       { return showOnlyLoss; }

    public ObservableList<Project>            getAvailableProjects()       { return availableProjects; }
    public ObservableList<ProjectSummaryDTO>  getProjectSummaries()        { return projectSummaries; }
    public ObservableList<MonthlySnapshotDTO> getMonthlySummaries()        { return monthlySummaries; }
    public ObservableList<ExpenseCategoryDTO> getExpenseCategories()       { return expenseCategories; }
    public ObservableList<IncomeBreakdownDTO> getIncomeBreakdown()         { return incomeBreakdown; }
    public ObservableList<Employee>           getEmployees()               { return employees; }
    public FilteredList<Employee>             getFilteredEmployees()       { return filteredEmployees; }

    public BooleanProperty                    loadingProperty()            { return loading; }
    public StringProperty                     errorMessageProperty()       { return errorMessage; }
    public StringProperty                     totalProfitProperty()        { return totalProfit; }
    public StringProperty                     grossIncomeProperty()        { return grossIncome; }
    public StringProperty                     netExpenseProperty()         { return netExpense; }
    public StringProperty                     netProfitTrendProperty()     { return netProfitTrend; }

    public void setSelectedProject(String name) {
        selectedProjectName = name;
        if (name == null || name.isBlank() || "All Projects".equals(name)) {
            selectedProject.set(null);
            return;
        }
        selectedProject.set(availableProjects.stream()
            .filter(p -> name.equals(p.getName())).findFirst().orElse(null));
    }

    public void setSelectedStatus(String status) { selectedStatus.set(status); }

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
