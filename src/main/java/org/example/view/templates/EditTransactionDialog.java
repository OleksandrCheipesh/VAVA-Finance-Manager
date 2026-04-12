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
import org.example.model.database.entity.Project;
import org.example.logging.AppLog;
import org.example.model.database.entity.Transaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EditTransactionDialog {

    private static String selectedType = "SALE";

    public static void show(Stage owner, Transaction existingTx, ObservableList<Project> projects, Consumer<Transaction> onSuccess) {
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

        // fields
        String initialAmount = existingTx != null && existingTx.getAmount() != null ? existingTx.getAmount().toString() : "";
        VBox form = new VBox(15);
        TextField amountField = UIFactory.inputField("0.00");
        if (!initialAmount.isEmpty()) amountField.setText(initialAmount);
        VBox amountBox = createLabeledField("Amount", amountField);

        String initialDesc = existingTx != null && existingTx.getDescription() != null ? existingTx.getDescription() : "";
        TextField descField = UIFactory.inputField("Enter description");
        if (!initialDesc.isEmpty()) descField.setText(initialDesc);
        VBox descBox = createLabeledField("Description", descField);

        ComboBox<String> clientCombo = UIFactory.inputComboBox("Select Client");
        clientCombo.getItems().addAll("TechCorp", "Adobe", "Microsoft");
        VBox clientBox = createLabeledField("Client", clientCombo);

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
        VBox projectBox = createLabeledField("Project", projectCombo);

        HBox splitBox = new HBox(15);
        HBox.setHgrow(clientBox, Priority.ALWAYS);
        HBox.setHgrow(projectBox, Priority.ALWAYS);
        splitBox.getChildren().addAll(clientBox, projectBox);

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

        VBox dateBox = createLabeledField("Date", datePicker);

        form.getChildren().addAll(amountBox, descBox, splitBox, dateBox);

        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        final String origAmountStyle = amountField.getStyle();
        final String origDescStyle = descField.getStyle();
        final String origDateStyle = datePicker.getStyle();

        String errorBorder = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 6;";

        saveBtn.setOnAction(e -> {
            // Restore original styles to clear any previous error borders
            amountField.setStyle(origAmountStyle);
            descField.setStyle(origDescStyle);
            datePicker.setStyle(origDateStyle);

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
            if (!valid) return;

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
                        Integer clientId = null; // TODO: replace with real client selection
                        Integer projectId = projectNameToId.get(projectCombo.getValue());
                        int accountId = 1; // TODO: replace with real account selection

                        int txId = existingTx != null ? existingTx.getId() : 0;
                        int txCompanyId = existingTx != null ? existingTx.getCompanyId() : 0;
                        Transaction newTx = new Transaction(txCompanyId, accountId, projectId, clientId, selectedType, amount, desc, date);
                        newTx.setId(txId);

                        onSuccess.accept(newTx);
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (NumberFormatException ex) {
                        saveBtn.setLoading(false);
                        amountField.setStyle(amountField.getStyle() + errorBorder);
                    } catch (Exception ex) {
                        saveBtn.setLoading(false);
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

        // animation
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
        return new VBox(5, label, field);
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