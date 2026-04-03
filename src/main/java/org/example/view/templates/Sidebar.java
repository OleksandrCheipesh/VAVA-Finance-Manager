package org.example.view.templates;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.view.mainStages.*;

import java.io.InputStream;

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

    // BUILDERS
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

        Label adminLabel = new Label("Admin");
        adminLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: white;");

        logoBox.getChildren().addAll(logo, adminLabel);

        return logoBox;
    }

    private VBox buildMenuItems() {
        menuItems = new VBox(5);
        menuItems.setAlignment(Pos.TOP_LEFT);

        String[] menuLabels = {"Dashboard", "Transactions", "Accounts", "Employees", "Projects", "Budget", "Reports", "Manage Company", "Settings"};

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
        box.setSpacing(15);

        String svgPath = "";

        switch (text) {
            case "Settings":
                svgPath = "M11.078 2.25c-.917 0-1.699.663-1.85 1.567L9.05 4.889c-.02.12-.115.26-.297.348a7.493 7.493 0 0 0-.986.57c-.166.115-.334.126-.45.083L6.3 5.508a1.875 1.875 0 0 0-2.282.819l-.922 1.597a1.875 1.875 0 0 0 .432 2.385l.84.692c.095.078.17.229.154.43a7.598 7.598 0 0 0 0 1.139c.015.2-.059.352-.153.43l-.841.692a1.875 1.875 0 0 0-.432 2.385l.922 1.597a1.875 1.875 0 0 0 2.282.818l1.019-.382c.115-.043.283-.031.45.082.312.214.641.405.985.57.182.088.277.228.297.35l.178 1.071c.151.904.933 1.567 1.85 1.567h1.844c.916 0 1.699-.663 1.85-1.567l.178-1.072c.02-.12.114-.26.297-.349.344-.165.673-.356.985-.57.167-.114.335-.125.45-.082l1.02.382a1.875 1.875 0 0 0 2.28-.819l.923-1.597a1.875 1.875 0 0 0-.432-2.385l-.84-.692c-.095-.078-.17-.229-.154-.43a7.614 7.614 0 0 0 0-1.139c-.016-.2.059-.352.153-.43l.84-.692c.708-.582.891-1.59.433-2.385l-.922-1.597a1.875 1.875 0 0 0-2.282-.818l-1.02.382c-.114.043-.282.031-.449-.083a7.49 7.49 0 0 0-.985-.57c-.183-.087-.277-.227-.297-.348l-.179-1.072a1.875 1.875 0 0 0-1.85-1.567h-1.843ZM12 15.75a3.75 3.75 0 1 0 0-7.5 3.75 3.75 0 0 0 0 7.5Z";
                break;
            case "Manage Company":
                svgPath = "M1.5 5.625c0-1.036.84-1.875 1.875-1.875h17.25c1.035 0 1.875.84 1.875 1.875v12.75c0 1.035-.84 1.875-1.875 1.875H3.375A1.875 1.875 0 0 1 1.5 18.375V5.625ZM21 9.375A.375.375 0 0 0 20.625 9h-7.5a.375.375 0 0 0-.375.375v1.5c0 .207.168.375.375.375h7.5A.375.375 0 0 0 21 10.875v-1.5Zm0 3.75a.375.375 0 0 0-.375-.375h-7.5a.375.375 0 0 0-.375.375v1.5c0 .207.168.375.375.375h7.5a.375.375 0 0 0 .375-.375v-1.5Zm0 3.75a.375.375 0 0 0-.375-.375h-7.5a.375.375 0 0 0-.375.375v1.5c0 .207.168.375.375.375h7.5a.375.375 0 0 0 .375-.375v-1.5ZM10.875 18.75a.375.375 0 0 0 .375-.375v-1.5a.375.375 0 0 0-.375-.375h-7.5a.375.375 0 0 0-.375.375v1.5c0 .207.168.375.375.375h7.5ZM3.375 15h7.5a.375.375 0 0 0 .375-.375v-1.5a.375.375 0 0 0-.375-.375h-7.5a.375.375 0 0 0-.375.375v1.5c0 .207.168.375.375.375Zm0-3.75h7.5a.375.375 0 0 0 .375-.375v-1.5A.375.375 0 0 0 10.875 9h-7.5A.375.375 0 0 0 3 9.375v1.5c0 .207.168.375.375.375Z";
                break;
            case "Employees":
                svgPath = "M4.5 6.375a4.125 4.125 0 1 1 8.25 0 4.125 4.125 0 0 1-8.25 0ZM14.25 8.625a3.375 3.375 0 1 1 6.75 0 3.375 3.375 0 0 1-6.75 0ZM1.5 19.125a7.125 7.125 0 0 1 14.25 0v.003l-.001.119a.75.75 0 0 1-.363.63 13.067 13.067 0 0 1-6.761 1.873c-2.472 0-4.786-.684-6.76-1.873a.75.75 0 0 1-.364-.63l-.001-.122ZM17.25 19.128l-.001.144a2.25 2.25 0 0 1-.233.96 10.088 10.088 0 0 0 5.06-1.01.75.75 0 0 0 .42-.643 4.875 4.875 0 0 0-6.957-4.611 8.586 8.586 0 0 1 1.71 5.157v.003Z";
                break;
            case "Budget":
                svgPath = "M2.273 5.625A4.483 4.483 0 0 1 5.25 4.5h13.5c1.141 0 2.183.425 2.977 1.125A3 3 0 0 0 18.75 3H5.25a3 3 0 0 0-2.977 2.625Z M2.273 8.625A4.483 4.483 0 0 1 5.25 7.5h13.5c1.141 0 2.183.425 2.977 1.125A3 3 0 0 0 18.75 6H5.25a3 3 0 0 0-2.977 2.625Z M5.25 9a3 3 0 0 0-3 3v6a3 3 0 0 0 3 3h13.5a3 3 0 0 0 3-3v-6a3 3 0 0 0-3-3H5.25Zm7.205 4.155a.75.75 0 0 1 .045 1.06l-1.594 1.75h1.344a.75.75 0 0 1 0 1.5H10.5a.75.75 0 0 1-.556-1.253l1.594-1.75H10.5a.75.75 0 0 1 0-1.5h1.75a.75.75 0 0 1 .705.193Zm3.795-.405a.75.75 0 0 1 .75.75v3a.75.75 0 0 1-1.5 0v-3a.75.75 0 0 1 .75-.75Z";
                break;
            case "Reports":
                svgPath = "M18.375 2.25c-1.035 0-1.875.84-1.875 1.875v15.75c0 1.035.84 1.875 1.875 1.875h.75c1.035 0 1.875-.84 1.875-1.875V4.125c0-1.036-.84-1.875-1.875-1.875h-.75ZM9.75 8.625c0-1.036.84-1.875 1.875-1.875h.75c1.036 0 1.875.84 1.875 1.875v11.25c0 1.035-.84 1.875-1.875 1.875h-.75a1.875 1.875 0 0 1-1.875-1.875V8.625ZM3 13.125c0-1.036.84-1.875 1.875-1.875h.75c1.036 0 1.875.84 1.875 1.875v6.75c0 1.035-.84 1.875-1.875 1.875h-.75A1.875 1.875 0 0 1 3 19.875v-6.75Z";
                break;
            case "Dashboard":
                svgPath = "M3 3h8v8H3zM13 3h8v8h-8zM3 13h8v8H3zM13 13h8v8h-8z";
                break;
            case "Transactions":
                svgPath = "M2.25 3a.75.75 0 0 0-.75.75v16.5c0 .414.336.75.75.75h.75a.75.75 0 0 0 .545-.236l1.955-2.151 1.955 2.151A.75.75 0 0 0 8 21h.001a.75.75 0 0 0 .544-.236l1.956-2.151 1.955 2.151A.75.75 0 0 0 13 21h.001a.75.75 0 0 0 .544-.236l1.956-2.151 1.955 2.151A.75.75 0 0 0 18 21h.75a.75.75 0 0 0 .75-.75V3.75A.75.75 0 0 0 18.75 3H2.25ZM6.75 7.5a.75.75 0 0 0 0 1.5h7.5a.75.75 0 0 0 0-1.5h-7.5Zm0 3a.75.75 0 0 0 0 1.5h7.5a.75.75 0 0 0 0-1.5h-7.5Zm0 3a.75.75 0 0 0 0 1.5h3.75a.75.75 0 0 0 0-1.5H6.75Z";
                break;
            case "Accounts":
                svgPath = "M2.25 8.25h19.5M2.25 9h19.5m-16.5 5.25h6m-6 2.25h3m-3.75 3h15a2.25 2.25 0 0 0 2.25-2.25V6.75A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25v10.5A2.25 2.25 0 0 0 4.5 19.5Z";
                break;
            default:
                svgPath = "";
        }

        // Creating SVG Icon
        javafx.scene.shape.SVGPath icon = new javafx.scene.shape.SVGPath();
        icon.setContent(svgPath);
        icon.setFill(javafx.scene.paint.Color.web(Themes.TEXT_MUTED));

        // Size
        StackPane iconPane = new StackPane(icon);
        iconPane.setMinSize(24, 24);
        iconPane.setPrefSize(24, 24);
        iconPane.setMaxSize(24, 24);

        Label label = new Label(text);
        box.getChildren().addAll(iconPane, label);

        applyDefaultMenuStyle(box);

        // Hover effect
        box.setOnMouseEntered(e -> {
            String windowTitle = stage.getTitle() != null ? stage.getTitle() : "";

            if (!windowTitle.contains(text)) {
                applyHoverMenuStyle(box);

                icon.setFill(javafx.scene.paint.Color.WHITE);
            }
        });

        box.setOnMouseExited(e -> {
            String windowTitle = stage.getTitle() != null ? stage.getTitle() : "";

            if (!windowTitle.contains(text)) {
                applyDefaultMenuStyle(box);

                icon.setFill(javafx.scene.paint.Color.web(Themes.TEXT_MUTED));
            }
        });

        box.setOnMouseClicked(e -> navigateTo(text));

        return box;
    }

    private VBox buildProfileCard() {
        VBox profileCard = new VBox(15);
        profileCard.setStyle("-fx-background-color: " + Themes.SIDEBAR_PROFILE_BG + "; -fx-background-radius: 12; -fx-padding: 16;");

        VBox textContainer = new VBox(2);

        Label name = new Label("Alex Johnson");
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label role = new Label("System Admin");
        role.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        textContainer.getChildren().addAll(name, role);

        Button logoutBtn = new Button("Logout");

        logoutBtn.setMaxWidth(Double.MAX_VALUE);

        // Original
//        logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_BG + "; -fx-text-fill: #D1D5DB; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;");
//        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;"));
//        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_BG + "; -fx-text-fill: #D1D5DB; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;"));


        // TEST TOAST MESSAGE !!!
        logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_BG + "; -fx-text-fill: #D1D5DB; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: " + Themes.SIDEBAR_LOGOUT_BG + "; -fx-text-fill: #D1D5DB; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 0; -fx-cursor: hand;"));

        logoutBtn.setOnMouseClicked(e -> {
            ToastManager.showSuccess(stage, "Toast Message !!");
        });
        // TEST TOAST MESSAGE !!!

        profileCard.getChildren().addAll(textContainer, logoutBtn);

        return profileCard;
    }

    // NAVIGATION
    private void navigateTo(String menuLabel) {
        String windowTitle = stage.getTitle() != null ? stage.getTitle() : "";

        if (windowTitle.contains(menuLabel)) return; // is on same page

        activeLabel = menuLabel; // new page

        BaseView view = switch (menuLabel) {
            case "Dashboard"      -> new DashBoardView();
            case "Settings"       -> new SettingsView();
            case "Manage Company" -> new ManageCompanyView();
            case "Employees"      -> new EmployeesView();
            case "Projects"       -> new ProjectsView();
            case "Budget"         -> new BudgetView();
            case "Reports"        -> new ReportsView();
            case "Transactions"   -> new TransactionsView();
            case "Accounts"       -> new AccountsView();
            default               -> null;
        };

        if (view != null) {
            view.show(stage);
        }
    }

    // STYLES
    private void applyDefaultMenuStyle(HBox box) {
        box.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-padding: 12 16;");

        if (box.getChildren().size() > 1 && box.getChildren().get(1) instanceof Label) {
            ((Label) box.getChildren().get(1)).setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 15px; -fx-font-weight: bold;");
        }
    }

    private void applyHoverMenuStyle(HBox box) {
        box.setStyle("-fx-background-color: " + Themes.SIDEBAR_HOVER + "; -fx-background-radius: 8; -fx-padding: 12 16;");

        if (box.getChildren().size() > 1 && box.getChildren().get(1) instanceof Label) {
            ((Label) box.getChildren().get(1)).setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        }
    }

    private void applyActiveMenuStyle(HBox box) {
        box.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 8; -fx-padding: 12 16;");

        if (box.getChildren().size() > 1 && box.getChildren().get(1) instanceof Label) {
            ((Label) box.getChildren().get(1)).setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        }

        // Color SVG icon to white
        if (box.getChildren().size() > 0 && box.getChildren().get(0) instanceof StackPane) {
            StackPane iconPane = (StackPane) box.getChildren().get(0);

            if (!iconPane.getChildren().isEmpty() && iconPane.getChildren().get(0) instanceof javafx.scene.shape.SVGPath) {
                ((javafx.scene.shape.SVGPath) iconPane.getChildren().get(0)).setFill(javafx.scene.paint.Color.WHITE);
            }
        }
    }
}