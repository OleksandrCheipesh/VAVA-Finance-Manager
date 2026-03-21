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
import org.example.model.database.entity.Transaction;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class AddTransactionDialog {

    private static String selectedType = "SALE";

    public static void show(Stage owner, Consumer<Transaction> onSuccess) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        Scene ownerScene = owner.getScene();
        Paint originalFill = ownerScene.getFill();
        ownerScene.setFill(Color.web("#111827"));

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
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 10);"
        );
        root.setPrefWidth(450);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        // Header & Close Button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("New Transaction");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: #F3F4F6;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-text-fill: #6B7280;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 0;"
        );

        // Close Animation
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, closeBtn);

        // Custom Toggle (Sale / Purchase)
        HBox toggleBox = new HBox();
        toggleBox.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 4;");
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

        // Fields
        VBox form = new VBox(15);

        TextField amountField = UIFactory.inputField("0.00");
        VBox amountBox = createLabeledField("Amount", amountField);

        TextField descField = UIFactory.inputField("Enter description");
        VBox descBox = createLabeledField("Description", descField);

        ComboBox<String> clientCombo = UIFactory.inputComboBox("Select Client");
        clientCombo.getItems().addAll("TechCorp", "Adobe", "Microsoft");
        VBox clientBox = createLabeledField("Client", clientCombo);

        ComboBox<String> projectCombo = UIFactory.inputComboBox("Select Project");
        projectCombo.getItems().addAll("Design", "Development", "Marketing");
        VBox projectBox = createLabeledField("Project", projectCombo);

        HBox splitBox = new HBox(15);
        HBox.setHgrow(clientBox, Priority.ALWAYS);
        HBox.setHgrow(projectBox, Priority.ALWAYS);
        splitBox.getChildren().addAll(clientBox, projectBox);

        DatePicker datePicker = UIFactory.inputDatePicker("Select date");
        datePicker.setValue(java.time.LocalDate.now());
        VBox dateBox = createLabeledField("Date", datePicker);

        form.getChildren().addAll(amountBox, descBox, splitBox, dateBox);

        // Save Button
        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        saveBtn.setOnAction(e -> {
            if (amountField.getText().isEmpty()) {
                amountField.setStyle(amountField.getStyle() + "-fx-border-color: #EF4444;");
                return;
            }

            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ex) {}
                javafx.application.Platform.runLater(() -> {
                    try {
                        BigDecimal amount = new BigDecimal(amountField.getText());
                        String desc = descField.getText();
                        java.time.LocalDate date = datePicker.getValue();

                        Integer clientId = 1;
                        Integer projectId = 1;

                        Transaction newTx = new Transaction(1, 1, projectId, clientId, selectedType, amount, desc, date);

                        onSuccess.accept(newTx);
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (NumberFormatException ex) {
                        saveBtn.setLoading(false);
                        amountField.setStyle(amountField.getStyle() + "-fx-border-color: #EF4444;");
                    }
                });
            }).start();
        });

        root.getChildren().addAll(header, toggleBox, form, saveBtn);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(AddTransactionDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception ex) {
            System.err.println("Warning: Could not load global.css for Modal");
        }

        modal.setScene(scene);

        // Input Animation
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
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #111827; -fx-font-size: 13px;");
        VBox box = new VBox(5, label, field);
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