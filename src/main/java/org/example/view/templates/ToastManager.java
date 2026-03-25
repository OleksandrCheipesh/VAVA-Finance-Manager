package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToastManager {

    public static void showSuccess(Stage ownerStage, String message) {
        showToast(ownerStage, "Success", message, Themes.TEXT_SUCCESS, "✔");
    }

    public static void showError(Stage ownerStage, String message) {
        showToast(ownerStage, "Error", message, Themes.TEXT_ERROR, "⚠");
    }

    private static void showToast(Stage ownerStage, String titleText, String messageText, String colorHex, String iconText) {
        Popup popup = new Popup();

        HBox root = new HBox(15);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(15, 20, 15, 15));
        root.setPrefWidth(350);

        root.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: transparent transparent transparent " + colorHex + ";" +
                        "-fx-border-width: 0 0 0 6;" +
                        "-fx-border-radius: 8;" +
                        // Note: rgba is an effect parameter, not a raw background color, so leaving it inline is standard practice
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 4);"
        );

        Label icon = new Label(iconText);

        VBox textBox = new VBox(5);
        Label title = new Label(titleText);

        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #0F172A;");

        Label msg = new Label(messageText);

        textBox.getChildren().addAll(title, message);
        root.getChildren().addAll(icon, textBox);
        popup.getContent().add(root);

        popup.setOnShown(e -> {
            double popupWidth = 440;
            double indentationRight = 35;
            double indentationUp = 40;

            popup.setX(ownerStage.getX() + ownerStage.getWidth() - popupWidth + indentationRight);
            popup.setY(ownerStage.getY() + indentationUp);
        });

        // Animation
        wrapper.setOpacity(0);
        wrapper.setTranslateX(50);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), wrapper);
        fadeIn.setToValue(1.0);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }
}