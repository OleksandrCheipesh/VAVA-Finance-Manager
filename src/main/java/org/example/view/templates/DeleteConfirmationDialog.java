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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DeleteConfirmationDialog {

    public static void show(Stage owner, String itemName, Runnable onConfirm) {
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

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
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

        // Icon
        StackPane iconBg = new StackPane();

        iconBg.setMinSize(64, 64); iconBg.setMaxSize(64, 64);
        iconBg.setStyle("-fx-background-color: " + Themes.TEXT_ERROR + "15; -fx-background-radius: 32;");

        SVGPath trashIcon = new SVGPath();

        trashIcon.setContent("M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16");
        trashIcon.setFill(Color.TRANSPARENT); trashIcon.setStroke(Color.web(Themes.TEXT_ERROR)); trashIcon.setStrokeWidth(2.0);
        trashIcon.setScaleX(1.3); trashIcon.setScaleY(1.3);

        iconBg.getChildren().add(trashIcon);

        // Text
        VBox textContainer = new VBox(10);
        textContainer.setAlignment(Pos.CENTER);

        Label title = new Label(I18n.t("Delete Item?"));
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label message = new Label(I18n.t("Are you sure you want to permanently remove") + "\n'" + itemName + "'? " + I18n.t("This action cannot be undone."));
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-line-spacing: 4px;");
        message.setTextAlignment(TextAlignment.CENTER);

        textContainer.getChildren().addAll(title, message);

        // Buttons
        HBox actionBox = new HBox(15);

        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(15, 0, 0, 0));

        Button cancelBtn = new Button(I18n.t("Keep It"));

        cancelBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        cancelBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Button deleteBtn = new Button(I18n.t("Yes, Delete"));

        String delNormal = "-fx-background-color: " + Themes.TEXT_ERROR + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;";
        String delHover = "-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;";

        deleteBtn.setStyle(delNormal);
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(delHover));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(delNormal));

        deleteBtn.setOnAction(e -> {
            onConfirm.run();
            closeWithAnimation(modal, shadowWrapper);
        });

        actionBox.getChildren().addAll(cancelBtn, deleteBtn);
        root.getChildren().addAll(iconBg, textContainer, actionBox);

        Scene scene = new Scene(shadowWrapper);

        scene.setFill(null);
        modal.setScene(scene);

        // Animations
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