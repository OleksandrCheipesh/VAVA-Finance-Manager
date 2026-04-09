package org.example.view.templates;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToastManager {

  public static void showSuccess(Stage ownerStage, String message) {
    showToast(ownerStage, "Success", message, Themes.TEXT_SUCCESS, "circle-check");
  }

  public static void showError(Stage ownerStage, String message) {
    showToast(ownerStage, "Error", message, Themes.TEXT_ERROR, "circle-x");
  }

  private static void showToast(Stage ownerStage, String titleText, String messageText, String colorHex, String iconName) {
    Popup popup = new Popup();
    popup.setAutoFix(true);
    popup.setHideOnEscape(true);

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
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 4);"
    );

    // NEW: Load the SVG Icon using our factory!
    SVGPath icon = IconFactory.getIcon(iconName, colorHex, 24);

    VBox textBox = new VBox(5);
    Label title = new Label(titleText);
    title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

    Label message = new Label(messageText);
    message.setWrapText(true);
    message.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

    textBox.getChildren().addAll(title, message);
    root.getChildren().addAll(icon, textBox);
    popup.getContent().add(root);

    popup.setOnShown(e -> {
      popup.setX(ownerStage.getX() + ownerStage.getWidth() - root.getPrefWidth() - 40);
      popup.setY(ownerStage.getY() + 40);
    });

    popup.show(ownerStage);

    PauseTransition delay = new PauseTransition(Duration.seconds(3));
    delay.setOnFinished(e -> popup.hide());
    delay.play();
  }
}