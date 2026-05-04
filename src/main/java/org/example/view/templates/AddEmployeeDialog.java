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
import org.example.logging.AppLog;
import org.example.model.database.entity.Employee;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AddEmployeeDialog {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public static void show(Stage owner, Employee employeeToEdit, Consumer<Employee> onSuccess) {
        boolean isEditMode = (employeeToEdit != null);

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
        root.setPrefWidth(450);

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
        Label title = new Label(isEditMode ? I18n.t("Edit Employee") : I18n.t("New Employee"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label subtitle = new Label(isEditMode ? I18n.t("Update member information.") : I18n.t("Add a new member to your team."));
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");

        titleBox.getChildren().addAll(title, subtitle);

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 0;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // ── Form fields ───────────────────────────────────────────────────────
        VBox form = new VBox(15);

        // Name + Surname row
        TextField nameField = UIFactory.inputField(I18n.t("Name"));
        nameField.setMinHeight(44);
        nameField.setPrefHeight(44);
        Label nameError = errorLabel();

        TextField surnameField = UIFactory.inputField(I18n.t("Surname"));
        surnameField.setMinHeight(44);
        surnameField.setPrefHeight(44);
        Label surnameError = errorLabel();

        GridPane splitName = new GridPane();
        splitName.setHgap(15);
        ColumnConstraints nameCol = new ColumnConstraints();
        nameCol.setPercentWidth(50);
        ColumnConstraints surnameCol = new ColumnConstraints();
        surnameCol.setPercentWidth(50);
        splitName.getColumnConstraints().addAll(nameCol, surnameCol);
        splitName.add(createLabeledField(I18n.t("NAME"), nameField, nameError), 0, 0);
        splitName.add(createLabeledField(I18n.t("SURNAME"), surnameField, surnameError), 1, 0);

        // Email
        TextField emailField = UIFactory.inputField(I18n.t("E-mail"));
        emailField.setMinHeight(44);
        emailField.setPrefHeight(44);
        Label emailError = errorLabel();
        VBox emailBox = createLabeledField(I18n.t("E-MAIL"), emailField, emailError);

        // Role / Position
        TextField roleField = UIFactory.inputField(I18n.t("Role"));
        roleField.setMinHeight(44);
        roleField.setPrefHeight(44);
        Label roleError = errorLabel();
        VBox roleBox = createLabeledField(I18n.t("ROLE"), roleField, roleError);

        // Salary
        TextField salaryField = UIFactory.inputField("0.00");
        salaryField.setMinHeight(44);
        salaryField.setPrefHeight(44);
        Label salaryError = errorLabel();
        VBox salaryBox = createLabeledField(I18n.t("SALARY"), salaryField, salaryError);

        // Department + Status row
        TextField deptField = UIFactory.inputField(I18n.t("Department"));
        deptField.setMinHeight(44);
        deptField.setPrefHeight(44);

        ComboBox<String> statusCombo = UIFactory.inputComboBox(I18n.t("Active"));
        statusCombo.getItems().addAll(I18n.t("Active"), I18n.t("Inactive"), I18n.t("Contractor"));
        statusCombo.setValue(I18n.t("Active"));
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.setPrefWidth(0);
        statusCombo.setMinHeight(44);
        statusCombo.setPrefHeight(44);
        statusCombo.setMaxHeight(44);

        GridPane splitBottom = new GridPane();
        splitBottom.setHgap(15);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(65);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(35);
        splitBottom.getColumnConstraints().addAll(col1, col2);
        splitBottom.add(createLabeledField(I18n.t("DEPARTMENT"), deptField), 0, 0);
        splitBottom.add(createLabeledField(I18n.t("STATUS"), statusCombo), 1, 0);

        form.getChildren().addAll(splitName, emailBox, roleBox, salaryBox, splitBottom);

        // ── Pre-fill for edit mode ────────────────────────────────────────────
        if (isEditMode) {
            nameField.setText(employeeToEdit.getName());
            surnameField.setText(employeeToEdit.getSurname());
            emailField.setText(employeeToEdit.getEmail());
            roleField.setText(employeeToEdit.getPosition());
            deptField.setText(employeeToEdit.getDepartment());
            statusCombo.setValue(employeeToEdit.getStatus());
            if (employeeToEdit.getSalary() != null) {
                salaryField.setText(employeeToEdit.getSalary().toPlainString());
            }
        }

        // ── Action buttons ────────────────────────────────────────────────────
        HBox actionBox = new HBox(20);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button(I18n.t("Cancel"));
        cancelBtn.setMinWidth(Region.USE_PREF_SIZE);
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 10 20;");
        cancelBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));
        cancelBtn.setOnMousePressed(e -> { cancelBtn.setScaleX(0.98); cancelBtn.setScaleY(0.98); });
        cancelBtn.setOnMouseReleased(e -> { cancelBtn.setScaleX(1.0); cancelBtn.setScaleY(1.0); });

        StateButton saveBtn = new StateButton(isEditMode ? I18n.t("Update") : I18n.t("Save"), StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setMinHeight(50);
        HBox.setHgrow(saveBtn, Priority.ALWAYS);
        saveBtn.setOnMousePressed(e -> { saveBtn.setScaleX(0.98); saveBtn.setScaleY(0.98); });
        saveBtn.setOnMouseReleased(e -> { saveBtn.setScaleX(1.0); saveBtn.setScaleY(1.0); });

        // ── Capture original styles ───────────────────────────────────────────
        final String origNameStyle    = nameField.getStyle();
        final String origSurnameStyle = surnameField.getStyle();
        final String origEmailStyle   = emailField.getStyle();
        final String origRoleStyle    = roleField.getStyle();
        final String origSalaryStyle  = salaryField.getStyle();
        final String errorBorder      = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;";

        // ── Save action ───────────────────────────────────────────────────────
        saveBtn.setOnAction(e -> {

            // Clear all previous errors
            clearFieldError(nameField,    origNameStyle,    nameError);
            clearFieldError(surnameField, origSurnameStyle, surnameError);
            clearFieldError(emailField,   origEmailStyle,   emailError);
            clearFieldError(roleField,    origRoleStyle,    roleError);
            clearFieldError(salaryField,  origSalaryStyle,  salaryError);

            boolean valid = true;

            // Name — required
            if (nameField.getText().trim().isEmpty()) {
                showFieldError(nameField, errorBorder, nameError, I18n.t("Name is required"));
                valid = false;
            }

            // Surname — required
            if (surnameField.getText().trim().isEmpty()) {
                showFieldError(surnameField, errorBorder, surnameError, I18n.t("Surname is required"));
                valid = false;
            }

            // Email — optional, but must be valid if provided
            String emailText = emailField.getText().trim();
            if (!emailText.isEmpty() && !EMAIL_PATTERN.matcher(emailText).matches()) {
                showFieldError(emailField, errorBorder, emailError, I18n.t("Enter a valid email address"));
                valid = false;
            }

            // Role / Position — required
            if (roleField.getText().trim().isEmpty()) {
                showFieldError(roleField, errorBorder, roleError, I18n.t("Role is required"));
                valid = false;
            }

            // Salary — optional, but must be a non-negative number if provided
            BigDecimal salary = null;
            String salaryText = salaryField.getText().trim();
            if (!salaryText.isEmpty()) {
                try {
                    salary = new BigDecimal(salaryText);
                    if (salary.compareTo(BigDecimal.ZERO) < 0) {
                        showFieldError(salaryField, errorBorder, salaryError, I18n.t("Salary cannot be negative"));
                        valid = false;
                    }
                } catch (NumberFormatException ex) {
                    showFieldError(salaryField, errorBorder, salaryError, I18n.t("Enter a valid number (e.g. 2500.00)"));
                    valid = false;
                }
            }

            if (!valid) {
                ToastManager.showError(owner, I18n.t("Please fix the highlighted fields."));
                return;
            }

            final BigDecimal finalSalary = salary;
            saveBtn.setLoading(true);

            new Thread(() -> {
                try { Thread.sleep(600); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                javafx.application.Platform.runLater(() -> {
                    try {
                        Employee emp = isEditMode ? employeeToEdit : new Employee();

                        if (!isEditMode) emp.setCompanyId(SessionManager.getInstance().getCurrentCompanyId());

                        emp.setName(nameField.getText().trim());
                        emp.setSurname(surnameField.getText().trim());
                        emp.setEmail(emailField.getText().trim());
                        emp.setPosition(roleField.getText().trim());
                        emp.setDepartment(deptField.getText().trim());
                        emp.setStatus(statusCombo.getValue());
                        emp.setSalary(finalSalary);

                        onSuccess.accept(emp);
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (Exception ex) {
                        saveBtn.setLoading(false);
                        ToastManager.showError(owner, I18n.t("Something went wrong. Please try again."));
                        AppLog.getLogger(AddEmployeeDialog.class).error("Error saving employee: {}", ex.getMessage(), ex);
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
            scene.getStylesheets().add(AddEmployeeDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            AppLog.getLogger(AddEmployeeDialog.class).warn("Could not load global.css: {}", e.getMessage());
        }

        modal.setScene(scene);

        // ── Entrance animation ────────────────────────────────────────────────
        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper);
        slideUp.setToY(0);

        new ParallelTransition(fadeIn, slideUp).play();
        modal.show();
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
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private static void clearFieldError(Node field, String origStyle, Label errorLabel) {
        field.setStyle(origStyle);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /** Three-arg version — field with an error label below it. */
    private static VBox createLabeledField(String labelText, Node field, Label errorLabel) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");

        VBox box = new VBox(4, label, field, errorLabel);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    /** Two-arg version — for fields that need no error label (dept, status). */
    private static VBox createLabeledField(String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");

        VBox box = new VBox(5, label, field);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
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
