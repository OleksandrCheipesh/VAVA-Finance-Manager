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
<<<<<<< HEAD
        showToast(ownerStage, "Success", message, Themes.TEXT_SUCCESS, "✔");
    }

    public static void showError(Stage ownerStage, String message) {
        showToast(ownerStage, "Error", message, Themes.TEXT_ERROR, "⚠");
=======
        showToast(ownerStage, "Success", message, Themes.PRIMARY, "✓");
    }

    public static void showError(Stage ownerStage, String message) {
        showToast(ownerStage, "Error", message, Themes.TEXT_ERROR, "✕");
>>>>>>> 001216d49568549d2a1122d2a002b0eb10899532
    }

    private static void showToast(Stage ownerStage, String titleText, String messageText, String colorHex, String iconText) {
        Popup popup = new Popup();

<<<<<<< HEAD
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
=======
        // Main card
        StackPane root = new StackPane();
        root.setPrefWidth(420);

        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;"
        );

        // Glow
        DropShadow standardShadow = new DropShadow(10, 0, 8, Color.rgb(0, 0, 0, 0.08));
        DropShadow glowEffect = new DropShadow(25, 0, 15, Color.web(colorHex + "4D"));

        glowEffect.setInput(standardShadow);

        root.setEffect(glowEffect);

        // Color stripe
        Region accentStripe = new Region();

        accentStripe.setPrefWidth(6);
        accentStripe.setMaxWidth(6);

        accentStripe.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                        "-fx-background-radius: 12 0 0 12;"
        );

        StackPane.setAlignment(accentStripe, Pos.CENTER_LEFT);

        // Context HBox
        HBox content = new HBox(20);

        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(22, 25, 22, 25));

        // Icon
        StackPane iconCircle = new StackPane();

        iconCircle.setMinWidth(40); iconCircle.setMaxWidth(40);
        iconCircle.setMinHeight(40); iconCircle.setMaxHeight(40);

        String lightBg = colorHex + "1A";

        iconCircle.setStyle(
                "-fx-background-color: " + lightBg + ";" +
                        "-fx-background-radius: 100;" +
                        "-fx-border-color: " + colorHex + "33;" +
                        "-fx-border-radius: 100;" +
                        "-fx-border-width: 1.5;"
>>>>>>> 001216d49568549d2a1122d2a002b0eb10899532
        );

        Label icon = new Label(iconText);

<<<<<<< HEAD
        VBox textBox = new VBox(5);
=======
        icon.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        iconCircle.getChildren().add(icon);

        // Texts
        VBox textBox = new VBox(2);

>>>>>>> 001216d49568549d2a1122d2a002b0eb10899532
        Label title = new Label(titleText);

        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #0F172A;");

<<<<<<< HEAD
        textBox.getChildren().addAll(title, message);
        root.getChildren().addAll(icon, textBox);
        popup.getContent().add(root);

=======
        Label msg = new Label(messageText);

        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B; -fx-line-spacing: 1.2;");

        textBox.getChildren().addAll(title, msg);

        content.getChildren().addAll(iconCircle, textBox);

        root.getChildren().addAll(accentStripe, content);

        StackPane.setMargin(content, new Insets(0, 0, 0, 6));

        // Wrapper
        StackPane wrapper = new StackPane(root);

        wrapper.setStyle("-fx-background-color: transparent;");
        wrapper.setPadding(new Insets(10, 10, 40, 10));

        popup.getContent().add(wrapper);

        // Position
>>>>>>> 001216d49568549d2a1122d2a002b0eb10899532
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

<<<<<<< HEAD
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
=======
        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), wrapper);
        fadeIn.setToValue(1.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), wrapper);
        slideIn.setToX(0);

        ParallelTransition entryAnimation = new ParallelTransition(fadeIn, slideIn);
        entryAnimation.setInterpolator(Interpolator.EASE_OUT);

        PauseTransition delay = new PauseTransition(Duration.seconds(3.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(350), wrapper);
        fadeOut.setToValue(0.0);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(350), wrapper);
        slideOut.setToX(50);

        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideOut);
        exitAnimation.setInterpolator(Interpolator.EASE_IN);

        SequentialTransition fullSequence = new SequentialTransition(entryAnimation, delay, exitAnimation);
        fullSequence.setOnFinished(e -> popup.hide());

        popup.show(ownerStage);
        fullSequence.play();
>>>>>>> 001216d49568549d2a1122d2a002b0eb10899532
    }
}