package org.example.view.templates;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.view.templates.StateButton;

public class ComponentTestView extends BaseView {

    private ScrollPane scrollPane;
    private VBox root;

    @Override
    protected void setContent() {
        root = new VBox(30);
        root.setAlignment(Pos.TOP_LEFT);

        // 1. Title
        Label title = buildContentTitle(I18n.t("Component Library Sandbox"));

        // 2. Buttons & Toasts
        HBox buttonBox = new HBox(15);
        StateButton primaryBtn = new StateButton(I18n.t("Show Success Toast"), StateButton.ButtonType.PRIMARY);
        StateButton secondaryBtn = new StateButton(I18n.t("Toggle Loading"), StateButton.ButtonType.SECONDARY);
        StateButton dangerBtn = new StateButton(I18n.t("Show Error Toast"), StateButton.ButtonType.DANGER);
        buttonBox.getChildren().addAll(primaryBtn, secondaryBtn, dangerBtn);

        primaryBtn.setOnAction(e -> ToastManager.showSuccess(stage, I18n.t("Success success success success success.")));
        dangerBtn.setOnAction(e -> ToastManager.showError(stage, I18n.t("Error error error error error error error.")));

        secondaryBtn.setOnAction(e -> {
            boolean isCurrentlyLoading = secondaryBtn.getText().isEmpty();
            secondaryBtn.setLoading(!isCurrentlyLoading);
        });

        // 3. Form Fields
        VBox formBox = new VBox(15);
        formBox.setMaxWidth(400);

        FormField normalField = FormField.createTextField(I18n.t("Name"), I18n.t("Enter your name"));

        FormField numericField = FormField.createNumericField(I18n.t("Salary"), "0.00");

        FormField errorField = FormField.createTextField(I18n.t("Email"), "email@company.com");
        errorField.setError(I18n.t("This email format is invalid."));

        FormField disabledField = FormField.createComboBox(I18n.t("Department"), I18n.t("Select Department"));
        disabledField.setDisabledState(true);

        formBox.getChildren().addAll(normalField, numericField, errorField, disabledField);

        // 4. Filter Bar
        FilterBar filterBar = new FilterBar();

        // 5. Table
        AppTable<String> emptyTable = new AppTable<>(I18n.t("No data found. Try adjusting your filters!"));
        emptyTable.setPrefHeight(200);

        // Assemble Screen
        root.getChildren().addAll(title,
                new Label(I18n.t("Buttons & Toasts")), buttonBox,
                new Label(I18n.t("Form Fields")), formBox,
                new Label(I18n.t("Filter Bar")), filterBar,
                new Label(I18n.t("Data Table")), emptyTable);

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