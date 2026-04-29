package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.model.database.entity.Account;
import org.example.model.database.entity.AccountCategory;
import org.example.viewModel.BudgetViewModel;

import java.math.BigDecimal;
import java.util.Locale;

public class BudgetDetailDialog {

    private static String formatCategory(org.example.model.database.entity.AccountCategory cat) {
        if (cat == null) return "Other";
        return switch (cat) {
            case BANK_ACCOUNT -> "Bank Account";
            case CASH -> "Cash";
            case CREDIT_LINE -> "Credit Line";
            case SAVINGS -> "Savings";
            case INVESTMENT -> "Investment";
            case OTHER -> "Other";
        };
    }

    public static void show(Stage owner, Account account, BudgetViewModel viewModel, Runnable onEdit, Runnable onDelete) {
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

        VBox root = new VBox(25);
        root.setPadding(new Insets(35, 45, 35, 45));
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 25, 0, 0, 10);"
        );
        root.setPrefWidth(550);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        HBox topNav = new HBox(15);
        topNav.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(40, 40);
        iconBox.setStyle("-fx-background-color: " + Themes.PRIMARY_TEAL + "; -fx-background-radius: 10;");
        iconBox.getChildren().add(IconFactory.getIcon("wallet", 20));

        Label topLabel = new Label("ACCOUNT DETAIL");
        topLabel.setStyle("-fx-font-weight: 900; -fx-font-size: 12px; -fx-text-fill: #64748B; -fx-letter-spacing: 1.5px;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

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
        HBox.setMargin(closeBtn, new Insets(0, -15, 0, 0));
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper, null));

        topNav.getChildren().addAll(iconBox, topLabel, topSpacer, closeBtn);

        VBox headerBox = new VBox(12);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(5, 0, 10, 0));

        AccountCategory category = account.getCategory();
        boolean isBankOrCash = category == AccountCategory.BANK_ACCOUNT || category == AccountCategory.CASH;
        boolean isSavingsOrInvestment = category == AccountCategory.SAVINGS || category == AccountCategory.INVESTMENT;
        boolean isCreditLine = category == AccountCategory.CREDIT_LINE;
        BigDecimal limitAmount = account.getLimitAmount() != null ? BigDecimal.valueOf(account.getLimitAmount()) : null;
        boolean hasUsableLimit = limitAmount != null && limitAmount.compareTo(BigDecimal.ZERO) > 0;

        BigDecimal currentBalance = account.getCurrentBalance() != null ? account.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal spentAmount = isCreditLine ? currentBalance : currentBalance;

        Label titleLabel = new Label(account.getAccountName());
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        Label categoryPill = new Label(formatCategory(account.getCategory()).toUpperCase());
        categoryPill.setStyle("-fx-background-color: #CCFBF1; -fx-text-fill: #0F766E; -fx-font-weight: 900; -fx-font-size: 10px; -fx-padding: 6 14; -fx-background-radius: 20; -fx-letter-spacing: 1px;");

        headerBox.getChildren().addAll(titleLabel, categoryPill);

        GridPane infoGrid = new GridPane();
        infoGrid.setMaxWidth(Double.MAX_VALUE);
        infoGrid.setVgap(25);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(60);
        infoGrid.getColumnConstraints().addAll(col1, col2);

        Label balanceLabel = new Label(isCreditLine ? "Total" : "Current Balance");
        balanceLabel.setStyle("-fx-font-weight: 900; -fx-font-size: 14px; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        double balance = spentAmount.doubleValue();
        String currency = account.getCurrency() != null ? account.getCurrency() : "USD";
        String balanceText = isCreditLine
                ? String.format(Locale.US, "%s %s%,.2f", currency, balance >= 0 ? "+" : "-", Math.abs(balance))
                : String.format(Locale.US, "%s %,.2f", currency, balance);
        String balanceColor = isCreditLine
                ? (balance < 0 ? Themes.TEXT_ERROR : Themes.DARK_GREEN)
                : Themes.DARK_GREEN;
        Label balanceValue = new Label(balanceText);
        balanceValue.setStyle("-fx-font-size: 34px; -fx-font-weight: 900; -fx-text-fill: " + balanceColor + ";");

        infoGrid.add(balanceLabel, 0, 0); infoGrid.add(balanceValue, 1, 0);
        GridPane.setValignment(balanceLabel, javafx.geometry.VPos.CENTER);
        GridPane.setValignment(balanceValue, javafx.geometry.VPos.CENTER);
        GridPane.setHalignment(balanceValue, javafx.geometry.HPos.RIGHT);

        VBox limitBox = new VBox(8);
        limitBox.setAlignment(Pos.CENTER_LEFT);
        Label limitLbl = new Label("ACCOUNT LIMIT");
        limitLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        HBox limitValBox = new HBox(6);
        limitValBox.setAlignment(Pos.CENTER_LEFT);

        ImageView limitIcon = IconFactory.getIcon("wallet", 18);
        String limitText = account.getLimitAmount() != null
                ? String.format(Locale.US, "%s %,.2f", currency, account.getLimitAmount().doubleValue())
                : "No limit set";
        Label limitVal = new Label(limitText);
        limitVal.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        limitValBox.getChildren().addAll(limitIcon, limitVal);
        limitBox.getChildren().addAll(limitLbl, limitValBox);

        VBox statusBox = new VBox(8);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        Label statusLbl = new Label("STATUS");
        statusLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        HBox statusValBox = new HBox(6);
        statusValBox.setAlignment(Pos.CENTER_RIGHT);

        ImageView statusIcon = IconFactory.getIcon("circle-check", 18);
        Label statusVal = new Label("Active");
        statusVal.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        statusValBox.getChildren().addAll(statusIcon, statusVal);
        statusBox.getChildren().addAll(statusLbl, statusValBox);

        if (!isBankOrCash) {
            infoGrid.add(limitBox, 0, 1); infoGrid.add(statusBox, 1, 1);
            GridPane.setHalignment(statusBox, javafx.geometry.HPos.RIGHT);
        }

        Region separator = new Region();
        separator.setMinHeight(1);
        separator.setStyle("-fx-background-color: #E2E8F0;");
        VBox.setMargin(separator, new Insets(5, 0, 5, 0));

        VBox utilBox = null;
        boolean isProgressEligibleCategory = isCreditLine || isSavingsOrInvestment;
        boolean showUtilSection = isProgressEligibleCategory && hasUsableLimit;

        if (showUtilSection) {
            BigDecimal utilizedAmount = currentBalance;
            String utilLabelText = isSavingsOrInvestment ? "Goal Progress" : "Credit Used";
            if (isCreditLine) {
                utilLabelText = "Credit Used";
                utilizedAmount = spentAmount.negate().max(BigDecimal.ZERO);
            }

            double utilization = 0.0;
            utilization = utilizedAmount
                    .divide(limitAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100.0))
                    .doubleValue();
            utilization = Math.min(utilization, 100.0);

            BigDecimal remaining = viewModel.getRemainingCredit(account);

            utilBox = new VBox(8);
            GridPane utilGrid = new GridPane();
            utilGrid.setMaxWidth(Double.MAX_VALUE);
            utilGrid.setVgap(4);

            ColumnConstraints utilLeftCol = new ColumnConstraints(); utilLeftCol.setPercentWidth(40); utilLeftCol.setHalignment(javafx.geometry.HPos.LEFT);
            ColumnConstraints utilRightCol = new ColumnConstraints(); utilRightCol.setPercentWidth(60); utilRightCol.setHalignment(javafx.geometry.HPos.RIGHT);
            utilGrid.getColumnConstraints().addAll(utilLeftCol, utilRightCol);

            Label utilLbl = new Label(utilLabelText);
            utilLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

            Label remLbl = new Label("REMAINING CAPACITY");
            remLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

            Label utilVal = new Label((int) utilization + "%");
            utilVal.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

            Label remVal = new Label(remaining != null
                    ? String.format(Locale.US, "%s %,.2f", currency, remaining.doubleValue())
                    : "—");
            remVal.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_GREEN + ";");

            utilGrid.add(utilLbl, 0, 0); utilGrid.add(remLbl, 1, 0);
            utilGrid.add(utilVal, 0, 1); utilGrid.add(remVal, 1, 1);
            GridPane.setValignment(utilVal, javafx.geometry.VPos.BASELINE);
            GridPane.setValignment(remVal, javafx.geometry.VPos.BASELINE);

            StackPane barContainer = new StackPane();
            barContainer.setAlignment(Pos.CENTER_LEFT);

            Region barBg = new Region(); barBg.setMinHeight(8); barBg.setMaxHeight(8); barBg.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10;");
            Region barFill = new Region(); barFill.setMinHeight(8); barFill.setMaxHeight(8);

            final double finalUtil = utilization;
            barFill.prefWidthProperty().bind(barBg.widthProperty().multiply(finalUtil / 100.0));
            barFill.maxWidthProperty().bind(barBg.widthProperty().multiply(finalUtil / 100.0));
            barFill.setStyle("-fx-background-color: " + Themes.DARK_GREEN + "; -fx-background-radius: 10;");

            barContainer.getChildren().addAll(barBg, barFill);
            utilBox.getChildren().addAll(utilGrid, barContainer);
        }

        HBox footer = new HBox(15);
        footer.setPadding(new Insets(15, 0, 0, 0));

        Button editBtn = new Button("Edit Account");
        editBtn.setGraphic(IconFactory.getWhiteIcon("square-pen", 18));
        editBtn.setMinHeight(52);
        editBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(editBtn, Priority.ALWAYS);

        editBtn.setStyle(
                "-fx-background-color: " + Themes.PRIMARY_TEAL + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: 900; " +
                        "-fx-background-radius: 12; " +
                        "-fx-cursor: hand;" +
                        "-fx-icon-text-gap: 10px;"
        );
        editBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper, onEdit));

        editBtn.setOnMousePressed(e -> {
            editBtn.setScaleX(0.98);
            editBtn.setScaleY(0.98);
        });

        editBtn.setOnMouseReleased(e -> {
            editBtn.setScaleX(1.0);
            editBtn.setScaleY(1.0);
        });

        Button deleteBtn = new Button();
        deleteBtn.setGraphic(IconFactory.getIcon("trash", 24));
        deleteBtn.setMinSize(52, 52);

        String normalDeleteStyle = "-fx-background-color: #FEE2E2; -fx-background-radius: 12; -fx-cursor: hand;";
        String hoverDeleteStyle = "-fx-background-color: #FECACA; -fx-background-radius: 12; -fx-cursor: hand;";

        deleteBtn.setStyle(normalDeleteStyle);

        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(hoverDeleteStyle));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(normalDeleteStyle));

        deleteBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper, onDelete));

        deleteBtn.setOnMousePressed(e -> {
            deleteBtn.setScaleX(0.95);
            deleteBtn.setScaleY(0.95);
        });

        deleteBtn.setOnMouseReleased(e -> {
            deleteBtn.setScaleX(1.0);
            deleteBtn.setScaleY(1.0);
        });

        footer.getChildren().addAll(editBtn, deleteBtn);

        root.getChildren().addAll(topNav, headerBox, infoGrid);
        if (!isBankOrCash) {
            root.getChildren().add(separator);
        }
        if (utilBox != null) {
            root.getChildren().add(utilBox);
        }
        root.getChildren().add(footer);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);
        modal.setScene(scene);

        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper); fadeIn.setToValue(1);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper); slideUp.setToY(0);

        new ParallelTransition(fadeIn, slideUp).play();

        modal.show();
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode, Runnable onFinishedCallback) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode); fadeOut.setToValue(0);
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode); slideDown.setToY(30);

        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);
        exitAnimation.setOnFinished(e -> {
            modal.close();
            if (onFinishedCallback != null) onFinishedCallback.run();
        });
        exitAnimation.play();
    }
}