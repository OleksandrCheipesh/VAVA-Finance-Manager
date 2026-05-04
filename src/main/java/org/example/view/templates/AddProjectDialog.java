package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
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
import org.example.model.database.entity.Project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Consumer;

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

        // ── Header ────────────────────────────────────────────────────────────
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
                "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;" +
                "-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 0;"
        );
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // ── Form fields ───────────────────────────────────────────────────────
        VBox form = new VBox(15);

        // Project name
        TextField nameField = UIFactory.inputField(I18n.t("e.g., Global Expansion phase 1"));
        nameField.setMinHeight(44); nameField.setPrefHeight(44);
        Label nameError = errorLabel();

        // Description — optional, no validation
        TextField descField = UIFactory.inputField(I18n.t("Describe the objectives and key outcomes..."));
        descField.setMinHeight(44); descField.setPrefHeight(44);

        // Budget
        TextField budgetField = UIFactory.inputField(CurrencyFormatter.symbol() + " 0.00");
        budgetField.setMinHeight(44); budgetField.setPrefHeight(44);
        Label budgetError = errorLabel();

        // Start date + End date in GridPane so each gets its own error label
        DatePicker startDateField = UIFactory.inputDatePicker("mm/dd/yyyy");
        startDateField.setMinHeight(44); startDateField.setPrefHeight(44);
        startDateField.setMaxWidth(Double.MAX_VALUE);
        Label startError = errorLabel();

        DatePicker endDateField = UIFactory.inputDatePicker("mm/dd/yyyy");
        endDateField.setMinHeight(44); endDateField.setPrefHeight(44);
        endDateField.setMaxWidth(Double.MAX_VALUE);
        Label endError = errorLabel();

        GridPane splitDates = new GridPane();
        splitDates.setHgap(15);
        ColumnConstraints dc1 = new ColumnConstraints(); dc1.setPercentWidth(50);
        ColumnConstraints dc2 = new ColumnConstraints(); dc2.setPercentWidth(50);
        splitDates.getColumnConstraints().addAll(dc1, dc2);
        splitDates.add(createLabeledField(I18n.t("START DATE"), startDateField, startError), 0, 0);
        splitDates.add(createLabeledField(I18n.t("END DATE"),   endDateField,   endError),   1, 0);

        form.getChildren().addAll(
                createLabeledField(I18n.t("PROJECT NAME"), nameField, nameError),
                createLabeledField(I18n.t("DESCRIPTION"),  descField),
                createLabeledField(I18n.t("BUDGET LIMIT"), budgetField, budgetError),
                splitDates
        );

        // ── Pre-fill for edit mode ────────────────────────────────────────────
        if (isEditMode) {
            nameField.setText(projectToEdit.getName() != null ? projectToEdit.getName() : "");
            descField.setText(projectToEdit.getDescription() != null ? projectToEdit.getDescription() : "");
            if (projectToEdit.getBudgetLimit() != null)
                budgetField.setText(projectToEdit.getBudgetLimit().toPlainString());
            startDateField.setValue(projectToEdit.getStartDate());
            endDateField.setValue(projectToEdit.getEndDate());
        }

        // ── Action buttons ────────────────────────────────────────────────────
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

        // ── Capture original styles ───────────────────────────────────────────
        final String origNameStyle   = nameField.getStyle();
        final String origBudgetStyle = budgetField.getStyle();
        final String origStartStyle  = startDateField.getStyle();
        final String origEndStyle    = endDateField.getStyle();
        final String errorBorder     = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;";

        // ── Save action ───────────────────────────────────────────────────────
        saveBtn.setOnAction(e -> {

            // Clear all previous errors
            clearFieldError(nameField,      origNameStyle,   nameError);
            clearFieldError(budgetField,    origBudgetStyle, budgetError);
            clearFieldError(startDateField, origStartStyle,  startError);
            clearFieldError(endDateField,   origEndStyle,    endError);

            boolean valid = true;

            // Name — required
            if (nameField.getText().trim().isEmpty()) {
                showFieldError(nameField, errorBorder, nameError, I18n.t("Project name is required"));
                valid = false;
            }

            // Budget — optional, must be valid non-negative number if filled
            String budgetStr = budgetField.getText()
                    .replaceAll("[" + CurrencyFormatter.symbol() + "$,]", "").trim();
            BigDecimal budget = null;
            if (!budgetStr.isEmpty()) {
                try {
                    budget = new BigDecimal(budgetStr);
                    if (budget.compareTo(BigDecimal.ZERO) < 0) {
                        showFieldError(budgetField, errorBorder, budgetError, I18n.t("Budget cannot be negative"));
                        valid = false;
                    }
                } catch (NumberFormatException ex) {
                    showFieldError(budgetField, errorBorder, budgetError, I18n.t("Enter a valid number (e.g. 5000.00)"));
                    valid = false;
                }
            }

            // Dates — if both are set, start must not be after end
            LocalDate start = startDateField.getValue();
            LocalDate end   = endDateField.getValue();
            if (start != null && end != null && start.isAfter(end)) {
                showFieldError(startDateField, errorBorder, startError, I18n.t("Start date must be before end date"));
                showFieldError(endDateField,   errorBorder, endError,   "");
                valid = false;
            }

            if (!valid) {
                ToastManager.showError(owner, I18n.t("Please fix the highlighted fields."));
                return;
            }

            final BigDecimal finalBudget = budget;
            saveBtn.setLoading(true);

            new Thread(() -> {
                try { Thread.sleep(600); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                javafx.application.Platform.runLater(() -> {
                    try {
                        int companyId = isEditMode
                                ? projectToEdit.getCompanyId()
                                : SessionManager.getInstance().getCurrentCompanyId();

                        Project proj = new Project(
                                companyId,
                                nameField.getText().trim(),
                                descField.getText().trim(),
                                finalBudget != null ? finalBudget : BigDecimal.ZERO,
                                start != null ? start : LocalDate.now(),
                                end   != null ? end   : LocalDate.now().plusMonths(3),
                                true
                        );
                        if (isEditMode) proj.setId(projectToEdit.getId());

                        onSuccess.accept(proj);
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (Exception ex) {
                        saveBtn.setLoading(false);
                        ToastManager.showError(owner, I18n.t("Something went wrong. Please try again."));
                    }
                });
            }).start();
        });

        actionBox.getChildren().addAll(cancelBtn, saveBtn);
        root.getChildren().addAll(header, form, actionBox);

        // ── Scene setup ───────────────────────────────────────────────────────
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Label errorLabel() {
        Label lbl = new Label();
        lbl.setStyle("-fx-text-fill: " + Themes.TEXT_ERROR + "; -fx-font-size: 11px;");
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    private static void showFieldError(Node field, String errorBorder, Label errorLabel, String message) {
        field.setStyle(field.getStyle() + errorBorder);
        if (!message.isEmpty()) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private static void clearFieldError(Node field, String origStyle, Label errorLabel) {
        field.setStyle(origStyle);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /** Three-arg — field with error label below. */
    private static VBox createLabeledField(String labelText, Node field, Label errorLabel) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");
        VBox box = new VBox(4, label, field, errorLabel);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    /** Two-arg — for fields that need no error label (description). */
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
        ParallelTransition exit = new ParallelTransition(fadeOut, slideDown);
        exit.setOnFinished(e -> modal.close());
        exit.play();
    }
}
