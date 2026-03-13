package org.example.view.templates;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.view.templates.Themes;

public class ToastManager {

    public static void showSuccess(Stage ownerStage, String message) {
        showToast(ownerStage, message, Themes.TEXT_SUCCESS, "#D1FAE5"); // Light green bg
    }

    public static void showError(Stage ownerStage, String message) {
        showToast(ownerStage, message, Themes.TEXT_ERROR, "#FEE2E2"); // Light red bg
    }

    private static void showToast(Stage ownerStage, String message, String colorHex, String bgColorHex) {
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);

        Label label = new Label(message);
        label.setStyle(
                "-fx-background-color: " + bgColorHex + ";" +
                        "-fx-text-fill: " + colorHex + ";" +
                        "-fx-padding: 15 30;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: " + colorHex + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-font-size: 14px; -fx-font-weight: bold;"
        );

        StackPane pane = new StackPane(label);
        pane.setAlignment(Pos.CENTER);
        popup.getContent().add(pane);

        // Position fixed at bottom-center of the stage
        popup.setOnShown(e -> {
            popup.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - popup.getWidth() / 2);
            popup.setY(ownerStage.getY() + ownerStage.getHeight() - 100);
        });

        popup.show(ownerStage);

        // Auto-hide after 3 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }
}