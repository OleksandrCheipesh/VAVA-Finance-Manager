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
import java.util.List;
import java.util.Locale;

public class ProjectPieChart extends HBox {

    private final PieChart chart;
    private final VBox customLegend;

    private static final String[] COLORS = {
            Themes.PRIMARY, "#3B82F6", "#10B981", "#F59E0B", "#6366F1"
    };

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

        for (ProjectSummaryDTO dto : data) {
            // Add pie slice
            PieChart.Data slice = new PieChart.Data(
                    dto.getProjectName(),
                    dto.getNetProfit().doubleValue()
            );
            chart.getData().add(slice);

            String color = COLORS[colorIndex % COLORS.length];

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

            Label amountLabel = new Label(currencyFormat.format(dto.getNetProfit()));
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

            colorIndex++;
        }
    }
}