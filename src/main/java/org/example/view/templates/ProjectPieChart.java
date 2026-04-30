package org.example.view.templates;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import org.example.model.reports.ProjectSummaryDTO;

import java.text.NumberFormat;
import org.example.logging.AppLog;
import java.util.List;
import javafx.scene.control.Tooltip;
import java.util.Locale;

public class ProjectPieChart extends HBox {

    private final PieChart chart;
    private final VBox customLegend;

    private static final String[] COLORS = {
            Themes.PRIMARY, "#3B82F6", "#10B981", "#F59E0B", "#6366F1"
    };

    private static final org.example.logging.AppLog.SimpleLogger logger = AppLog.getLogger(ProjectPieChart.class);

    public ProjectPieChart(ObservableList<ProjectSummaryDTO> data) {
        super(10);

        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(10, 0, 10, 0));

        chart = new PieChart();
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setMinSize(120, 120);
        chart.setPrefSize(120, 120);

        customLegend = new VBox(15);
        customLegend.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(customLegend, Priority.ALWAYS);

        getChildren().addAll(chart, customLegend);

        try {
            getStylesheets().add(getClass().getResource("/styles/charts.css").toExternalForm());
        } catch (Exception ignored) {}

        // Build with initial data
        rebuild(data);

        // React to future changes
        data.addListener((ListChangeListener<ProjectSummaryDTO>) c -> rebuild(data));
    }

    /** Called externally when a local (in-modal) filter needs to transform the displayed data. */
    public void rebuildFrom(java.util.Collection<ProjectSummaryDTO> data) {
        rebuild(new java.util.ArrayList<>(data));
    }

    private void rebuild(List<ProjectSummaryDTO> data) {
        chart.getData().clear();
        customLegend.getChildren().clear();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        int colorIndex = 0;

        // Log incoming data to aid debugging when pie appears unexpected
        try {
            logger.info("Rebuilding ProjectPieChart with {} entries", data.size());
            for (ProjectSummaryDTO d : data) {
                logger.debug("ProjectSummaryDTO: id={} name={} netProfit={} mom={}", d.getProjectId(), d.getProjectName(), d.getNetProfit(), d.getMonthOverMonthPct());
            }
        } catch (Exception ignored) {}

        // If only a single project is provided, show a 2-slice chart representing Income vs Expenses
        if (data.size() == 1) {
            ProjectSummaryDTO dto = data.get(0);
            double income = dto.getTotalIncome() == null ? 0.0 : dto.getTotalIncome().doubleValue();
            double expense = dto.getTotalExpense() == null ? 0.0 : dto.getTotalExpense().doubleValue();

            // If both zero, fall back to placeholder
            if (income == 0.0 && expense == 0.0) {
                PieChart.Data placeholder = new PieChart.Data("No data", 1);
                chart.getData().add(placeholder);
                javafx.application.Platform.runLater(() -> { if (placeholder.getNode() != null) placeholder.getNode().setStyle("-fx-pie-color: #E5E7EB;"); });
                Label placeholderLabel = new Label("No profit/loss data for selected period");
                placeholderLabel.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 12px;");
                customLegend.getChildren().add(placeholderLabel);
                return;
            }

            PieChart.Data incomeSlice = new PieChart.Data("Income", Math.max(0, income));
            PieChart.Data expenseSlice = new PieChart.Data("Expenses", Math.max(0, expense));
            chart.getData().addAll(incomeSlice, expenseSlice);

            // Legend for Income
            HBox incomeItem = new HBox(5);
            incomeItem.setAlignment(Pos.CENTER_LEFT);
            Region incomeDot = new Region();
            incomeDot.setMinSize(10, 10);
            incomeDot.setMaxSize(10, 10);
            incomeDot.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 50%;");
            Label incomeLabel = new Label("Income");
            incomeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");
            Label incomeAmount = new Label(currencyFormat.format(income));
            incomeAmount.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            Region spacer1 = new Region(); HBox.setHgrow(spacer1, Priority.ALWAYS);
            incomeItem.getChildren().addAll(incomeDot, incomeLabel, incomeAmount, spacer1);
            customLegend.getChildren().add(incomeItem);

            // Legend for Expenses
            HBox expenseItem = new HBox(5);
            expenseItem.setAlignment(Pos.CENTER_LEFT);
            Region expenseDot = new Region();
            expenseDot.setMinSize(10, 10);
            expenseDot.setMaxSize(10, 10);
            expenseDot.setStyle("-fx-background-color: #EF4444; -fx-background-radius: 50%;");
            Label expenseLabel = new Label("Expenses");
            expenseLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");
            Label expenseAmount = new Label(currencyFormat.format(expense));
            expenseAmount.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);
            expenseItem.getChildren().addAll(expenseDot, expenseLabel, expenseAmount, spacer2);
            customLegend.getChildren().add(expenseItem);

            // Apply styles and tooltips on FX thread
            javafx.application.Platform.runLater(() -> {
                if (incomeSlice.getNode() != null) {
                    incomeSlice.getNode().setStyle("-fx-pie-color: " + Themes.PRIMARY + ";");
                    Tooltip.install(incomeSlice.getNode(), new Tooltip("Income: " + currencyFormat.format(income)));
                }
                if (expenseSlice.getNode() != null) {
                    expenseSlice.getNode().setStyle("-fx-pie-color: #EF4444;");
                    Tooltip.install(expenseSlice.getNode(), new Tooltip("Expenses: " + currencyFormat.format(expense)));
                }
            });

            return;
        }

        // Multi-project: each slice represents a project's net profit magnitude (keeps original behavior)
        for (ProjectSummaryDTO dto : data) {
            if (dto.getNetProfit() == null) continue;
            double raw = dto.getNetProfit().doubleValue();
            if (raw == 0.0) continue;
            double magnitude = Math.abs(raw);

            // Use a unique slice name (project name + id) so identical project names don't collapse
            String sliceName = dto.getProjectName() + " (#" + dto.getProjectId() + ")";
            PieChart.Data slice = new PieChart.Data(sliceName, magnitude);
            chart.getData().add(slice);

            String color = raw < 0 ? "#EF4444" : COLORS[colorIndex % COLORS.length];

            // Legend row
            HBox legendItem = new HBox(5);
            legendItem.setAlignment(Pos.CENTER_LEFT);

            Region dot = new Region();
            dot.setMinSize(10, 10);
            dot.setMaxSize(10, 10);
            dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50%;");

            Label nameLabel = new Label(dto.getProjectName());
            nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");
            nameLabel.setMinWidth(40);
            nameLabel.setMaxWidth(70);
            nameLabel.setPrefWidth(70);

            // Show absolute magnitude in pie legend, but indicate sign for negative values
            String amountText;
            if (raw < 0) {
                amountText = "-" + currencyFormat.format(magnitude);
            } else {
                amountText = currencyFormat.format(magnitude);
            }
            Label amountLabel = new Label(amountText);
            amountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            amountLabel.setMinWidth(60);
            amountLabel.setMaxWidth(60);
            amountLabel.setAlignment(Pos.CENTER_RIGHT);

            // Null-safe badge for monthOverMonthPct
            Double mom = dto.getMonthOverMonthPct();
            Label badge;
            if (mom == null) {
                badge = new Label("N/A");
                badge.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #9CA3AF; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 4; -fx-background-radius: 10;");
            } else {
                String prefix = mom >= 0 ? "+" : "";
                badge = new Label(prefix + String.format(Locale.US, "%.1f%%", mom));
                if (mom >= 0) {
                    badge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #059669; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 4; -fx-background-radius: 10;");
                } else {
                    badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 4; -fx-background-radius: 10;");
                }
            }
            badge.setMinWidth(45);
            badge.setMaxWidth(45);
            badge.setAlignment(Pos.CENTER);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            legendItem.getChildren().addAll(dot, nameLabel, amountLabel, spacer, badge);
            customLegend.getChildren().add(legendItem);
            // Apply slice color if node already available, otherwise schedule on FX thread
            final String c = color;
            if (slice.getNode() != null) {
                slice.getNode().setStyle("-fx-pie-color: " + c + ";");
                // Attach tooltip immediately
                String tipText = dto.getProjectName() + "\n" + (raw < 0 ? "Net: -" : "Net: ") + currencyFormat.format(magnitude) + "\nMoM: " + (dto.getMonthOverMonthPct() == null ? "N/A" : String.format(Locale.US, "%.1f%%", dto.getMonthOverMonthPct()));
                Tooltip.install(slice.getNode(), new Tooltip(tipText));
            } else {
                javafx.application.Platform.runLater(() -> {
                    if (slice.getNode() != null) {
                        slice.getNode().setStyle("-fx-pie-color: " + c + ";");
                        String tipText = dto.getProjectName() + "\n" + (raw < 0 ? "Net: -" : "Net: ") + currencyFormat.format(magnitude) + "\nMoM: " + (dto.getMonthOverMonthPct() == null ? "N/A" : String.format(Locale.US, "%.1f%%", dto.getMonthOverMonthPct()));
                        Tooltip.install(slice.getNode(), new Tooltip(tipText));
                    }
                });
            }

            colorIndex++;
        }

        // If nothing added (all zero / no data), add a placeholder slice so UI doesn't break
        if (chart.getData().isEmpty()) {
            PieChart.Data placeholder = new PieChart.Data("No data", 1);
            chart.getData().add(placeholder);
            if (placeholder.getNode() != null) placeholder.getNode().setStyle("-fx-pie-color: #E5E7EB;");
            else javafx.application.Platform.runLater(() -> { if (placeholder.getNode() != null) placeholder.getNode().setStyle("-fx-pie-color: #E5E7EB;"); });
            Label placeholderLabel = new Label("No profit/loss data for selected period");
            placeholderLabel.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 12px;");
            customLegend.getChildren().add(placeholderLabel);
        }
    }
}