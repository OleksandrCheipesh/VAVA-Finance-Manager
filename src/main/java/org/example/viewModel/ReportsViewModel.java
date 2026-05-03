package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.database.entity.Project;
import org.example.model.reports.*;

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

    // ----------------------------------------------------------------
    // Master dummy data — never modified, always filtered from
    // ----------------------------------------------------------------
    private static final List<MonthlySnapshotDTO> ALL_MONTHLY = List.of(
            new MonthlySnapshotDTO(YearMonth.of(2025, 11), bd(22500), bd(24000), bd(-1500)),
            new MonthlySnapshotDTO(YearMonth.of(2025, 12), bd(31200), bd(25050), bd(6150)),
            new MonthlySnapshotDTO(YearMonth.of(2026,  1), bd(46000), bd(28050), bd(17950)),
            new MonthlySnapshotDTO(YearMonth.of(2026,  2), bd(41300), bd(28400), bd(12900)),
            new MonthlySnapshotDTO(YearMonth.of(2026,  3), bd(44500), bd(30000), bd(14500)),
            new MonthlySnapshotDTO(YearMonth.of(2026,  4), bd(41000), bd(29700), bd(11300))
    );

    private static final List<ProjectSummaryDTO> ALL_PROJECTS = List.of(
            new ProjectSummaryDTO(1, "ERP Implementation",  bd(44000), bd(16100), bd(27900),  12.5),
            new ProjectSummaryDTO(2, "Website Redesign",    bd(22500), bd(7600),  bd(14900),  -3.2),
            new ProjectSummaryDTO(3, "Data Migration",      bd(28700), bd(8400),  bd(20300),   8.1),
            new ProjectSummaryDTO(4, "Mobile App",          bd(75000), bd(21700), bd(53300),  22.4),
            new ProjectSummaryDTO(5, "Security Audit",      bd(15800), bd(5000),  bd(10800),   0.0)
    );

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
        loadStaticLists();
        recompute();          // populate monthlySummaries / projectSummaries immediately
    }

    // ----------------------------------------------------------------
    // recompute() — filters ALL_MONTHLY and ALL_PROJECTS by the
    // current filter state, then groups by Month or Quarter,
    // and calls setAll() so chart ListChangeListeners fire.
    // ----------------------------------------------------------------
    public void recompute() {
        LocalDate from = resolvedFrom();
        LocalDate to   = resolvedTo();

        // --- Monthly snapshots: filter by date range ---
        List<MonthlySnapshotDTO> filtered = ALL_MONTHLY.stream()
                .filter(s -> {
                    LocalDate first = s.getPeriod().atDay(1);
                    LocalDate last  = s.getPeriod().atEndOfMonth();
                    // include if the month overlaps [from, to]
                    if (from != null && last.isBefore(from))  return false;
                    if (to   != null && first.isAfter(to))    return false;
                    return true;
                })
                .collect(Collectors.toList());

        // --- Group by Quarter if requested ---
        if ("Quarter".equals(grouping.get())) {
            filtered = groupByQuarter(filtered);
        }

        monthlySummaries.setAll(filtered);

        // --- Project summaries: no date filter on dummy data, just pass through ---
        // (when a real service exists, pass from/to/status here)
        projectSummaries.setAll(ALL_PROJECTS);
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
            // Use first month of the quarter as the period so axis labels show correctly
            YearMonth  period  = bucket.get(0).getPeriod();
            quarters.add(new MonthlySnapshotDTO(period, income, expense, net));
        }
        return quarters;
    }

    // ----------------------------------------------------------------
    // Static lists (expense categories, income breakdown) — not date-
    // filtered in dummy mode; just loaded once.
    // ----------------------------------------------------------------
    private void loadStaticLists() {
        expenseCategories.setAll(
                new ExpenseCategoryDTO("Salaries",     bd(91200), 52.4),
                new ExpenseCategoryDTO("Contractors",  bd(25600), 14.7),
                new ExpenseCategoryDTO("Equipment",    bd(18500), 10.6),
                new ExpenseCategoryDTO("Software",     bd(12400),  7.1),
                new ExpenseCategoryDTO("Cloud & Infra",bd(8900),   5.1),
                new ExpenseCategoryDTO("Other",        bd(17600), 10.1)
        );
        incomeBreakdown.setAll(
                new IncomeBreakdownDTO("Mobile App",         bd(75000), 40.6),
                new IncomeBreakdownDTO("ERP Implementation", bd(44000), 23.8),
                new IncomeBreakdownDTO("Data Migration",     bd(28700), 15.5),
                new IncomeBreakdownDTO("Website Redesign",   bd(22500), 12.2),
                new IncomeBreakdownDTO("Security Audit",     bd(15800),  8.6)
        );
    }

    private static BigDecimal bd(double value) {
        return BigDecimal.valueOf(value);
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

    public void setSelectedProject(String name) {
        selectedProjectName = name;
    }

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
    public SummaryDTO getSummary() {
        BigDecimal totalRevenue = monthlySummaries.stream()
                .map(MonthlySnapshotDTO::getIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCosts = monthlySummaries.stream()
                .map(MonthlySnapshotDTO::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netProfit = totalRevenue.subtract(totalCosts);

        return new SummaryDTO(
                totalRevenue,
                totalCosts,
                netProfit,
                netProfit
        );
    }
}