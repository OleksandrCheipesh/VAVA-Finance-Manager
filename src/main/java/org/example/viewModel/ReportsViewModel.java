package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.database.entity.Project;
import org.example.model.reports.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class ReportsViewModel {

    // ----------------------------------------------------------------
    // Filter state — bound to ReportFilterBar in the view
    // ----------------------------------------------------------------
    private final ObjectProperty<Project>   selectedProject = new SimpleObjectProperty<>(null);
    private final ObjectProperty<PeriodType> selectedPeriod = new SimpleObjectProperty<>(PeriodType.THIS_MONTH);
    private final ObjectProperty<LocalDate>  customFrom     = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate>  customTo       = new SimpleObjectProperty<>();

    // ----------------------------------------------------------------
    // Chart data — bound to chart components
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
        loadDummyData();
        // TODO: replace loadDummyData() with recompute() once ReportService is ready
        // Add listeners to filter properties so recompute() fires on filter change:
        // selectedProject.addListener((obs, o, n) -> recompute());
        // selectedPeriod.addListener((obs, o, n)  -> recompute());
    }

    // ----------------------------------------------------------------
    // Called when any filter changes — runs service queries and
    // repopulates all ObservableLists.
    // TODO: implement when ReportService is ready
    // ----------------------------------------------------------------
    public void recompute() {
        // loading.set(true);
        // Task<Void> task = new Task<>() {
        //     protected Void call() throws Exception {
        //         FilterState filter = buildFilterState();
        //         List<ProjectSummaryDTO>  ps = reportModel.getProjectSummaries(filter);
        //         List<MonthlySnapshotDTO> ms = reportModel.getMonthlySummary(filter);
        //         ...
        //         Platform.runLater(() -> {
        //             projectSummaries.setAll(ps);
        //             monthlySummaries.setAll(ms);
        //             loading.set(false);
        //         });
        //         return null;
        //     }
        // };
        // new Thread(task).start();
    }

    // ----------------------------------------------------------------
    // Dummy data — frontend uses this until backend connects ReportService
    // ----------------------------------------------------------------
    private void loadDummyData() {
        projectSummaries.setAll(
            new ProjectSummaryDTO(1, "ERP Implementation",  bd(44000), bd(16100), bd(27900),  12.5),
            new ProjectSummaryDTO(2, "Website Redesign",    bd(22500), bd(7600),  bd(14900),  -3.2),
            new ProjectSummaryDTO(3, "Data Migration",      bd(28700), bd(8400),  bd(20300),   8.1),
            new ProjectSummaryDTO(4, "Mobile App",          bd(75000), bd(21700), bd(53300),  22.4),
            new ProjectSummaryDTO(5, "Security Audit",      bd(15800), bd(5000),  bd(10800),   0.0)
        );

        monthlySummaries.setAll(
            new MonthlySnapshotDTO(YearMonth.of(2025, 11), bd(22500), bd(24000), bd(-1500)),
            new MonthlySnapshotDTO(YearMonth.of(2025, 12), bd(31200), bd(25050), bd(6150)),
            new MonthlySnapshotDTO(YearMonth.of(2026,  1), bd(46000), bd(28050), bd(17950)),
            new MonthlySnapshotDTO(YearMonth.of(2026,  2), bd(41300), bd(28400), bd(12900)),
            new MonthlySnapshotDTO(YearMonth.of(2026,  3), bd(44500), bd(30000), bd(14500)),
            new MonthlySnapshotDTO(YearMonth.of(2026,  4), bd(41000), bd(29700), bd(11300))
        );

        expenseCategories.setAll(
            new ExpenseCategoryDTO("Salaries",    bd(91200), 52.4),
            new ExpenseCategoryDTO("Contractors", bd(25600), 14.7),
            new ExpenseCategoryDTO("Equipment",   bd(18500), 10.6),
            new ExpenseCategoryDTO("Software",    bd(12400),  7.1),
            new ExpenseCategoryDTO("Cloud & Infra",bd(8900),  5.1),
            new ExpenseCategoryDTO("Other",       bd(17600), 10.1)
        );

        incomeBreakdown.setAll(
            new IncomeBreakdownDTO("Mobile App",          bd(75000), 40.6),
            new IncomeBreakdownDTO("ERP Implementation",  bd(44000), 23.8),
            new IncomeBreakdownDTO("Data Migration",      bd(28700), 15.5),
            new IncomeBreakdownDTO("Website Redesign",    bd(22500), 12.2),
            new IncomeBreakdownDTO("Security Audit",      bd(15800),  8.6)
        );
    }

    private BigDecimal bd(double value) {
        return BigDecimal.valueOf(value);
    }

    // ----------------------------------------------------------------
    // Filter state helpers
    // ----------------------------------------------------------------
    public LocalDate resolvedFrom() {
        return switch (selectedPeriod.get()) {
            case THIS_MONTH   -> LocalDate.now().withDayOfMonth(1);
            case LAST_MONTH   -> LocalDate.now().minusMonths(1).withDayOfMonth(1);
            case THIS_QUARTER -> LocalDate.now().withDayOfMonth(1).withMonth(
                                    ((LocalDate.now().getMonthValue() - 1) / 3) * 3 + 1);
            case THIS_YEAR    -> LocalDate.now().withDayOfYear(1);
            case CUSTOM       -> customFrom.get();
        };
    }

    public LocalDate resolvedTo() {
        return switch (selectedPeriod.get()) {
            case THIS_MONTH   -> LocalDate.now();
            case LAST_MONTH   -> LocalDate.now().minusMonths(1)
                                    .withDayOfMonth(LocalDate.now().minusMonths(1)
                                    .lengthOfMonth());
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
    // Getters
    // ----------------------------------------------------------------
    public ObjectProperty<Project>            selectedProjectProperty()  { return selectedProject; }
    public ObjectProperty<PeriodType>         selectedPeriodProperty()   { return selectedPeriod; }
    public ObjectProperty<LocalDate>          customFromProperty()       { return customFrom; }
    public ObjectProperty<LocalDate>          customToProperty()         { return customTo; }
    public ObservableList<ProjectSummaryDTO>  getProjectSummaries()      { return projectSummaries; }
    public ObservableList<MonthlySnapshotDTO> getMonthlySummaries()      { return monthlySummaries; }
    public ObservableList<ExpenseCategoryDTO> getExpenseCategories()     { return expenseCategories; }
    public ObservableList<IncomeBreakdownDTO> getIncomeBreakdown()       { return incomeBreakdown; }
    public BooleanProperty                    loadingProperty()          { return loading; }
    public StringProperty                     errorMessageProperty()     { return errorMessage; }
}
