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
import org.example.model.database.entity.Client;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AddEditClientDialog {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public static void show(Stage owner, Client clientToEdit, Consumer<Client> onSuccess) {
        boolean isEditMode = (clientToEdit != null);

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
        root.setPrefWidth(480);

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
        Label title = new Label(isEditMode ? I18n.t("Edit Client") : I18n.t("Add Client"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label subtitle = new Label(isEditMode ? I18n.t("Update client information.") : I18n.t("Onboard a new client to the ecosystem."));
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subtitle);

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 0;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, headerSpacer, closeBtn);

        // ── Form fields ───────────────────────────────────────────────────────
        VBox form = new VBox(15);

        // First Name + Last Name row
        TextField nameField = UIFactory.inputField(I18n.t("First name"));
        nameField.setMinHeight(44); nameField.setPrefHeight(44);
        Label nameError = errorLabel();

        TextField surnameField = UIFactory.inputField(I18n.t("Last name"));
        surnameField.setMinHeight(44); surnameField.setPrefHeight(44);
        Label surnameError = errorLabel();

        GridPane splitName = new GridPane();
        splitName.setHgap(15);
        ColumnConstraints nc = new ColumnConstraints(); nc.setPercentWidth(50);
        ColumnConstraints sc = new ColumnConstraints(); sc.setPercentWidth(50);
        splitName.getColumnConstraints().addAll(nc, sc);
        splitName.add(createLabeledField(I18n.t("FIRST NAME"), nameField, nameError), 0, 0);
        splitName.add(createLabeledField(I18n.t("LAST NAME"), surnameField, surnameError), 1, 0);

        // Email + Phone row
        TextField emailField = UIFactory.inputField("email@example.com");
        emailField.setMinHeight(44); emailField.setPrefHeight(44);
        Label emailError = errorLabel();

        TextField phoneField = UIFactory.inputField("+1 (555) 000-0000");
        phoneField.setMinHeight(44); phoneField.setPrefHeight(44);
        Label phoneError = errorLabel();

        GridPane splitContact = new GridPane();
        splitContact.setHgap(15);
        ColumnConstraints ec = new ColumnConstraints(); ec.setPercentWidth(50);
        ColumnConstraints pc = new ColumnConstraints(); pc.setPercentWidth(50);
        splitContact.getColumnConstraints().addAll(ec, pc);
        splitContact.add(createLabeledField(I18n.t("EMAIL"), emailField, emailError), 0, 0);
        splitContact.add(createLabeledField(I18n.t("PHONE"), phoneField, phoneError), 1, 0);

        // Monthly Income
        TextField incomeField = UIFactory.inputField("0.00");
        incomeField.setMinHeight(44); incomeField.setPrefHeight(44);
        Label incomeError = errorLabel();

        form.getChildren().addAll(
                splitName,
                splitContact,
                createLabeledField(I18n.t("MONTHLY INCOME"), incomeField, incomeError)
        );

        // ── Pre-fill for edit mode ────────────────────────────────────────────
        if (isEditMode) {
            nameField.setText(clientToEdit.getName() != null ? clientToEdit.getName() : "");
            surnameField.setText(clientToEdit.getSurname() != null ? clientToEdit.getSurname() : "");
            emailField.setText(clientToEdit.getEmail() != null ? clientToEdit.getEmail() : "");
            phoneField.setText(clientToEdit.getPhone() != null ? clientToEdit.getPhone() : "");
            if (clientToEdit.getMonthlyIncome() != null)
                incomeField.setText(clientToEdit.getMonthlyIncome().toPlainString());
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

        StateButton saveBtn = new StateButton(isEditMode ? I18n.t("Update") : I18n.t("Add Client"), StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setMinHeight(50);
        HBox.setHgrow(saveBtn, Priority.ALWAYS);
        saveBtn.setOnMousePressed(e -> { saveBtn.setScaleX(0.98); saveBtn.setScaleY(0.98); });
        saveBtn.setOnMouseReleased(e -> { saveBtn.setScaleX(1.0); saveBtn.setScaleY(1.0); });

        // ── Capture original styles ───────────────────────────────────────────
        final String origNameStyle    = nameField.getStyle();
        final String origSurnameStyle = surnameField.getStyle();
        final String origEmailStyle   = emailField.getStyle();
        final String origPhoneStyle   = phoneField.getStyle();
        final String origIncomeStyle  = incomeField.getStyle();
        final String errorBorder      = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;";

        // ── Save action ───────────────────────────────────────────────────────
        saveBtn.setOnAction(e -> {

            // Clear all previous errors
            clearFieldError(nameField,    origNameStyle,    nameError);
            clearFieldError(surnameField, origSurnameStyle, surnameError);
            clearFieldError(emailField,   origEmailStyle,   emailError);
            clearFieldError(phoneField,   origPhoneStyle,   phoneError);
            clearFieldError(incomeField,  origIncomeStyle,  incomeError);

            boolean valid = true;

            // First name — required
            if (nameField.getText().trim().isEmpty()) {
                showFieldError(nameField, errorBorder, nameError, I18n.t("First name is required"));
                valid = false;
            }

            // Last name — required
            if (surnameField.getText().trim().isEmpty()) {
                showFieldError(surnameField, errorBorder, surnameError, I18n.t("Last name is required"));
                valid = false;
            }

            // Email — optional, must be valid if provided
            String emailText = emailField.getText().trim();
            if (!emailText.isEmpty() && !EMAIL_PATTERN.matcher(emailText).matches()) {
                showFieldError(emailField, errorBorder, emailError, I18n.t("Enter a valid email address"));
                valid = false;
            }

            // Monthly income — optional, must be a non-negative number if provided
            BigDecimal income = null;
            String incomeText = incomeField.getText().trim();
            if (!incomeText.isEmpty()) {
                try {
                    income = new BigDecimal(incomeText);
                    if (income.compareTo(BigDecimal.ZERO) < 0) {
                        showFieldError(incomeField, errorBorder, incomeError, I18n.t("Income cannot be negative"));
                        valid = false;
                    }
                } catch (NumberFormatException ex) {
                    showFieldError(incomeField, errorBorder, incomeError, I18n.t("Enter a valid number (e.g. 2500.00)"));
                    valid = false;
                }
            }

            if (!valid) {
                ToastManager.showError(owner, I18n.t("Please fix the highlighted fields."));
                return;
            }

            Client client = isEditMode ? clientToEdit : new Client();
            client.setName(nameField.getText().trim());
            client.setSurname(surnameField.getText().trim());
            client.setEmail(emailText.isEmpty() ? null : emailText);
            client.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
            client.setMonthlyIncome(income);

            if (!isEditMode) {
                client.setCompanyId(SessionManager.getInstance().getCurrentCompanyId());
            }

            onSuccess.accept(client);
            closeWithAnimation(modal, shadowWrapper);
        });

        actionBox.getChildren().addAll(cancelBtn, saveBtn);
        root.getChildren().addAll(header, form, actionBox);

        // ── Scene setup ───────────────────────────────────────────────────────
        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(AddEditClientDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            AppLog.getLogger(AddEditClientDialog.class).warn("Could not load global.css: {}", e.getMessage());
        }

        modal.setScene(scene);
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

    /** Three-arg — field with error label below. */
    private static VBox createLabeledField(String labelText, Node field, Label errorLabel) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");
        VBox box = new VBox(4, label, field, errorLabel);
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
