package org.example.view.templates;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class StateButton extends Button {

    public enum ButtonType { PRIMARY, SECONDARY, DANGER }

    private final String defaultColor;
    private final String hoverColor;
    private final String originalText;
    private final ProgressIndicator spinner;

    public StateButton(String text, ButtonType type) {
        super("");
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

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px;");

        spinner = new ProgressIndicator();
        spinner.setPrefSize(18, 18);
        spinner.setMaxSize(18, 18);
        spinner.setStyle("-fx-progress-color: white;");
        spinner.setVisible(false);

        Region rightSpacer = new Region();
        rightSpacer.setPrefSize(18, 18);
        rightSpacer.setMaxSize(18, 18);

        HBox graphicBox = new HBox(10);
        graphicBox.setAlignment(Pos.CENTER);
        graphicBox.getChildren().addAll(spinner, textLabel, rightSpacer);

        this.setGraphic(graphicBox);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        applyStyle(defaultColor);

        this.setOnMouseEntered(e -> {
            if (!isMouseTransparent()) applyStyle(hoverColor);
        });

        this.setOnMouseExited(e -> {
            if (!isMouseTransparent()) applyStyle(defaultColor);
        });

        this.setOnMousePressed(e -> {
            if (!isMouseTransparent()) {
                this.setScaleX(0.98);
                this.setScaleY(0.98);
            }
        });

        this.setOnMouseReleased(e -> {
            if (!isMouseTransparent()) {
                this.setScaleX(1.0);
                this.setScaleY(1.0);
            }
        });
    }

    private void applyStyle(String bgColor) {
        this.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 12 24;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-opacity: 1.0;"
        );
    }

    public void setLoading(boolean isLoading) {
        this.setMouseTransparent(isLoading);
        this.setFocusTraversable(!isLoading);
        spinner.setVisible(isLoading);

        String currentStyle = this.getStyle() == null ? "" : this.getStyle();
        currentStyle = currentStyle.replace("-fx-opacity: 0.85;", "").replace("-fx-opacity: 1.0;", "");

        if (isLoading) {
            this.setStyle(currentStyle + "-fx-opacity: 0.85;");
        } else {
            this.setStyle(currentStyle + "-fx-opacity: 1.0;");
        }
    }
}