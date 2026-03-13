package org.example.view.templates;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToastManager {

    public static void showSuccess(Stage ownerStage, String message) {
        // Using Unicode checkmark for the icon
        showToast(ownerStage, "Success", message, Themes.TEXT_SUCCESS, "✔");
    }

    public static void showError(Stage ownerStage, String message) {
        // Using Unicode warning sign for the icon
        showToast(ownerStage, "Error", message, Themes.TEXT_ERROR, "⚠");
    }

    private static void showToast(Stage ownerStage, String titleText, String messageText, String colorHex, String iconText) {
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);

        // Main Container (Matches Figma)
        HBox root = new HBox(15);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(15, 20, 15, 15));
        root.setPrefWidth(350);

        // White card, drop shadow, and thick left border
        root.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: transparent transparent transparent " + colorHex + ";" +
                        "-fx-border-width: 0 0 0 6;" + // 6px width on the left side only
                        "-fx-border-radius: 8;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 4);"
        );

        // Icon Label
        Label icon = new Label(iconText);
        icon.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Text Container (Title + Message)
        VBox textBox = new VBox(5);
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label message = new Label(messageText);
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        textBox.getChildren().addAll(title, message);
        root.getChildren().addAll(icon, textBox);

        popup.getContent().add(root);

        // Position Top-Right (accounting for window size and toast width)
        popup.setOnShown(e -> {
            popup.setX(ownerStage.getX() + ownerStage.getWidth() - root.getPrefWidth() - 40);
            popup.setY(ownerStage.getY() + 40);
        });

        popup.show(ownerStage);

        // Auto-hide after 3 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }
}