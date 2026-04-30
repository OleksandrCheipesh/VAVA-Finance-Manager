package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.SessionManager;
import org.example.logging.AppLog;
import org.example.model.database.entity.Project;
import org.example.model.validation.ProjValExept;
import org.example.model.validation.ProjectValidator;
import org.example.view.templates.StateButton;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Consumer;

import static org.example.model.validation.ProjValExept.ProjErrorCode.*;

public class AddProjectDialog {

    public static void show(Stage owner, Project projectToEdit, Consumer<Project> onSuccess) {
        boolean isEditMode = (projectToEdit != null);
        Stage modal = new Stage();

        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        Scene ownerScene = owner.getScene();
        Paint originalFill = ownerScene.getFill();
        ownerScene.setFill(Color.web(Themes.TEXT_DARK));

        Node backgroundRoot = ownerScene.getRoot();
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.3);

        GaussianBlur blur = new GaussianBlur(15);
        blur.setInput(darken);
        backgroundRoot.setEffect(blur);

        modal.setOnHidden(e -> {
            backgroundRoot.setEffect(null);
            ownerScene.setFill(originalFill);
        });

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 10);"
        );
        root.setPrefWidth(500);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        shadowWrapper.setOnMouseClicked(e -> {
            if (e.getTarget() == shadowWrapper) closeWithAnimation(modal, shadowWrapper);
        });

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);

        Label title = new Label(isEditMode ? I18n.t("Edit Project") : I18n.t("Add New Project"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label subtitle = new Label(isEditMode ? I18n.t("Update the project details below.") : I18n.t("Define the scope and resources for your next initiative."));
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subtitle);

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-text-fill: " + Themes.TEXT_MUTED + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 0;"
        );
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // Form Fields
        VBox form = new VBox(15);

        TextField nameField = UIFactory.inputField("e.g., Global Expansion phase 1");
        nameField.setMinHeight(44);
        nameField.setPrefHeight(44);

        VBox nameBox = createLabeledField(I18n.t("PROJECT NAME"), nameField);

        TextField descField = UIFactory.inputField("Describe the objectives and key outcomes...");
        descField.setMinHeight(44);
        descField.setPrefHeight(44);

        VBox descBox = createLabeledField(I18n.t("DESCRIPTION"), descField);

        TextField budgetField = UIFactory.inputField("$ 0.00");
        budgetField.setMinHeight(44);
        budgetField.setPrefHeight(44);

        VBox budgetBox = createLabeledField(I18n.t("BUDGET LIMIT"), budgetField);

        DatePicker startDateField = UIFactory.inputDatePicker("mm/dd/yyyy");
        startDateField.setMinHeight(44);
        startDateField.setPrefHeight(44);
        startDateField.setMaxWidth(Double.MAX_VALUE);

        VBox startBox = createLabeledField(I18n.t("START DATE"), startDateField);

        DatePicker endDateField = UIFactory.inputDatePicker("mm/dd/yyyy");
        endDateField.setMinHeight(44);
        endDateField.setPrefHeight(44);
        endDateField.setMaxWidth(Double.MAX_VALUE);

        VBox endBox = createLabeledField(I18n.t("END DATE"), endDateField);

        HBox splitDates = new HBox(15);
        HBox.setHgrow(startBox, Priority.ALWAYS);
        HBox.setHgrow(endBox, Priority.ALWAYS);

        splitDates.getChildren().addAll(startBox, endBox);

        form.getChildren().addAll(nameBox, descBox, budgetBox, splitDates);

        // Pre-fill fields when editing an existing project
        if (isEditMode) {
            nameField.setText(projectToEdit.getName() != null ? projectToEdit.getName() : "");
            descField.setText(projectToEdit.getDescription() != null ? projectToEdit.getDescription() : "");
            if (projectToEdit.getBudgetLimit() != null)
                budgetField.setText(projectToEdit.getBudgetLimit().toPlainString());
            startDateField.setValue(projectToEdit.getStartDate());
            endDateField.setValue(projectToEdit.getEndDate());
        }

        // Bottom Buttons
        HBox actionBox = new HBox(20);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button(I18n.t("Cancel"));
        cancelBtn.setMinWidth(Region.USE_PREF_SIZE);
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 10 20;");
        cancelBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        setupScaleEffect(cancelBtn, 0.98);

        StateButton saveBtn = new StateButton(isEditMode ? I18n.t("Save Changes") : I18n.t("Add New Project"), StateButton.ButtonType.PRIMARY);
        saveBtn.setMinHeight(50);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(saveBtn, Priority.ALWAYS);
        setupScaleEffect(saveBtn, 0.98);

        final String origNameStyle = nameField.getStyle();
        final String origDescStyle = descField.getStyle();
        final String origBudgetStyle = budgetField.getStyle();
        final String origStartStyle = startDateField.getStyle();
        final String origEndStyle = endDateField.getStyle();

        String errorBorder = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;";

        saveBtn.setOnAction(e -> {
            // Reset styles
            nameField.setStyle(origNameStyle);
            descField.setStyle(origDescStyle);
            budgetField.setStyle(origBudgetStyle);
            startDateField.setStyle(origStartStyle);
            endDateField.setStyle(origEndStyle);

            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(600); } catch (InterruptedException ex) {}
                Platform.runLater(() -> {
                    try {
                        String name = nameField.getText().trim();
                        String desc = descField.getText().trim();
                        String budgetStr = budgetField.getText().replaceAll("[$,]", "");

                        BigDecimal budget = budgetStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(budgetStr);

                        LocalDate start = startDateField.getValue() != null ? startDateField.getValue() : LocalDate.now();
                        LocalDate end = endDateField.getValue() != null ? endDateField.getValue() : LocalDate.now().plusMonths(3);

                        int companyId = isEditMode
                                ? projectToEdit.getCompanyId()
                                : SessionManager.getInstance().getCurrentCompanyId();
                        Project newProj = new Project(companyId, name, desc, budget, start, end, true);
                        if (isEditMode) newProj.setId(projectToEdit.getId());
                        try {
                            ProjectValidator.validate(newProj);
                        } catch (ProjValExept pe) {
                            saveBtn.setLoading(false);
                            switch (pe.getCode()) {
                                case NAME_EMPTY -> nameField.setStyle(nameField.getStyle() + errorBorder);
                                case BUDGET_NEGATIVE, SPEND_EXCEEDS_BUDGET -> budgetField.setStyle(budgetField.getStyle() + errorBorder);
                                case DATE_INVALID_RANGE -> {
                                    startDateField.setStyle(startDateField.getStyle() + errorBorder);
                                    endDateField.setStyle(endDateField.getStyle() + errorBorder);
                                }
                                default -> {}
                            }
                            ToastManager.showError(owner, pe.getMessage());
                            return;
                        }
                        onSuccess.accept(newProj);
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (NumberFormatException ex) {
                        saveBtn.setLoading(false);
                        budgetField.setStyle(budgetField.getStyle() + errorBorder);
                        ToastManager.showError(owner, I18n.t("Please enter a valid numeric budget."));
                    }
                });
            }).start();
        });

        actionBox.getChildren().addAll(cancelBtn, saveBtn);
        root.getChildren().addAll(header, form, actionBox);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(AddProjectDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception ignored) {}

        modal.setScene(scene);
        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper);
        slideUp.setToY(0);

        modal.show();

        new ParallelTransition(fadeIn, slideUp).play();
    }

    private static VBox createLabeledField(String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");

        VBox box = new VBox(5, label, field);
        box.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(box, Priority.ALWAYS);

        return box;
    }

    private static void setupScaleEffect(Node node, double scale) {
        node.setOnMousePressed(e -> { node.setScaleX(scale); node.setScaleY(scale); });
        node.setOnMouseReleased(e -> { node.setScaleX(1.0); node.setScaleY(1.0); });
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode);
        fadeOut.setToValue(0);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode);
        slideDown.setToY(30);

        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);
        exitAnimation.setOnFinished(e -> modal.close());

        exitAnimation.play();
    }
}