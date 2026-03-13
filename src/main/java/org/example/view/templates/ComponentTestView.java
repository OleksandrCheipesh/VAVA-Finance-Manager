package org.example.view.templates;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.view.templates.*; // Assuming you put components in a components package. Change to templates if they are all in templates.

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

        // Logic for testing buttons and toasts
        primaryBtn.setOnAction(e -> ToastManager.showSuccess(stage, "Data saved successfully!"));
        dangerBtn.setOnAction(e -> ToastManager.showError(stage, "Failed to connect to database."));

        // Toggle loading state on the secondary button to test the spinner
        secondaryBtn.setOnAction(e -> {
            boolean isCurrentlyLoading = secondaryBtn.getText().isEmpty(); // It has no text when loading
            secondaryBtn.setLoading(!isCurrentlyLoading);
        });

        // 3. Form Fields
        VBox formBox = new VBox(15);
        formBox.setMaxWidth(400);

        FormField normalField = FormField.createTextField("Standard Text Input");

        FormField numericField = FormField.createNumericField("Numeric Only Input (try typing letters)");

        FormField errorField = FormField.createTextField("I have an error");
        errorField.setError("This is what an error state looks like!");

        FormField disabledField = FormField.createTextField("I am disabled");
        disabledField.setDisabledState(true);

        formBox.getChildren().addAll(normalField, numericField, errorField, disabledField);

        // 4. Filter Bar
        FilterBar filterBar = new FilterBar();

        // 5. Table (Empty State)
        AppTable<String> emptyTable = new AppTable<>("No data found. Try adjusting your filters!");
        emptyTable.setPrefHeight(200);

        // Add everything to the screen
        root.getChildren().addAll(title, new Label("Buttons & Toasts"), buttonBox,
                new Label("Form Fields"), formBox,
                new Label("Filter Bar"), filterBar,
                new Label("Data Table"), emptyTable);

        // Wrap in a ScrollPane in case your screen is small
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F4FAFA;");

        scene = new Scene(scrollPane, 1000, 800);
    }

    @Override
    protected void setStyle() {
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #F4FAFA;"); // Use your background theme color
    }

    @Override
    protected void setLogic() {
        // Logic was attached directly to the buttons in setContent for this test
    }
}