package org.example.view.templates;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import org.example.view.templates.Themes;

public class StateButton extends Button {

    public enum ButtonType { PRIMARY, SECONDARY, DANGER }

    private final String defaultColor;
    private final String hoverColor;
    private final String originalText;

    public StateButton(String text, ButtonType type) {
        super(text);
        this.originalText = text;

        switch (type) {
            case PRIMARY -> {
                this.defaultColor = Themes.PRIMARY;
                this.hoverColor = Themes.BTN_PRIMARY_HOVER;
            }
            case SECONDARY -> {
                this.defaultColor = Themes.BTN_SECONDARY;
                this.hoverColor = Themes.BTN_SECONDARY_HOVER;
            }
            case DANGER -> {
                this.defaultColor = Themes.BTN_DANGER;
                this.hoverColor = Themes.BTN_DANGER_HOVER;
            }
            default -> {
                this.defaultColor = Themes.PRIMARY;
                this.hoverColor = Themes.BTN_PRIMARY_HOVER;
            }
        }

        applyStyle(defaultColor);

        // Hover state
        this.setOnMouseEntered(e -> applyStyle(hoverColor));
        this.setOnMouseExited(e -> applyStyle(defaultColor));
    }

    private void applyStyle(String bgColor) {
        this.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"
        );
    }

    public void setLoading(boolean isLoading) {
        if (isLoading) {
            this.setDisable(true);
            this.setText(""); // Hide text

            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setPrefSize(18, 18);
            spinner.setStyle("-fx-progress-color: white;");

            HBox graphicBox = new HBox(spinner);
            graphicBox.setAlignment(Pos.CENTER);
            this.setGraphic(graphicBox);
        } else {
            this.setDisable(false);
            this.setText(originalText);
            this.setGraphic(null);
        }
    }
}