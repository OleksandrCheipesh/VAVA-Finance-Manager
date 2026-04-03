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
import org.example.model.database.entity.Account;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class AddAccountDialog {

    public static void show(Stage owner, Consumer<Account> onSuccess) {
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
        root.setPrefWidth(420);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("New Account");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: " + Themes.BG_FIELD + ";" +
                        "-fx-background-radius: 8; -fx-cursor: hand;" +
                        "-fx-text-fill: " + Themes.TEXT_MUTED + ";" +
                        "-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0;"
        );
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, closeBtn);

        // Fields
        VBox form = new VBox(15);

        TextField nameField = UIFactory.inputField("e.g. Main Account");
        VBox nameBox = createLabeledField("Account Name", nameField);

        TextField balanceField = UIFactory.inputField("0.00");
        VBox balanceBox = createLabeledField("Initial Balance", balanceField);

        ComboBox<String> currencyCombo = UIFactory.inputComboBox("Select Currency");
        currencyCombo.getItems().addAll("CZK", "EUR", "GBP", "USD");
        VBox currencyBox = createLabeledField("Currency", currencyCombo);

        form.getChildren().addAll(nameBox, balanceBox, currencyBox);

        // Save Button
        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        final String origNameStyle     = nameField.getStyle();
        final String origBalanceStyle  = balanceField.getStyle();
        final String origCurrencyStyle = currencyCombo.getStyle();
        String errorBorder = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 6;";

        saveBtn.setOnAction(e -> {
            nameField.setStyle(origNameStyle);
            balanceField.setStyle(origBalanceStyle);
            currencyCombo.setStyle(origCurrencyStyle);

            boolean valid = true;
            if (nameField.getText().isBlank()) {
                nameField.setStyle(nameField.getStyle() + errorBorder);
                valid = false;
            }
            if (balanceField.getText().isBlank()) {
                balanceField.setStyle(balanceField.getStyle() + errorBorder);
                valid = false;
            }
            if (currencyCombo.getValue() == null) {
                currencyCombo.setStyle(currencyCombo.getStyle() + errorBorder);
                valid = false;
            }
            if (!valid) return;

            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                javafx.application.Platform.runLater(() -> {
                    try {
                        BigDecimal balance = new BigDecimal(balanceField.getText().trim());
                        if (balance.compareTo(BigDecimal.ZERO) < 0) {
                            saveBtn.setLoading(false);
                            balanceField.setStyle(balanceField.getStyle() + errorBorder);
                            return;
                        }
                        String name = nameField.getText().trim();
                        String currency = currencyCombo.getValue();

                        // companyId set by ViewModel
                        Account newAccount = new Account(0, name, balance, currency);
                        onSuccess.accept(newAccount);
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (NumberFormatException ex) {
                        saveBtn.setLoading(false);
                        balanceField.setStyle(balanceField.getStyle() + errorBorder);
                    } catch (Exception ex) {
                        saveBtn.setLoading(false);
                        System.err.println("Unexpected error saving account: " + ex.getMessage());
                    }
                });
            }).start();
        });

        root.getChildren().addAll(header, form, saveBtn);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(AddAccountDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load global.css for AddAccountDialog.");
        }

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
        return new VBox(5, label, field);
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
