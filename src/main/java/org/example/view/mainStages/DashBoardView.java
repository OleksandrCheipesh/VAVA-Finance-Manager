package org.example.view.mainStages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.view.templates.BaseView;
import org.example.view.templates.Sidebar;

public class DashBoardView extends BaseView {

    private BorderPane root;

    // CONTENT
    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);
        root.setCenter(buildContent());
        scene = new Scene(root);
        stage.setTitle("App Manager - Dashboard");
    }

    // STYLES
    @Override
    protected void setStyle() {
    }

    // LOGIC
    @Override
    protected void setLogic() {
    }

    // BUILDERS
    private VBox buildContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #F5F7FA;");

        content.getChildren().addAll(
                buildContentTitle("Dashboard"),
                buildStatsCards(),
                buildChartsSection(),
                createRecentActivityTable()
        );

        return content;
    }

    private HBox buildStatsCards() {
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER_LEFT);

        statsCards.getChildren().addAll(
                createStatCard("Total Employees", "156", "#2D7A9E"),
                createStatCard("Active Projects", "23", "#27AE60"),
                createStatCard("Budget Used", "$1.2M", "#E67E22"),
                createStatCard("Reports", "45", "#9B59B6")
        );

        return statsCards;
    }

    private HBox buildChartsSection() {
        HBox chartsSection = new HBox(20);
        chartsSection.setPrefHeight(300);

        VBox chart1 = createChartPlaceholder("Revenue Overview");
        VBox chart2 = createChartPlaceholder("Employee Distribution");

        HBox.setHgrow(chart1, Priority.ALWAYS);
        HBox.setHgrow(chart2, Priority.ALWAYS);

        chartsSection.getChildren().addAll(chart1, chart2);
        return chartsSection;
    }

    // FACTORY METHODS
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 14px;");

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private VBox createChartPlaceholder(String title) {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #1A1F2B;");

        Region chartPlaceholder = new Region();
        chartPlaceholder.setPrefHeight(200);
        chartPlaceholder.setStyle(
                "-fx-background-color: #ECF0F1;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #BDC3C7;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: 1;"
        );

        StackPane placeholderStack = new StackPane();
        placeholderStack.getChildren().addAll(chartPlaceholder, new Label("Chart Preview"));

        container.getChildren().addAll(titleLabel, placeholderStack);
        return container;
    }

    private VBox createRecentActivityTable() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label titleLabel = new Label("Recent Activity");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #1A1F2B;");

        container.getChildren().addAll(titleLabel, buildTableHeader(), buildTableRows());
        return container;
    }

    private HBox buildTableHeader() {
        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(10, 0, 10, 0));
        tableHeader.setStyle("-fx-border-color: #ECF0F1; -fx-border-width: 0 0 2 0;");

        String[] headers = {"Activity", "User", "Date", "Status"};
        double[] widths = {0.4, 0.2, 0.2, 0.2};

        for (int i = 0; i < headers.length; i++) {
            Label header = new Label(headers[i]);
            header.setStyle("-fx-font-weight: bold; -fx-text-fill: #7F8C8D;");
            header.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(header, Priority.ALWAYS);
            header.prefWidthProperty().bind(tableHeader.widthProperty().multiply(widths[i]));
            tableHeader.getChildren().add(header);
        }

        return tableHeader;
    }

    private VBox buildTableRows() {
        VBox tableRows = new VBox(5);

        String[][] activities = {
                {"New employee added",       "John Smith",    "2026-02-26", "Completed"},
                {"Budget updated",           "Sarah Johnson", "2026-02-25", "Pending"},
                {"Report generated",         "Mike Wilson",   "2026-02-25", "Completed"},
                {"Company settings changed", "Emily Brown",   "2026-02-24", "Completed"},
                {"Employee terminated",      "Robert Davis",  "2026-02-24", "Warning"}
        };

        for (String[] activity : activities) {
            tableRows.getChildren().add(
                    createTableRow(activity[0], activity[1], activity[2], activity[3])
            );
        }

        return tableRows;
    }

    private HBox createTableRow(String activity, String user, String date, String status) {
        HBox row = new HBox();
        row.setPadding(new Insets(8, 0, 8, 0));
        row.setStyle("-fx-border-color: #F0F3F4; -fx-border-width: 0 0 1 0;");

        row.getChildren().addAll(
                createRowCell(activity, row, 0.4),
                createRowCell(user, row, 0.2),
                createRowCell(date, row, 0.2),
                createStatusLabel(status, row)
        );

        return row;
    }

    private Label createRowCell(String text, HBox row, double widthRatio) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.prefWidthProperty().bind(row.widthProperty().multiply(widthRatio));
        return label;
    }

    private Label createStatusLabel(String status, HBox row) {
        Label label = createRowCell(status, row, 0.2);

        String color = switch (status) {
            case "Completed" -> "#27AE60";
            case "Pending"   -> "#F39C12";
            case "Warning"   -> "#E74C3C";
            default          -> "#7F8C8D";
        };

        label.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        return label;
    }
}