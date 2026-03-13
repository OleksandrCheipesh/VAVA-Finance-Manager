package org.example.view.templates;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.view.templates.StateButton; // Assuming you kept StateButton in components package or move to templates

// Note: Ensure your imports for StateButton, FormField, FilterBar, AppTable, and ToastManager are correct based on your package structure!

public class ComponentTestView extends BaseView {

    private ScrollPane scrollPane;
    private VBox root;

    @Override
    protected void setContent() {
        root = new VBox(30);
        root.setAlignment(Pos.TOP_LEFT);

        // 1. Title
        Label title = buildContentTitle("Component Library Sandbox");

        // 2. Buttons & Toasts
        HBox buttonBox = new HBox(15);
        StateButton primaryBtn = new StateButton("Show Success Toast", StateButton.ButtonType.PRIMARY);
        StateButton secondaryBtn = new StateButton("Toggle Loading", StateButton.ButtonType.SECONDARY);
        StateButton dangerBtn = new StateButton("Show Error Toast", StateButton.ButtonType.DANGER);
        buttonBox.getChildren().addAll(primaryBtn, secondaryBtn, dangerBtn);

        // Trigger the new Figma Toasts
        primaryBtn.setOnAction(e -> ToastManager.showSuccess(stage, "Success success success success success."));
        dangerBtn.setOnAction(e -> ToastManager.showError(stage, "Error error error error error error error."));

        // Test loading spinner
        secondaryBtn.setOnAction(e -> {
            boolean isCurrentlyLoading = secondaryBtn.getText().isEmpty();
            secondaryBtn.setLoading(!isCurrentlyLoading);
        });

        // 3. Form Fields (Updated for Figma labels!)
        VBox formBox = new VBox(15);
        formBox.setMaxWidth(400);

        FormField normalField = FormField.createTextField("Name", "Enter your name");

        FormField numericField = FormField.createNumericField("Salary", "0.00");

        FormField errorField = FormField.createTextField("Email", "email@company.com");
        errorField.setError("This email format is invalid.");

        FormField disabledField = FormField.createComboBox("Department", "Select Department");
        disabledField.setDisabledState(true);

        formBox.getChildren().addAll(normalField, numericField, errorField, disabledField);

        // 4. Filter Bar (Figma style)
        FilterBar filterBar = new FilterBar();

        // 5. Table (Figma empty state)
        AppTable<String> emptyTable = new AppTable<>("No data found. Try adjusting your filters!");
        emptyTable.setPrefHeight(200);

        // Assemble Screen
        root.getChildren().addAll(title,
                new Label("Buttons & Toasts"), buttonBox,
                new Label("Form Fields"), formBox,
                new Label("Filter Bar"), filterBar,
                new Label("Data Table"), emptyTable);

        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");

        scene = new Scene(scrollPane, 1000, 800);
    }

    @Override
    protected void setStyle() {
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        // Logic handled in button actions above
    }
}