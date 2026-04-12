package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.model.database.entity.Employee;
import org.example.view.templates.StateButton;

import java.util.Optional;
import java.util.function.Consumer;

public class EmployeeDetailsDialog {

    public static void show(Stage owner, Employee employee, Consumer<Employee> onUpdate, Consumer<Employee> onDelete) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        javafx.scene.Node backgroundRoot = owner.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(30);
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.4);
        darken.setInput(blur);
        backgroundRoot.setEffect(darken);

        modal.setOnHidden(e -> backgroundRoot.setEffect(null));

        StackPane modalContainer = new StackPane();
        modalContainer.setStyle("-fx-background-color: transparent;");
        modalContainer.setPadding(new Insets(60));

        // INCREASED SIZE & PADDING
        VBox root = new VBox(25);
        root.setPadding(new Insets(25, 40, 40, 40));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 24; -fx-border-radius: 24; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.20), 40, 0, 0, 15);");
        root.setPrefWidth(600); // Wider window per requirements
        root.setMaxWidth(600);

        modalContainer.getChildren().add(root);
        StackPane.setAlignment(root, Pos.CENTER);
        modalContainer.setOnMouseClicked(e -> { if (e.getTarget() == modalContainer) closeWithAnimation(modal, root); });

        // --- NEW: Top Right Close Button ---
        HBox closeBox = new HBox();
        closeBox.setAlignment(Pos.TOP_RIGHT);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #9CA3AF; -fx-padding: 0;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, root));
        closeBox.getChildren().add(closeBtn);

        // --- Top Section: Avatar & Name ---
        HBox topSection = new HBox(20);
        topSection.setAlignment(Pos.CENTER_LEFT);

        // Avatar Box (Using the ready-made PNG user icon)
        StackPane avatarBox = new StackPane();
        avatarBox.setPrefSize(80, 80);
        avatarBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16;");

        // Load the user-round icon from our factory!
        javafx.scene.image.ImageView personIcon = IconFactory.getIcon("user-round", 40);
        personIcon.setOpacity(0.5); // Soften it slightly to look like a placeholder

        // Active dot logic
        boolean isActive = "Active".equalsIgnoreCase(employee.getStatus());
        Circle statusDot = new Circle(8, isActive ? Color.web("#10B981") : Color.web("#9CA3AF"));
        statusDot.setStroke(Color.WHITE);
        statusDot.setStrokeWidth(3);
        StackPane.setAlignment(statusDot, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(statusDot, new Insets(0, -4, -4, 0));
        avatarBox.getChildren().addAll(personIcon, statusDot);

        // Name & Role
        VBox nameBox = new VBox(5);
        Label nameLbl = new Label(employee.getName() + " " + employee.getSurname());
        nameLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label roleLbl = new Label(employee.getPosition());
        roleLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY_DARK + ";");

        Label statusPill = new Label(employee.getStatus().toUpperCase());
        String pillBg, pillTxt;
        if ("Active".equalsIgnoreCase(employee.getStatus())) { pillBg = "#D1FAE5"; pillTxt = "#10B981"; }
        else if ("Contractor".equalsIgnoreCase(employee.getStatus())) { pillBg = "#FEF3C7"; pillTxt = "#D97706"; }
        else { pillBg = "#F1F5F9"; pillTxt = "#64748B"; }
        statusPill.setStyle("-fx-background-color: " + pillBg + "; -fx-text-fill: " + pillTxt + "; -fx-font-size: 10px; -fx-font-weight: 900; -fx-padding: 4 12; -fx-background-radius: 12;");

        nameBox.getChildren().addAll(nameLbl, roleLbl, statusPill);

        topSection.getChildren().addAll(avatarBox, nameBox);

        // --- Grid Details Section (Styled like beautiful disabled input fields) ---
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        // Setting columns to exactly 50% width so fields align perfectly
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(createDetailItem("NAME", employee.getName() + " " + employee.getSurname()), 0, 0);
        grid.add(createDetailItem("E-MAIL", employee.getEmail()), 1, 0);
        grid.add(createDetailItem("ROLE (INDUSTRY)", employee.getPosition()), 0, 1);
        grid.add(createDetailItem("DEPARTMENT", employee.getDepartment()), 1, 1);

        // --- Bottom Action Buttons ---
        HBox actionBox = new HBox(15);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        StateButton editBtn = new StateButton("Edit Profile", StateButton.ButtonType.PRIMARY);
        editBtn.setGraphic(IconFactory.getWhiteIcon("square-pen", 16)); // PNG Icon
        HBox.setHgrow(editBtn, Priority.ALWAYS);
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 12; -fx-background-radius: 10; -fx-cursor: hand; -fx-icon-text-gap: 8px;");

        Button delBtn = new Button();
        delBtn.setGraphic(IconFactory.getIcon("trash", 20)); // PNG Icon
        delBtn.setStyle("-fx-background-color: #FEE2E2; -fx-background-radius: 10; -fx-padding: 10 16; -fx-cursor: hand;");

        // Button Logic
        editBtn.setOnAction(e -> {
            modal.close();
            // Opens AddEmployeeDialog in EDIT mode
            AddEmployeeDialog.show(owner, employee, updatedEmp -> onUpdate.accept(updatedEmp));
        });

        delBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Employee");
            alert.setHeaderText("Remove " + employee.getName() + "?");
            alert.setContentText("Are you sure you want to delete this employee? This action cannot be undone.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                onDelete.accept(employee);
                closeWithAnimation(modal, root);
            }
        });

        actionBox.getChildren().addAll(editBtn, delBtn);

        // Add all elements to root
        root.getChildren().addAll(closeBox, topSection, grid, actionBox);

        Scene scene = new Scene(modalContainer);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        root.setOpacity(0);
        root.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), root);
        slideUp.setToY(0);

        new ParallelTransition(fadeIn, slideUp).play();

        modal.show();
    }

    // UPDATED: Styled to look like clean, modern "input fields"
    private static VBox createDetailItem(String labelTxt, String valueTxt) {
        VBox box = new VBox(5);
        // Gives the read-only data a beautiful input-field container appearance
        box.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 12 16; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        Label lbl = new Label(labelTxt);
        lbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        Label val = new Label(valueTxt == null || valueTxt.isEmpty() ? "N/A" : valueTxt);
        val.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        box.getChildren().addAll(lbl, val);
        return box;
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode);
        fadeOut.setToValue(0);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode);
        slideDown.setToY(30);

        ParallelTransition exit = new ParallelTransition(fadeOut, slideDown);
        exit.setOnFinished(e -> modal.close());
        exit.play();
    }
}