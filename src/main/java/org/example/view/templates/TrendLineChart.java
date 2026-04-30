package org.example.view.templates;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import org.example.model.reports.MonthlySnapshotDTO;

import java.util.List;

public class TrendLineChart extends VBox {

    private final XYChart.Series<String, Number> series;
    private final LineChart<String, Number> chart;
    private final NumberAxis yAxis;
    private Line zeroLine;

    public TrendLineChart(ObservableList<MonthlySnapshotDTO> data) {
        setPadding(new Insets(10));

        CategoryAxis xAxis = new CategoryAxis();
        yAxis = new NumberAxis();

        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(true);
        chart.getStyleClass().add("trend-line-chart");

        series = new XYChart.Series<>();
        series.setName(I18n.t("Net Profit"));
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

        rebuild(data);
        data.addListener((ListChangeListener<MonthlySnapshotDTO>) c -> rebuild(data));
    }

    private void rebuild(List<MonthlySnapshotDTO> data) {
        series.getData().clear();
        for (MonthlySnapshotDTO snapshot : data) {
            String month = snapshot.getPeriod().getMonth().name().substring(0, 3);
            series.getData().add(new XYChart.Data<>(month, snapshot.getNetProfit().doubleValue()));
        }
    }

    /**
     * Show or hide a horizontal zero line drawn across the chart plot area.
     * The line is added as an overlay on the chart's plot background.
     */
    public void setZeroLineVisible(boolean visible) {
        if (visible) {
            if (zeroLine == null) {
                zeroLine = new Line();
                zeroLine.setStroke(javafx.scene.paint.Color.web("#64748B"));
                zeroLine.setStrokeWidth(1.5);
                zeroLine.getStrokeDashArray().addAll(6.0, 4.0);
                zeroLine.setMouseTransparent(true);
            }
            // Position it once the chart has been laid out
            javafx.application.Platform.runLater(() -> {
                Node plotArea = chart.lookup(".chart-plot-background");
                if (plotArea != null && plotArea.getScene() != null) {
                    javafx.scene.layout.Pane chartPane = (javafx.scene.layout.Pane) chart.lookup(".chart-content");
                    if (chartPane != null && !chartPane.getChildren().contains(zeroLine)) {
                        chartPane.getChildren().add(zeroLine);
                    }
                    // Convert y=0 from data coords to display coords
                    double displayY = yAxis.getDisplayPosition(0);
                    javafx.geometry.Bounds plotBounds = plotArea.getBoundsInParent();
                    zeroLine.setStartX(plotBounds.getMinX());
                    zeroLine.setEndX(plotBounds.getMaxX());
                    zeroLine.setStartY(plotBounds.getMinY() + displayY);
                    zeroLine.setEndY(plotBounds.getMinY() + displayY);
                    zeroLine.setVisible(true);
                }
            });
        } else {
            if (zeroLine != null) {
                zeroLine.setVisible(false);
            }
        }
    }
}