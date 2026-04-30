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
import org.example.model.validation.ClientValidator;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class AddEditClientDialog {

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

        VBox form = new VBox(15);

        TextField nameField = UIFactory.inputField("First name");
        nameField.setMinHeight(44);
        nameField.setPrefHeight(44);

        TextField surnameField = UIFactory.inputField("Last name");
        surnameField.setMinHeight(44);
        surnameField.setPrefHeight(44);

        HBox splitName = new HBox(15);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        HBox.setHgrow(surnameField, Priority.ALWAYS);
        splitName.getChildren().addAll(
                createLabeledField(I18n.t("FIRST NAME"), nameField),
                createLabeledField(I18n.t("LAST NAME"), surnameField)
        );

        TextField emailField = UIFactory.inputField("email@example.com");
        emailField.setMinHeight(44);
        emailField.setPrefHeight(44);

        TextField phoneField = UIFactory.inputField("+1 (555) 000-0000");
        phoneField.setMinHeight(44);
        phoneField.setPrefHeight(44);

        HBox splitContact = new HBox(15);
        HBox.setHgrow(emailField, Priority.ALWAYS);
        HBox.setHgrow(phoneField, Priority.ALWAYS);
        splitContact.getChildren().addAll(
                createLabeledField(I18n.t("EMAIL"), emailField),
                createLabeledField(I18n.t("PHONE"), phoneField)
        );

        TextField incomeField = UIFactory.inputField("0.00");
        incomeField.setMinHeight(44);
        incomeField.setPrefHeight(44);

        form.getChildren().addAll(splitName, splitContact, createLabeledField(I18n.t("MONTHLY INCOME"), incomeField));

        if (isEditMode) {
            nameField.setText(clientToEdit.getName() != null ? clientToEdit.getName() : "");
            surnameField.setText(clientToEdit.getSurname() != null ? clientToEdit.getSurname() : "");
            emailField.setText(clientToEdit.getEmail() != null ? clientToEdit.getEmail() : "");
            phoneField.setText(clientToEdit.getPhone() != null ? clientToEdit.getPhone() : "");
            if (clientToEdit.getMonthlyIncome() != null)
                incomeField.setText(clientToEdit.getMonthlyIncome().toPlainString());
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: " + Themes.TEXT_ERROR + "; -fx-font-size: 13px; -fx-wrap-text: true;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

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

        saveBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            Client client = isEditMode ? clientToEdit : new Client();
            client.setName(nameField.getText().trim());
            client.setSurname(surnameField.getText().trim());
            client.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
            client.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());

            String incomeText = incomeField.getText().trim();
            if (incomeText.isEmpty()) {
                client.setMonthlyIncome(null);
            } else {
                try {
                    client.setMonthlyIncome(new BigDecimal(incomeText));
                } catch (NumberFormatException ex) {
                    showInlineError(errorLabel, I18n.t("Monthly income must be a valid number."));
                    return;
                }
            }

            try {
                ClientValidator.validate(client);
            } catch (IllegalArgumentException ex) {
                showInlineError(errorLabel, ex.getMessage());
                return;
            }

            if (!isEditMode) {
                client.setCompanyId(SessionManager.getInstance().getCurrentCompanyId());
            }

            onSuccess.accept(client);
            closeWithAnimation(modal, shadowWrapper);
        });

        actionBox.getChildren().addAll(cancelBtn, saveBtn);
        root.getChildren().addAll(header, form, errorLabel, actionBox);

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

    private static void showInlineError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

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