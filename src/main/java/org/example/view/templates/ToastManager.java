package org.example.view.templates;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
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
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);

        // Root card — StackPane layers the accent stripe behind the content
        StackPane root = new StackPane();
        root.setPrefWidth(420);
        root.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;"
        );

        // Colored glow shadow
        DropShadow standardShadow = new DropShadow(10, 0, 8, Color.rgb(0, 0, 0, 0.08));
        DropShadow glowEffect = new DropShadow(25, 0, 15, Color.web(colorHex + "4D"));
        glowEffect.setInput(standardShadow);
        root.setEffect(glowEffect);

        // Left accent stripe
        Region accentStripe = new Region();
        accentStripe.setPrefWidth(6);
        accentStripe.setMaxWidth(6);
        accentStripe.setMaxHeight(Double.MAX_VALUE);
        accentStripe.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                "-fx-background-radius: 12 0 0 12;"
        );
        StackPane.setAlignment(accentStripe, Pos.CENTER_LEFT);

        // Content row — offset right to clear the stripe
        HBox content = new HBox(20);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(22, 25, 22, 25));
        StackPane.setMargin(content, new Insets(0, 0, 0, 6));

        // Icon circle
        StackPane iconCircle = new StackPane();
        iconCircle.setMinSize(40, 40);
        iconCircle.setMaxSize(40, 40);
        iconCircle.setStyle(
                "-fx-background-color: " + colorHex + "1A;" +
                "-fx-background-radius: 100;" +
                "-fx-border-color: " + colorHex + "33;" +
                "-fx-border-radius: 100;" +
                "-fx-border-width: 1.5;"
        );
        Label icon = new Label(iconText);
        icon.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        iconCircle.getChildren().add(icon);

        // Text block
        VBox textBox = new VBox(2);
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #0F172A;");
        Label msg = new Label(messageText);
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B; -fx-line-spacing: 1.2;");
        textBox.getChildren().addAll(title, msg);

        content.getChildren().addAll(iconCircle, textBox);
        root.getChildren().addAll(accentStripe, content);

        // Outer wrapper — provides bottom padding so shadow isn't clipped
        StackPane wrapper = new StackPane(root);
        wrapper.setStyle("-fx-background-color: transparent;");
        wrapper.setPadding(new Insets(10, 10, 40, 10));

        popup.getContent().add(wrapper);

        // Position: top-right of the owner window
        popup.setOnShown(e -> {
            double popupWidth = 440;
            double indentationRight = 35;
            popup.setX(ownerStage.getX() + ownerStage.getWidth() - popupWidth - indentationRight);
            popup.setY(ownerStage.getY() + 35);
        });

        // Entry: slide in from right + fade in
        wrapper.setOpacity(0);
        wrapper.setTranslateX(50);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), wrapper);
        fadeIn.setToValue(1.0);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), wrapper);
        slideIn.setToX(0);
        ParallelTransition entryAnimation = new ParallelTransition(fadeIn, slideIn);
        entryAnimation.setInterpolator(Interpolator.EASE_OUT);

        PauseTransition hold = new PauseTransition(Duration.seconds(3.5));

        // Exit: slide out to right + fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(350), wrapper);
        fadeOut.setToValue(0.0);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(350), wrapper);
        slideOut.setToX(50);
        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideOut);
        exitAnimation.setInterpolator(Interpolator.EASE_IN);

        SequentialTransition fullSequence = new SequentialTransition(entryAnimation, hold, exitAnimation);
        fullSequence.setOnFinished(e -> popup.hide());

        popup.show(ownerStage);
        fullSequence.play();
    }
}
