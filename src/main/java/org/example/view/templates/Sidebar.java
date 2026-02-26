package org.example.view.templates;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.view.mainStages.*;

public class Sidebar {

    private VBox sidebarNode;
    private VBox menuItems;
    private Stage stage;

    // active page
    private static String activeLabel = "Dashboard";

    public Sidebar(Stage stage) {
        this.stage = stage;
    }

    public VBox getSidebar() {
        if (sidebarNode == null) {
            sidebarNode = buildSidebar();
        }
        return sidebarNode;
    }

    //        BUILDERS
    private VBox buildSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30, 20, 20, 20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #1A1F2B;");

        sidebar.getChildren().addAll(buildSidebarTitle(), buildMenuItems());
        return sidebar;
    }

    private Label buildSidebarTitle() {
        Label adminLabel = new Label("Admin");
        adminLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        adminLabel.setStyle("-fx-text-fill: white; -fx-padding: 0 0 20 0;");
        return adminLabel;
    }

    private VBox buildMenuItems() {
        menuItems = new VBox(5);
        menuItems.setAlignment(Pos.TOP_LEFT);

        String[] menuLabels = {"Dashboard", "Settings", "Manage Company", "Employees", "Budget", "Reports"};

        for (String label : menuLabels) {
            Label menuItem = createMenuItem(label);
            // active
            if (label.equals(activeLabel)) {
                applyActiveMenuStyle(menuItem);
            }
            menuItems.getChildren().add(menuItem);
        }

        return menuItems;
    }

    private Label createMenuItem(String text) {
        Label label = new Label(text);
        applyDefaultMenuStyle(label);

        label.setOnMouseEntered(e -> {
            if (!text.equals(activeLabel)) {
                applyHoverMenuStyle(label);
            }
        });

        label.setOnMouseExited(e -> {
            if (!text.equals(activeLabel)) {
                applyDefaultMenuStyle(label);
            }
        });

        label.setOnMouseClicked(e -> navigateTo(text));

        return label;
    }

    //       NAVIGATION
    private void navigateTo(String menuLabel) {
        if (menuLabel.equals(activeLabel)) return; // is on same page

        activeLabel = menuLabel; // new page

        BaseView view = switch (menuLabel) {
            case "Dashboard"      -> new DashBoardView();
            case "Settings"       -> new SettingsView();
            case "Manage Company" -> new ManageCompanyView();
            case "Employees"      -> new EmployeesView();
            case "Budget"         -> new BudgetView();
            case "Reports"        -> new ReportsView();
            default               -> null;
        };

        if (view != null) {
            view.show(stage);
        }
    }

    //         STYLES
    private void applyDefaultMenuStyle(Label label) {
        label.setStyle(
                "-fx-text-fill: #B0B7C3;" +
                        "-fx-padding: 10 15;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;"
        );
    }

    private void applyHoverMenuStyle(Label label) {
        label.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-background-color: #2D2F3A;" +
                        "-fx-padding: 10 15;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;"
        );
    }

    private void applyActiveMenuStyle(Label label) {
        label.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-background-color: #2D7A9E;" +
                        "-fx-padding: 10 15;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 14px;"
        );
    }
}