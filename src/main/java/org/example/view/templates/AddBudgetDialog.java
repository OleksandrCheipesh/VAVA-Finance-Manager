package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
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
import org.example.model.database.entity.AccountCategory;
import org.example.model.database.entity.AccountCycle;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class AddBudgetDialog {

    private static String selectedFreq = "MONTHLY";
    private static final double FIELD_HEIGHT = 44.0;

    private static String formatCategory(AccountCategory cat) {
        return switch (cat) {
            case BANK_ACCOUNT -> "Bank Account";
            case CASH -> "Cash";
            case CREDIT_LINE -> "Credit Line";
            case SAVINGS -> "Savings";
            case INVESTMENT -> "Investment";
            case OTHER -> "Other";
        };
    }

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

        root.setPrefWidth(550);

        StackPane shadowWrapper = new StackPane(root);

        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("New Account");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label subtitle = new Label("Define the parameters for your new financial account.");
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

        VBox form = new VBox(15);

        TextField nameField = UIFactory.inputField("e.g., Main Operating Account");
        nameField.setMinHeight(FIELD_HEIGHT);
        nameField.setPrefHeight(FIELD_HEIGHT);
        nameField.setMaxHeight(FIELD_HEIGHT);

        VBox nameBox = createLabeledField("ACCOUNT NAME", nameField);

        ComboBox<String> catCombo = UIFactory.inputComboBox("Select Category");

        for (AccountCategory cat : AccountCategory.values()) {
            catCombo.getItems().add(formatCategory(cat));
        }
        catCombo.setMaxWidth(Double.MAX_VALUE);
        catCombo.setMinHeight(FIELD_HEIGHT);
        catCombo.setPrefHeight(FIELD_HEIGHT);
        catCombo.setMaxHeight(FIELD_HEIGHT);

        VBox catBox = createLabeledField("CATEGORY", catCombo);

        TextField limitField = UIFactory.inputField("0.00 (optional)");

        limitField.setMaxWidth(Double.MAX_VALUE);
        limitField.setMinHeight(FIELD_HEIGHT);
        limitField.setPrefHeight(FIELD_HEIGHT);
        limitField.setMaxHeight(FIELD_HEIGHT);

        VBox limitBox = createLabeledField("LIMIT AMOUNT", limitField);

        GridPane row1Grid = new GridPane();
        row1Grid.setHgap(15);

        ColumnConstraints r1c1 = new ColumnConstraints(); r1c1.setPercentWidth(50);
        ColumnConstraints r1c2 = new ColumnConstraints(); r1c2.setPercentWidth(50);

        row1Grid.getColumnConstraints().addAll(r1c1, r1c2);
        row1Grid.add(catBox, 0, 0);
        row1Grid.add(limitBox, 1, 0);

        TextField balanceField = UIFactory.inputField("0.00");

        balanceField.setMaxWidth(Double.MAX_VALUE);
        balanceField.setMinHeight(FIELD_HEIGHT);
        balanceField.setPrefHeight(FIELD_HEIGHT);
        balanceField.setMaxHeight(FIELD_HEIGHT);

        VBox balanceBox = createLabeledField("INITIAL BALANCE", balanceField);

        ComboBox<String> currencyCombo = UIFactory.inputComboBox("Select Currency");

        currencyCombo.setItems(FXCollections.observableArrayList("CZK", "EUR", "GBP", "USD"));
        currencyCombo.setMaxWidth(Double.MAX_VALUE);
        currencyCombo.setMinHeight(FIELD_HEIGHT);
        currencyCombo.setPrefHeight(FIELD_HEIGHT);
        currencyCombo.setMaxHeight(FIELD_HEIGHT);

        VBox currencyBox = createLabeledField("CURRENCY", currencyCombo);

        GridPane row2Grid = new GridPane();
        row2Grid.setHgap(15);

        ColumnConstraints r2c1 = new ColumnConstraints(); r2c1.setPercentWidth(50);
        ColumnConstraints r2c2 = new ColumnConstraints(); r2c2.setPercentWidth(50);

        row2Grid.getColumnConstraints().addAll(r2c1, r2c2);
        row2Grid.add(balanceBox, 0, 0);
        row2Grid.add(currencyBox, 1, 0);

        VBox freqBoxMain = new VBox(5);

        Label freqLabel = new Label("FREQUENCY CYCLE");
        freqLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");

        HBox toggleBox = new HBox();

        toggleBox.setStyle("-fx-background-color: " + Themes.BG_FIELD + "; -fx-background-radius: 8; -fx-padding: 4;");
        toggleBox.setMaxWidth(Double.MAX_VALUE);
        toggleBox.setMinHeight(FIELD_HEIGHT);
        toggleBox.setPrefHeight(FIELD_HEIGHT);
        toggleBox.setMaxHeight(FIELD_HEIGHT);

        Button monthlyBtn = new Button("Monthly");
        Button weeklyBtn = new Button("Weekly");

        String activeStyle = "-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: default;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;";

        monthlyBtn.setMaxWidth(Double.MAX_VALUE); monthlyBtn.setMaxHeight(Double.MAX_VALUE);
        weeklyBtn.setMaxWidth(Double.MAX_VALUE); weeklyBtn.setMaxHeight(Double.MAX_VALUE);

        HBox.setHgrow(monthlyBtn, Priority.ALWAYS);
        HBox.setHgrow(weeklyBtn, Priority.ALWAYS);

        monthlyBtn.setStyle(selectedFreq.equals("MONTHLY") ? activeStyle : inactiveStyle);
        weeklyBtn.setStyle(selectedFreq.equals("WEEKLY") ? activeStyle : inactiveStyle);

        monthlyBtn.setOnAction(e -> {
            selectedFreq = "MONTHLY";
            monthlyBtn.setStyle(activeStyle);
            weeklyBtn.setStyle(inactiveStyle);
        });

        weeklyBtn.setOnAction(e -> {
            selectedFreq = "WEEKLY";
            weeklyBtn.setStyle(activeStyle);
            monthlyBtn.setStyle(inactiveStyle);
        });

        toggleBox.getChildren().addAll(monthlyBtn, weeklyBtn);

        freqBoxMain.getChildren().addAll(freqLabel, toggleBox);
        freqBoxMain.setMaxWidth(Double.MAX_VALUE);

        form.getChildren().addAll(nameBox, row1Grid, row2Grid, freqBoxMain);

        HBox actionBox = new HBox(20);

        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.setPadding(new Insets(20, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");

        cancelBtn.setMinWidth(Region.USE_PREF_SIZE);
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 10 20;");
        cancelBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        cancelBtn.setOnMousePressed(e -> {
            cancelBtn.setScaleX(0.98);
            cancelBtn.setScaleY(0.98);
        });

        cancelBtn.setOnMouseReleased(e -> {
            cancelBtn.setScaleX(1.0);
            cancelBtn.setScaleY(1.0);
        });

        StateButton saveBtn = new StateButton("Save Account", StateButton.ButtonType.PRIMARY);

        saveBtn.setMinHeight(50);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        String normalStyle = "-fx-background-color: " + Themes.PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;";

        String hoverStyle = "-fx-background-color: " + Themes.BTN_PRIMARY_HOVER + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;";

        saveBtn.setStyle(normalStyle);

        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle(hoverStyle));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle(normalStyle));

        saveBtn.setOnMousePressed(e -> {
            saveBtn.setScaleX(0.98);
            saveBtn.setScaleY(0.98);
        });

        saveBtn.setOnMouseReleased(e -> {
            saveBtn.setScaleX(1.0);
            saveBtn.setScaleY(1.0);
        });

        HBox.setHgrow(saveBtn, Priority.ALWAYS);

        String errorBorder = "-fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;";

        saveBtn.setOnAction(e -> {
            nameField.setStyle(nameField.getStyle().replace(errorBorder, ""));
            balanceField.setStyle(balanceField.getStyle().replace(errorBorder, ""));
            limitField.setStyle(limitField.getStyle().replace(errorBorder, ""));
            currencyCombo.setStyle(currencyCombo.getStyle().replace(errorBorder, ""));

            boolean isValid = true;

            if (nameField.getText().trim().isEmpty()) {
                nameField.setStyle(nameField.getStyle() + errorBorder);
                isValid = false;
            }

            BigDecimal balanceParsed = BigDecimal.ZERO;
            if (!balanceField.getText().trim().isEmpty()) {
                try {
                    balanceParsed = new BigDecimal(balanceField.getText().replace(",", "").trim());
                    if (balanceParsed.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    balanceField.setStyle(balanceField.getStyle() + errorBorder);
                    isValid = false;
                }
            }

            Integer limitParsed = null;
            if (!limitField.getText().trim().isEmpty()) {
                try {
                    double raw = Double.parseDouble(limitField.getText().replace(",", "").trim());
                    if (raw < 0) throw new NumberFormatException();
                    limitParsed = (int) raw;
                } catch (NumberFormatException ex) {
                    limitField.setStyle(limitField.getStyle() + errorBorder);
                    isValid = false;
                }
            }

            if (currencyCombo.getValue() == null) {
                currencyCombo.setStyle(currencyCombo.getStyle() + errorBorder);
                isValid = false;
            }

            if (!isValid) {
                ToastManager.showError(owner, "Please fill in all required fields correctly.");
                return;
            }

            saveBtn.setLoading(true);

            final BigDecimal finalBalance = balanceParsed;
            final Integer finalLimit = limitParsed;

            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ex) {}
                javafx.application.Platform.runLater(() -> {
                    Account account = new Account();
                    account.setAccountName(nameField.getText().trim());
                    account.setCurrentBalance(finalBalance);
                    account.setCurrency(currencyCombo.getValue());
                    account.setLimitAmount(finalLimit);

                    String selectedCatLabel = catCombo.getValue();
                    AccountCategory cat = AccountCategory.OTHER;
                    if (selectedCatLabel != null) {
                        for (AccountCategory c : AccountCategory.values()) {
                            if (formatCategory(c).equals(selectedCatLabel)) { cat = c; break; }
                        }
                    }
                    account.setCategory(cat);
                    account.setCycle(selectedFreq.equals("WEEKLY") ? AccountCycle.WEEKLY : AccountCycle.MONTHLY);

                    onSuccess.accept(account);
                    closeWithAnimation(modal, shadowWrapper);
                });
            }).start();
        });

        actionBox.getChildren().addAll(cancelBtn, saveBtn);

        root.getChildren().addAll(header, form, actionBox);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(AddBudgetDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            var logger = org.example.logging.AppLog.getLogger(AddBudgetDialog.class);
            logger.warn("Could not load global.css for AddBudgetDialog: {}", e.getMessage());
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