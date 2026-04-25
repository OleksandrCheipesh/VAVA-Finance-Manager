package org.example.view.templates;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.model.reports.MonthlySnapshotDTO;

public class IncomeExpenseBarChart extends VBox {

    public IncomeExpenseBarChart(ObservableList<MonthlySnapshotDTO> data) {
        setPadding(new Insets(10));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);

        chart.setAnimated(false);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("income-expense-chart");

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        for (MonthlySnapshotDTO snapshot : data) {
            String month = snapshot.getPeriod().getMonth().name().substring(0, 3);

            incomeSeries.getData().add(new XYChart.Data<>(month, snapshot.getIncome().doubleValue()));
            expenseSeries.getData().add(new XYChart.Data<>(month, snapshot.getExpense().doubleValue()));
        }

        chart.getData().addAll(incomeSeries, expenseSeries);

        VBox.setVgrow(chart, Priority.ALWAYS);

        getChildren().add(chart);

        try {
            getStylesheets().add(getClass().getResource("/styles/charts.css").toExternalForm());
        } catch (Exception ignored) {}

        chart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
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
                });
            }
        });
    }
}