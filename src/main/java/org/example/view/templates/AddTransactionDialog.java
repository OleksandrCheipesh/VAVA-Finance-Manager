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

public class AddTransactionDialog {

    private static String selectedType = "SALE";

    public static void show(Stage owner, ObservableList<Account> accounts, ObservableList<Project> projects, Consumer<Transaction> onSuccess) {
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

        VBox titleBox = new VBox(4);
        Label title = new Label("New Transaction");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label subtitle = new Label("Record a new sale or purchase.");
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

        HBox toggleBox = new HBox();
        toggleBox.setStyle("-fx-background-color: " + Themes.BG_FIELD + "; -fx-background-radius: 8; -fx-padding: 4;");

        Button saleBtn = new Button("+ Sale");
        Button purchaseBtn = new Button("- Purchase");

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

        VBox form = new VBox(15);
        TextField amountField = UIFactory.inputField("0.00");

        amountField.setMinHeight(44);
        amountField.setPrefHeight(44);

        VBox amountBox = createLabeledField("AMOUNT", amountField);

        TextField descField = UIFactory.inputField("Enter description");

        descField.setMinHeight(44);
        descField.setPrefHeight(44);

        VBox descBox = createLabeledField("DESCRIPTION", descField);

        boolean hasAccounts = !accounts.isEmpty();
        String accountPrompt = hasAccounts ? "Select Account" : "No accounts found";

        ComboBox<Account> accountCombo = UIFactory.inputComboBox(accountPrompt);

        accountCombo.setMaxWidth(Double.MAX_VALUE);
        accountCombo.setMinHeight(44);
        accountCombo.setPrefHeight(44);
        accountCombo.setMaxHeight(44);

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

        Map<String, Integer> projectNameToId = new HashMap<>();

        ComboBox<String> projectCombo = UIFactory.inputComboBox("Select Project");

        projectCombo.setMaxWidth(Double.MAX_VALUE);
        projectCombo.setMinHeight(44);
        projectCombo.setPrefHeight(44);
        projectCombo.setMaxHeight(44);

        for (Project p : projects) {
            projectCombo.getItems().add(p.getName());
            projectNameToId.put(p.getName(), p.getId());
        }

        GridPane splitBox = new GridPane();
        splitBox.setHgap(15);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        splitBox.getColumnConstraints().addAll(col1, col2);

        splitBox.add(createLabeledField("ACCOUNT", accountCombo), 0, 0);
        splitBox.add(createLabeledField("PROJECT", projectCombo), 1, 0);

        DatePicker datePicker = UIFactory.inputDatePicker("Select date");

        datePicker.setValue(java.time.LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setMinHeight(44);
        datePicker.setPrefHeight(44);
        datePicker.setMaxHeight(44);

        datePicker.getEditor().focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                java.time.LocalDate val = datePicker.getValue();
                datePicker.getEditor().setText(
                        val != null ? datePicker.getConverter().toString(val) : ""
                );
            }
        });

        VBox dateBox = createLabeledField("DATE", datePicker);

        form.getChildren().addAll(amountBox, descBox, splitBox, dateBox);

        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        saveBtn.setMinHeight(50);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        final String origAmountStyle = amountField.getStyle();
        final String origDescStyle = descField.getStyle();
        final String origDateStyle = datePicker.getStyle();
        final String origAccountStyle = accountCombo.getStyle();

        String errorBorder = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 6;";

        saveBtn.setOnAction(e -> {
            amountField.setStyle(origAmountStyle);
            descField.setStyle(origDescStyle);
            datePicker.setStyle(origDateStyle);
            accountCombo.setStyle(origAccountStyle);

            boolean valid = true;
            if (amountField.getText().isBlank()) {
                amountField.setStyle(amountField.getStyle() + errorBorder);
                valid = false;
            }
            if (descField.getText().isBlank()) {
                descField.setStyle(descField.getStyle() + errorBorder);
                valid = false;
            }
            if (datePicker.getValue() == null) {
                datePicker.setStyle(datePicker.getStyle() + errorBorder);
                valid = false;
            }
            if (accountCombo.getValue() == null) {
                accountCombo.setStyle(accountCombo.getStyle() + errorBorder);
                valid = false;
            }
            if (!valid) {
                ToastManager.showError(owner, accounts.isEmpty()
                        ? "No accounts found. Please create an account first."
                        : "Please fill in all required fields correctly.");
                return;
            }

            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                javafx.application.Platform.runLater(() -> {
                    try {
                        BigDecimal amount = new BigDecimal(amountField.getText().trim());
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            saveBtn.setLoading(false);
                            amountField.setStyle(amountField.getStyle() + errorBorder);
                            return;
                        }

                        String desc = descField.getText().trim();
                        java.time.LocalDate date = datePicker.getValue();
                        Integer clientId = null;
                        Integer projectId = projectNameToId.get(projectCombo.getValue());
                        int accountId = accountCombo.getValue().getId();

                        Transaction newTx = new Transaction(0, accountId, projectId, clientId, selectedType, amount, desc, date);

                        onSuccess.accept(newTx);
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (NumberFormatException ex) {
                        saveBtn.setLoading(false);
                        amountField.setStyle(amountField.getStyle() + errorBorder);
                    } catch (Exception ex) {
                        saveBtn.setLoading(false);
                        var logger = AppLog.getLogger(AddTransactionDialog.class);
                        logger.error("Unexpected error saving transaction: {}", ex.getMessage(), ex);
                    }
                });
            }).start();
        });

        root.getChildren().addAll(header, toggleBox, form, saveBtn);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(AddTransactionDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            var logger = AppLog.getLogger(AddTransactionDialog.class);
            logger.warn("Could not load global.css for AddTransactionDialog: {}", e.getMessage());
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

        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);

        exitAnimation.setOnFinished(e -> modal.close());
        exitAnimation.play();
    }
}