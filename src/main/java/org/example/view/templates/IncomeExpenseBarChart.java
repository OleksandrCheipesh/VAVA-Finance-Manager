package org.example.view.templates;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.model.reports.MonthlySnapshotDTO;

import java.util.List;

public class IncomeExpenseBarChart extends VBox {

    private final XYChart.Series<String, Number> incomeSeries;
    private final XYChart.Series<String, Number> expenseSeries;
    private final BarChart<String, Number> chart;

    public IncomeExpenseBarChart(ObservableList<MonthlySnapshotDTO> data) {
        setPadding(new Insets(10));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        chart = new BarChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("income-expense-chart");

        incomeSeries = new XYChart.Series<>();
        incomeSeries.setName(I18n.t("Income"));

        expenseSeries = new XYChart.Series<>();
        expenseSeries.setName(I18n.t("Expenses"));

        chart.getData().addAll(incomeSeries, expenseSeries);

        VBox.setVgrow(chart, Priority.ALWAYS);
        getChildren().add(chart);

        try {
            getStylesheets().add(getClass().getResource("/styles/charts.css").toExternalForm());
        } catch (Exception ignored) {}

        chart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> applyColors());
            }
        });

        // Build with initial data
        rebuild(data);

        // React to future changes
        data.addListener((ListChangeListener<MonthlySnapshotDTO>) c -> {
            rebuild(data);
            Platform.runLater(() -> applyColors());
        });
    }

    private void rebuild(List<MonthlySnapshotDTO> data) {
        incomeSeries.getData().clear();
        expenseSeries.getData().clear();
        for (MonthlySnapshotDTO snapshot : data) {
            String month = snapshot.getPeriod().getMonth().name().substring(0, 3);
            incomeSeries.getData().add(new XYChart.Data<>(month, snapshot.getIncome().doubleValue()));
            expenseSeries.getData().add(new XYChart.Data<>(month, snapshot.getExpense().doubleValue()));
        }
    }

    private void applyColors() {
        for (Node node : chart.lookupAll(".default-color0.chart-bar")) {
            node.setStyle("-fx-bar-fill: " + Themes.TEXT_SUCCESS + ";");
        }
        for (Node node : chart.lookupAll(".default-color0.chart-legend-item-symbol")) {
            node.setStyle("-fx-background-color: " + Themes.TEXT_SUCCESS + ";");
        }
        for (Node node : chart.lookupAll(".default-color1.chart-bar")) {
            node.setStyle("-fx-bar-fill: " + Themes.TEXT_ERROR + "CC;");
        }
        for (Node node : chart.lookupAll(".default-color1.chart-legend-item-symbol")) {
            node.setStyle("-fx-background-color: " + Themes.TEXT_ERROR + "CC;");
        }
    }

    /**
     * Show or hide income / expense series.
     * @param seriesName "Income" or "Expenses"
     * @param visible    true = show, false = hide
     */
    public void setSeriesVisible(String seriesName, boolean visible) {
        XYChart.Series<String, Number> target =
                "Income".equals(seriesName) ? incomeSeries : expenseSeries;

        // Hide bars by setting opacity; removing from chart loses the color index
        for (XYChart.Data<String, Number> d : target.getData()) {
            if (d.getNode() != null) {
                d.getNode().setVisible(visible);
                d.getNode().setManaged(visible);
            }
        }

        // Hide legend item too
        Platform.runLater(() -> {
            for (Node n : chart.lookupAll(".chart-legend-item")) {
                if (n instanceof javafx.scene.layout.HBox hb) {
                    for (javafx.scene.Node child : hb.getChildren()) {
                        if (child instanceof Label lbl && lbl.getText().equals(seriesName)) {
                            hb.setVisible(visible);
                            hb.setManaged(visible);
                        }
                    }
                }
            }
        });
    }
}