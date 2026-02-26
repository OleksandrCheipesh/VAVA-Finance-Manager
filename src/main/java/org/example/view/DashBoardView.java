package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DashBoardView {

    public void show(Stage stage) {
        BorderPane root = new BorderPane();

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30, 20, 20, 20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #1A1F2B;");

        Label adminLabel = new Label("Admin");
        adminLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        adminLabel.setStyle("-fx-text-fill: white; -fx-padding: 0 0 20 0;");

        VBox menuItems = new VBox(5);
        menuItems.setAlignment(Pos.TOP_LEFT);

        String[] menuLabels = {
                "Dashboard",
                "Settings",
                "Manage Company",
                "Employees",
                "Budget",
                "Reports"
        };

        for (String label : menuLabels) {
            Label menuItem = createMenuItem(label);
            if (label.equals("Dashboard")) {
                menuItem.setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-background-color: #2D7A9E;" +
                                "-fx-padding: 10 15;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-size: 14px;"
                );
            }
            menuItems.getChildren().add(menuItem);
        }

        sidebar.getChildren().addAll(adminLabel, menuItems);

        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #F5F7FA;");

        Label dashboardTitle = new Label("Dashboard");
        dashboardTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        dashboardTitle.setStyle("-fx-text-fill: #1A1F2B;");

        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER_LEFT);

        VBox card1 = createStatCard("Total Employees", "156", "#2D7A9E");
        VBox card2 = createStatCard("Active Projects", "23", "#27AE60");
        VBox card3 = createStatCard("Budget Used", "$1.2M", "#E67E22");
        VBox card4 = createStatCard("Reports", "45", "#9B59B6");

        statsCards.getChildren().addAll(card1, card2, card3, card4);

        HBox chartsSection = new HBox(20);
        chartsSection.setPrefHeight(300);

        VBox chart1 = createChartPlaceholder("Revenue Overview");
        VBox chart2 = createChartPlaceholder("Employee Distribution");

        chartsSection.getChildren().addAll(chart1, chart2);
        HBox.setHgrow(chart1, Priority.ALWAYS);
        HBox.setHgrow(chart2, Priority.ALWAYS);

        VBox recentActivity = createRecentActivityTable();

        content.getChildren().addAll(dashboardTitle, statsCards, chartsSection, recentActivity);

        root.setLeft(sidebar);
        root.setCenter(content);

        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("App Manager - Dashboard");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private Label createMenuItem(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: #B0B7C3;" +
                        "-fx-padding: 10 15;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;"
        );

        label.setOnMouseEntered(e ->
                label.setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-background-color: #2D2F3A;" +
                                "-fx-padding: 10 15;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-size: 14px;" +
                                "-fx-cursor: hand;"
                )
        );

        label.setOnMouseExited(e ->
                label.setStyle(
                        "-fx-text-fill: #B0B7C3;" +
                                "-fx-padding: 10 15;" +
                                "-fx-font-size: 14px;" +
                                "-fx-cursor: hand;"
                )
        );

        return label;
    }

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

        VBox tableRows = new VBox(5);

        String[][] activities = {
                {"New employee added", "John Smith", "2026-02-26", "Completed"},
                {"Budget updated", "Sarah Johnson", "2026-02-25", "Pending"},
                {"Report generated", "Mike Wilson", "2026-02-25", "Completed"},
                {"Company settings changed", "Emily Brown", "2026-02-24", "Completed"},
                {"Employee terminated", "Robert Davis", "2026-02-24", "Warning"}
        };

        for (String[] activity : activities) {
            HBox row = createTableRow(activity[0], activity[1], activity[2], activity[3]);
            tableRows.getChildren().add(row);
        }

        container.getChildren().addAll(titleLabel, tableHeader, tableRows);
        return container;
    }

    private HBox createTableRow(String activity, String user, String date, String status) {
        HBox row = new HBox();
        row.setPadding(new Insets(8, 0, 8, 0));
        row.setStyle("-fx-border-color: #F0F3F4; -fx-border-width: 0 0 1 0;");

        Label activityLabel = new Label(activity);
        activityLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(activityLabel, Priority.ALWAYS);
        activityLabel.prefWidthProperty().bind(row.widthProperty().multiply(0.4));

        Label userLabel = new Label(user);
        userLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(userLabel, Priority.ALWAYS);
        userLabel.prefWidthProperty().bind(row.widthProperty().multiply(0.2));

        Label dateLabel = new Label(date);
        dateLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        dateLabel.prefWidthProperty().bind(row.widthProperty().multiply(0.2));

        Label statusLabel = new Label(status);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        statusLabel.prefWidthProperty().bind(row.widthProperty().multiply(0.2));

        String statusColor = switch (status) {
            case "Completed" -> "#27AE60";
            case "Pending" -> "#F39C12";
            case "Warning" -> "#E74C3C";
            default -> "#7F8C8D";
        };

        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");

        row.getChildren().addAll(activityLabel, userLabel, dateLabel, statusLabel);
        return row;
    }
}