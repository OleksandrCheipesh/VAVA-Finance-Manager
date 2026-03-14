package org.example.view.templates;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

        javafx.scene.Node backgroundRoot = owner.getScene().getRoot();
        backgroundRoot.setEffect(new GaussianBlur(15));
        modal.setOnHidden(e -> backgroundRoot.setEffect(null));

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-background-radius: 16; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 20, 0, 0, 10);");
        root.setPrefWidth(450);

        // Header & Close Button
        HBox header = new HBox();
        Label title = new Label("New Transaction");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> modal.close());
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, closeBtn);

        // --- Custom Toggle (Sale / Purchase) ---
        HBox toggleBox = new HBox();
        toggleBox.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 4;");
        Button saleBtn = new Button("+ Sale");
        Button purchaseBtn = new Button("- Purchase");

        String activeStyle = "-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: default;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;";

        saleBtn.setPrefWidth(200); purchaseBtn.setPrefWidth(200);
        saleBtn.setStyle(activeStyle); purchaseBtn.setStyle(inactiveStyle);

        saleBtn.setOnAction(e -> { selectedType = "SALE"; saleBtn.setStyle(activeStyle); purchaseBtn.setStyle(inactiveStyle); });
        purchaseBtn.setOnAction(e -> { selectedType = "PURCHASE"; purchaseBtn.setStyle(activeStyle); saleBtn.setStyle(inactiveStyle); });
        toggleBox.getChildren().addAll(saleBtn, purchaseBtn);

        // --- Fields ---
        FormField amountField = FormField.createNumericField("Amount", "0.00");
        FormField descField = FormField.createTextField("Description", "Enter description");

        HBox splitBox = new HBox(15);
        FormField clientField = FormField.createComboBox("Client", "Select Client");
        ((ComboBox<String>) clientField.getControl()).getItems().addAll("TechCorp", "Adobe", "Microsoft");
        FormField projectField = FormField.createComboBox("Project", "Select Project");
        ((ComboBox<String>) projectField.getControl()).getItems().addAll("Design", "Development", "Marketing");
        splitBox.getChildren().addAll(clientField, projectField);

        FormField dateField = FormField.createDatePicker("Date", "Select date");
        ((DatePicker) dateField.getControl()).setValue(java.time.LocalDate.now());

        // Buttons
        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        saveBtn.setOnAction(e -> {
            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ex) {}
                javafx.application.Platform.runLater(() -> {
                    TextField amtTxt = (TextField) amountField.getControl();
                    if (amtTxt.getText().isEmpty()) { amountField.setError("Amount is required"); saveBtn.setLoading(false); return; }

                    BigDecimal amount = new BigDecimal(amtTxt.getText());
                    String desc = ((TextField) descField.getControl()).getText();
                    java.time.LocalDate date = ((DatePicker) dateField.getControl()).getValue();

                    Integer clientId = 1;
                    Integer projectId = 1;

                    Transaction newTx = new Transaction(1, 1, projectId, clientId, selectedType, amount, desc, date);

                    onSuccess.accept(newTx);
                    modal.close();
                });
            }).start();
        });

        root.getChildren().addAll(header, toggleBox, amountField, descField, splitBox, dateField, saveBtn);

        Scene scene = new Scene(root);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }
}