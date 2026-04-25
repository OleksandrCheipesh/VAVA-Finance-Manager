package org.example.view.templates;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.model.reports.MonthlySnapshotDTO;

public class TrendLineChart extends VBox {

    public TrendLineChart(ObservableList<MonthlySnapshotDTO> data) {
        setPadding(new Insets(10));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(true);
        chart.getStyleClass().add("trend-line-chart");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Net Profit");

        for (MonthlySnapshotDTO snapshot : data) {
            String month = snapshot.getPeriod().getMonth().name().substring(0, 3);
            series.getData().add(new XYChart.Data<>(month, snapshot.getNetProfit().doubleValue()));
        }

        chart.getData().add(series);

        VBox.setVgrow(chart, Priority.ALWAYS);

        getChildren().add(chart);

        try {
            getStylesheets().add(getClass().getResource("/styles/charts.css").toExternalForm());
        } catch (Exception ignored) {}

        chart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                for (Node node : chart.lookupAll(".chart-series-line")) {
                    node.setStyle("-fx-stroke: " + Themes.PRIMARY + ";");
                }
                for (Node node : chart.lookupAll(".chart-line-symbol")) {
                    node.setStyle("-fx-background-color: white, " + Themes.PRIMARY + "; -fx-background-insets: 0, 2; -fx-padding: 4px;");
                }
            }
        });
    }
}