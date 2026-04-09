package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.viewModel.BudgetViewModel;

import java.util.Locale;

public class BudgetDetailDialog {

    public static void show(Stage owner, BudgetViewModel.Budget budget, Runnable onEdit, Runnable onDelete) {
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

        // Nav Bar
        HBox topNav = new HBox(15);
        topNav.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(40, 40);
        iconBox.setStyle("-fx-background-color: " + Themes.PRIMARY_TEAL + "; -fx-background-radius: 10;");

        // NEW: Load header wallet icon from factory
        iconBox.getChildren().add(IconFactory.getIcon("wallet", Themes.DARK_TEXT, 20));

        Label topLabel = new Label("BUDGET DETAIL");
        topLabel.setStyle("-fx-font-weight: 900; -fx-font-size: 12px; -fx-text-fill: #64748B; -fx-letter-spacing: 1.5px;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-font-size: 18px; -fx-text-fill: #9CA3AF; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper, null));

        topNav.getChildren().addAll(iconBox, topLabel, topSpacer, closeBtn);

        // Header Title
        VBox headerBox = new VBox(12);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(5, 0, 10, 0));

        Label title = new Label(budget.getName());
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        Label categoryPill = new Label(budget.getCategory().toUpperCase());
        categoryPill.setStyle("-fx-background-color: #CCFBF1; -fx-text-fill: #0F766E; -fx-font-weight: 900; -fx-font-size: 10px; -fx-padding: 6 14; -fx-background-radius: 20; -fx-letter-spacing: 1px;");

        headerBox.getChildren().addAll(title, categoryPill);

        // Info grid
        GridPane infoGrid = new GridPane();
        infoGrid.setMaxWidth(Double.MAX_VALUE);
        infoGrid.setVgap(25);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(60);
        infoGrid.getColumnConstraints().addAll(col1, col2);

        Label balanceLabel = new Label("Current Balance");
        balanceLabel.setStyle("-fx-font-weight: 900; -fx-font-size: 14px; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        Label balanceValue = new Label(String.format(Locale.US, "$%,.2f", budget.getBalance()));
        balanceValue.setStyle("-fx-font-size: 38px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_GREEN + ";");

        infoGrid.add(balanceLabel, 0, 0); infoGrid.add(balanceValue, 1, 0);
        GridPane.setValignment(balanceLabel, javafx.geometry.VPos.CENTER);
        GridPane.setValignment(balanceValue, javafx.geometry.VPos.CENTER);

        VBox limitBox = new VBox(8);
        Label limitLbl = new Label("BUDGET LIMIT");
        limitLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        HBox limitValBox = new HBox(6);
        limitValBox.setAlignment(Pos.CENTER_LEFT);

        // NEW: Load limit icon from factory (Using wallet as a proxy for the flag)
        SVGPath limitIcon = IconFactory.getIcon("wallet", Themes.PRIMARY_TEAL, 18);

        Label limitVal = new Label(String.format(Locale.US, "$%,.2f", budget.getLimit()));
        limitVal.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        limitValBox.getChildren().addAll(limitIcon, limitVal);
        limitBox.getChildren().addAll(limitLbl, limitValBox);

        VBox statusBox = new VBox(8);
        Label statusLbl = new Label("STATUS");
        statusLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        HBox statusValBox = new HBox(6);
        statusValBox.setAlignment(Pos.CENTER_LEFT);

        // NEW: Load status icon from factory
        SVGPath statusIcon = IconFactory.getIcon("circle-check", Themes.PRIMARY_TEAL, 18);

        Label statusVal = new Label("Active");
        statusVal.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        statusValBox.getChildren().addAll(statusIcon, statusVal);
        statusBox.getChildren().addAll(statusLbl, statusValBox);

        infoGrid.add(limitBox, 0, 1); infoGrid.add(statusBox, 1, 1);

        // Separator
        Region separator = new Region();
        separator.setMinHeight(1);
        separator.setStyle("-fx-background-color: #E2E8F0;");
        VBox.setMargin(separator, new Insets(5, 0, 5, 0));

        // Utilization progress
        VBox utilBox = new VBox(8);
        GridPane utilGrid = new GridPane();
        utilGrid.setMaxWidth(Double.MAX_VALUE);
        utilGrid.setVgap(4);

        ColumnConstraints utilLeftCol = new ColumnConstraints(); utilLeftCol.setPercentWidth(40); utilLeftCol.setHalignment(javafx.geometry.HPos.LEFT);
        ColumnConstraints utilRightCol = new ColumnConstraints(); utilRightCol.setPercentWidth(60); utilRightCol.setHalignment(javafx.geometry.HPos.RIGHT);
        utilGrid.getColumnConstraints().addAll(utilLeftCol, utilRightCol);

        Label utilLbl = new Label("UTILIZATION");
        utilLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        Label remLbl = new Label("REMAINING CAPACITY");
        remLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        Label utilVal = new Label((int)budget.getUtilization() + "%");
        utilVal.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        double remaining = budget.getLimit() - budget.getBalance();
        Label remVal = new Label(String.format(Locale.US, "$%,.2f", remaining));
        remVal.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_GREEN + ";");

        utilGrid.add(utilLbl, 0, 0); utilGrid.add(remLbl, 1, 0);
        utilGrid.add(utilVal, 0, 1); utilGrid.add(remVal, 1, 1);
        GridPane.setValignment(utilVal, javafx.geometry.VPos.BASELINE);
        GridPane.setValignment(remVal, javafx.geometry.VPos.BASELINE);

        StackPane barContainer = new StackPane();
        barContainer.setAlignment(Pos.CENTER_LEFT);

        Region barBg = new Region(); barBg.setMinHeight(8); barBg.setMaxHeight(8); barBg.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10;");
        Region barFill = new Region(); barFill.setMinHeight(8); barFill.setMaxHeight(8);

        barFill.prefWidthProperty().bind(barBg.widthProperty().multiply(budget.getUtilization() / 100.0));
        barFill.maxWidthProperty().bind(barBg.widthProperty().multiply(budget.getUtilization() / 100.0));
        barFill.setStyle("-fx-background-color: " + Themes.DARK_GREEN + "; -fx-background-radius: 10;");

        barContainer.getChildren().addAll(barBg, barFill);
        utilBox.getChildren().addAll(utilGrid, barContainer);

        // Footer buttons
        HBox footer = new HBox(15);
        footer.setPadding(new Insets(15, 0, 0, 0));

        Button editBtn = new Button("Edit Budget");

        // NEW: Load edit icon from factory
        editBtn.setGraphic(IconFactory.getIcon("square-pen", "#ffffff", 18));

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

        Button deleteBtn = new Button();

        // NEW: Load trash icon from factory
        deleteBtn.setGraphic(IconFactory.getIcon("trash", "#EF4444", 24));

        deleteBtn.setMinSize(52, 52);
        deleteBtn.setStyle(
                "-fx-background-color: #FCE8E8; " +
                        "-fx-background-radius: 12; " +
                        "-fx-cursor: hand;"
        );

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this budget?\nThis action cannot be undone.", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Delete Budget");
            confirm.setHeaderText("Confirm Deletion");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    closeWithAnimation(modal, shadowWrapper, onDelete);
                }
            });
        });

        footer.getChildren().addAll(editBtn, deleteBtn);

        root.getChildren().addAll(topNav, headerBox, infoGrid, separator, utilBox, footer);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);
        modal.setScene(scene);

        // Animations
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
            if (onFinishedCallback != null) {
                onFinishedCallback.run();
            }
        });
        exitAnimation.play();
    }
}