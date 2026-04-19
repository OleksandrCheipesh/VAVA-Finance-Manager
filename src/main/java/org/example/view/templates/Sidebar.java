package org.example.view.templates;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.SessionManager;
import org.example.model.database.entity.User;
import org.example.view.authentication.LoginView;
import org.example.view.mainStages.*;

public class Sidebar {

    private VBox sidebarNode;
    private VBox menuItems;
    private Stage stage;

    private static String activeLabel = "Dashboard";

    public Sidebar(Stage stage) {
        this.stage = stage;
    }

    public VBox getSidebar() {
        if (sidebarNode == null) sidebarNode = buildSidebar();
        return sidebarNode;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: " + Themes.SIDEBAR_BG + ";");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(buildSidebarTitle(), buildMenuItems(), spacer, buildProfileCard());
        return sidebar;
    }

    private HBox buildSidebarTitle() {
        HBox logoBox = new HBox(15);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 0, 10, 0));

        Region logo = new Region();
        logo.setPrefSize(36, 36);
        logo.setMinSize(36, 36);
        logo.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 10;");

        String compName;
        try {
            compName = SessionManager.getInstance().getCurrentCompany().getName();
        } catch (Exception e) {
            compName = "Unknown";
        }
        Label adminLabel = new Label(compName);
        adminLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: white;");

        logoBox.getChildren().addAll(logo, adminLabel);
        return logoBox;
    }

    private VBox buildMenuItems() {
        menuItems = new VBox(5);
        menuItems.setAlignment(Pos.TOP_LEFT);

        String[] menuLabels = {"Dashboard", "Transactions", "Employees", "Projects", "Budget", "Reports", "Settings"};

        for (String label : menuLabels) {
            HBox menuItem = createMenuItem(label);
            Platform.runLater(() -> {
                String windowTitle = stage.getTitle() != null ? stage.getTitle() : "";
                if (windowTitle.contains(label)) {
                    activeLabel = label;
                    applyActiveMenuStyle(menuItem);
                }
            });
            menuItems.getChildren().add(menuItem);
        }
        return menuItems;
    }

    private HBox createMenuItem(String text) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setCursor(Cursor.HAND);
        box.setSpacing(20);

        String iconName = switch (text) {
            case "Dashboard" -> "layout-dashboard";
            case "Transactions" -> "receipt";
            case "Employees" -> "users-round";
            case "Projects" -> "folder";
            case "Budget" -> "wallet";
            case "Reports" -> "chart-column-big";
            case "Settings", "Manage Company" -> "settings";
            default -> "folder";
        };

        // Load the PNG image
        ImageView iconView = IconFactory.getIcon(iconName, 24);

        Label label = new Label(text);
        box.getChildren().addAll(iconView, label);

        applyDefaultMenuStyle(box);

        box.setOnMouseEntered(e -> {
            String windowTitle = stage.getTitle() != null ? stage.getTitle() : "";
            if (!windowTitle.contains(text)) applyHoverMenuStyle(box);
        });

        box.setOnMouseExited(e -> {
            String windowTitle = stage.getTitle() != null ? stage.getTitle() : "";
            if (!windowTitle.contains(text)) applyDefaultMenuStyle(box);
        });

        box.setOnMouseClicked(e -> navigateTo(text));
        return box;
    }

    private VBox buildProfileCard() {
        VBox profileCard = new VBox(15);
        profileCard.setStyle("-fx-background-color: " + Themes.SIDEBAR_PROFILE_BG + "; -fx-background-radius: 12; -fx-padding: 16;");

        VBox textContainer = new VBox(2);

        User user = SessionManager.getInstance().getCurrentUser();
        String fullName = (user != null)
                ? user.getName() + " " + user.getSurname()
                : "Unknown User";

        String roleText = (user != null && user.getPosition() != null)
                ? user.getPosition().name()
                : "No Role";

        Label name = new Label(fullName);
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label role = new Label(roleText);
        role.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        textContainer.getChildren().addAll(name, role);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_BG + "; -fx-text-fill: #D1D5DB; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_BG + "; -fx-text-fill: #D1D5DB; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;"));
        logoutBtn.setOnMouseClicked(e -> {
            SessionManager.getInstance().logout();

            BaseView loginView = new LoginView();
            loginView.show(stage);
        });


        profileCard.getChildren().addAll(textContainer, logoutBtn);
        return profileCard;
    }

    private void navigateTo(String menuLabel) {
        String windowTitle = stage.getTitle() != null ? stage.getTitle() : "";
        if (windowTitle.contains(menuLabel)) return;

        activeLabel = menuLabel;
        BaseView view = switch (menuLabel) {
            case "Dashboard"      -> new DashBoardView();
            case "Settings"       -> new SettingsView();
            case "Employees"      -> new EmployeesView();
            case "Projects"       -> new ProjectsView();
            case "Budget"         -> new BudgetView();
            case "Reports"        -> new ReportsView();
            case "Transactions"   -> new TransactionsView();
            default               -> null;
        };

        if (view != null) view.show(stage);
    }

    // STYLES - Always keep icons white, just change the opacity!
    private void applyDefaultMenuStyle(HBox box) {
        box.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-padding: 12 16;");

        if (box.getChildren().size() > 0 && box.getChildren().get(0) instanceof ImageView img) {
            // Keep the white filter, just lower the opacity so it matches the muted text
            javafx.scene.effect.ColorAdjust makeWhite = new javafx.scene.effect.ColorAdjust();
            makeWhite.setBrightness(1.0);
            img.setEffect(makeWhite);
            img.setOpacity(0.5); // Faded white looks like the muted gray text
        }
        if (box.getChildren().size() > 1 && box.getChildren().get(1) instanceof Label lbl) {
            lbl.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 15px; -fx-font-weight: bold;");
        }
    }

    private void applyHoverMenuStyle(HBox box) {
        box.setStyle("-fx-background-color: " + Themes.SIDEBAR_HOVER + "; -fx-background-radius: 8; -fx-padding: 12 16;");

        if (box.getChildren().size() > 0 && box.getChildren().get(0) instanceof ImageView img) {
            javafx.scene.effect.ColorAdjust makeWhite = new javafx.scene.effect.ColorAdjust();
            makeWhite.setBrightness(1.0);
            img.setEffect(makeWhite);
            img.setOpacity(1.0); // Full brightness on hover
        }
        if (box.getChildren().size() > 1 && box.getChildren().get(1) instanceof Label lbl) {
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        }
    }

    private void applyActiveMenuStyle(HBox box) {
        box.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 8; -fx-padding: 12 16;");

        if (box.getChildren().size() > 0 && box.getChildren().get(0) instanceof ImageView img) {
            javafx.scene.effect.ColorAdjust makeWhite = new javafx.scene.effect.ColorAdjust();
            makeWhite.setBrightness(1.0);
            img.setEffect(makeWhite);
            img.setOpacity(1.0); // Full brightness when active
        }
        if (box.getChildren().size() > 1 && box.getChildren().get(1) instanceof Label lbl) {
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        }
    }
}