package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
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
import org.example.model.database.entity.Account;
import org.example.model.database.entity.Project;
import org.example.logging.AppLog;
import org.example.model.database.entity.Transaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EditTransactionDialog {

    private static String selectedType = "SALE";

    public static void show(Stage owner, Transaction existingTx, ObservableList<Account> accounts, ObservableList<Project> projects, Consumer<Transaction> onSuccess) {
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

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Edit Transaction");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: " + Themes.BG_FIELD + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-text-fill: " + Themes.TEXT_MUTED + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 0;"
        );

        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, closeBtn);

        HBox toggleBox = new HBox();
        toggleBox.setStyle("-fx-background-color: " + Themes.BG_FIELD + "; -fx-background-radius: 8; -fx-padding: 4;");
        Button saleBtn = new Button("+ Sale");
        Button purchaseBtn = new Button("- Purchase");

        if (existingTx != null && existingTx.getType() != null) {
            selectedType = existingTx.getType().toUpperCase();
        } else {
            selectedType = "SALE";
        }

        String activeStyle = "-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: default;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;";

        saleBtn.setMaxWidth(Double.MAX_VALUE);
        purchaseBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(saleBtn, Priority.ALWAYS);
        HBox.setHgrow(purchaseBtn, Priority.ALWAYS);

        saleBtn.setStyle(selectedType.equals("SALE") ? activeStyle : inactiveStyle);
        purchaseBtn.setStyle(selectedType.equals("PURCHASE") ? activeStyle : inactiveStyle);

        saleBtn.setOnAction(e -> {
            selectedType = "SALE";
            saleBtn.setStyle(activeStyle);
            purchaseBtn.setStyle(inactiveStyle);
        });
        purchaseBtn.setOnAction(e -> {
            selectedType = "PURCHASE";
            purchaseBtn.setStyle(activeStyle);
            saleBtn.setStyle(inactiveStyle);
        });
        toggleBox.getChildren().addAll(saleBtn, purchaseBtn);

        // Fields
        String initialAmount = existingTx != null && existingTx.getAmount() != null ? existingTx.getAmount().toString() : "";
        VBox form = new VBox(15);
        TextField amountField = UIFactory.inputField("0.00");
        if (!initialAmount.isEmpty()) amountField.setText(initialAmount);
        Label amountError = errorLabel();
        VBox amountBox = createLabeledField("Amount", amountField, amountError);

        String initialDesc = existingTx != null && existingTx.getDescription() != null ? existingTx.getDescription() : "";
        TextField descField = UIFactory.inputField("Enter description");
        if (!initialDesc.isEmpty()) descField.setText(initialDesc);
        Label descError = errorLabel();
        VBox descBox = createLabeledField("Description", descField, descError);

        // Account selector — pre-select account matching existingTx.accountId
        boolean hasAccounts = !accounts.isEmpty();
        String accountPrompt = hasAccounts ? "Select Account" : "No accounts found";
        ComboBox<Account> accountCombo = UIFactory.inputComboBox(accountPrompt);
        accountCombo.setMaxWidth(Double.MAX_VALUE);
        accountCombo.setItems(accounts);
        accountCombo.setDisable(!hasAccounts);
        accountCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAccountName());
            }
        });
        accountCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getAccountName());
                    setStyle("-fx-text-fill: " + Themes.TEXT_DARK + ";");
                } else {
                    setText(accountPrompt);
                    setStyle("-fx-text-fill: " + Themes.TEXT_DARK + ";");
                }
            }
        });

        if (existingTx != null) {
            for (Account a : accounts) {
                if (a.getId() == existingTx.getAccountId()) {
                    accountCombo.setValue(a);
                    break;
                }
            }
        }
        Label accountError = errorLabel();
        VBox accountBox = createLabeledField("Account", accountCombo, accountError);

        Map<String, Integer> projectNameToId = new HashMap<>();
        ComboBox<String> projectCombo = UIFactory.inputComboBox("Select Project");
        String initialProject = null;
        for (Project p : projects) {
            projectCombo.getItems().add(p.getName());
            projectNameToId.put(p.getName(), p.getId());
            if (existingTx != null && existingTx.getProjectId() != null && p.getId() == existingTx.getProjectId()) {
                initialProject = p.getName();
            }
        }
        if (initialProject != null) {
            projectCombo.setValue(initialProject);
        }
        Label projectError = errorLabel();
        VBox projectBox = createLabeledField("Project", projectCombo, projectError);

        HBox splitBox = new HBox(15);
        HBox.setHgrow(accountBox, Priority.ALWAYS);
        HBox.setHgrow(projectBox, Priority.ALWAYS);
        splitBox.getChildren().addAll(accountBox, projectBox);

        DatePicker datePicker = UIFactory.inputDatePicker("Select date");
        java.time.LocalDate initialDate = existingTx != null && existingTx.getDate() != null ? existingTx.getDate() : java.time.LocalDate.now();
        datePicker.setValue(initialDate);

        datePicker.getEditor().focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                java.time.LocalDate val = datePicker.getValue();
                datePicker.getEditor().setText(
                        val != null ? datePicker.getConverter().toString(val) : ""
                );
            }
        });

        Label dateError = errorLabel();
        VBox dateBox = createLabeledField("Date", datePicker, dateError);

        form.getChildren().addAll(amountBox, descBox, splitBox, dateBox);

        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        final String origAmountStyle = amountField.getStyle();
        final String origDescStyle = descField.getStyle();
        final String origDateStyle = datePicker.getStyle();
        final String origAccountStyle = accountCombo.getStyle();

        String errorBorder = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;";

        saveBtn.setOnAction(e -> {
            // Clear all previous errors
            clearFieldError(amountField, origAmountStyle, amountError);
            clearFieldError(descField, origDescStyle, descError);
            clearFieldError(datePicker, origDateStyle, dateError);
            clearFieldError(accountCombo, origAccountStyle, accountError);

            boolean valid = true;

            // Validate amount — parse immediately, not in background thread
            BigDecimal amount = null;
            String amountText = amountField.getText().trim();
            if (amountText.isBlank()) {
                showFieldError(amountField, errorBorder, amountError, "Amount is required");
                valid = false;
            } else {
                try {
                    amount = new BigDecimal(amountText);
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        showFieldError(amountField, errorBorder, amountError, "Amount must be greater than zero");
                        valid = false;
                    }
                } catch (NumberFormatException ex) {
                    showFieldError(amountField, errorBorder, amountError, "Enter a valid number (e.g. 1500.00)");
                    valid = false;
                }
            }

            if (descField.getText().isBlank()) {
                showFieldError(descField, errorBorder, descError, "Description is required");
                valid = false;
            }

            if (datePicker.getValue() == null) {
                showFieldError(datePicker, errorBorder, dateError, "Please select a date");
                valid = false;
            }

            if (accountCombo.getValue() == null) {
                showFieldError(accountCombo, errorBorder, accountError,
                        accounts.isEmpty() ? "No accounts available — create one first" : "Please select an account");
                valid = false;
            }

            if (!valid) {
                ToastManager.showError(owner, "Please fix the highlighted fields.");
                return;
            }

            final BigDecimal finalAmount = amount;
            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                javafx.application.Platform.runLater(() -> {
                    try {
                        String desc = descField.getText().trim();
                        java.time.LocalDate date = datePicker.getValue();
                        Integer clientId = null;
                        Integer projectId = projectNameToId.get(projectCombo.getValue());
                        int accountId = accountCombo.getValue().getId();

                        int txId = existingTx != null ? existingTx.getId() : 0;
                        int txCompanyId = existingTx != null ? existingTx.getCompanyId() : 0;
                        Transaction newTx = new Transaction(txCompanyId, accountId, projectId, clientId, selectedType, finalAmount, desc, date);
                        newTx.setId(txId);

                        onSuccess.accept(newTx);
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (Exception ex) {
                        saveBtn.setLoading(false);
                        ToastManager.showError(owner, "Something went wrong. Please try again.");
                        var logger = AppLog.getLogger(EditTransactionDialog.class);
                        logger.error("Unexpected error saving transaction: {}", ex.getMessage(), ex);
                    }
                });
            }).start();
        });

        root.getChildren().addAll(header, toggleBox, form, saveBtn);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(EditTransactionDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            var logger = AppLog.getLogger(EditTransactionDialog.class);
            logger.warn("Could not load global.css for EditTransactionDialog: {}", e.getMessage());
        }

        modal.setScene(scene);

        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper);
        slideUp.setToY(0);

        ParallelTransition entranceAnimation = new ParallelTransition(fadeIn, slideUp);

        modal.show();
        entranceAnimation.play();
    }

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

        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);
        exitAnimation.setOnFinished(e -> modal.close());
        exitAnimation.play();
    }
}
