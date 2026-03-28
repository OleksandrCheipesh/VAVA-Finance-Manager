package org.example.view.templates;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SummaryCard extends VBox {

    private final Label valueLabel;
    private final Label subLabel;

    public SummaryCard(String title, String value, String subText, String valColor) {
        super(5); // Space between texts

        // Basic card style
        this.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + "; " +
                        "-fx-border-color: " + Themes.BORDER_LIGHT + "; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 20;"
        );

        // Ensures that the cards are spread evenly across the entire width
        HBox.setHgrow(this, Priority.ALWAYS);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 15px; -fx-font-weight: bold;");

        // Main value
        valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + valColor + "; -fx-font-size: 29px; -fx-font-weight: bold;");

        // Subtext
        subLabel = new Label(subText);
        subLabel.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");

        // Add text to cards
        this.getChildren().addAll(titleLabel, valueLabel, subLabel);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubText(String subText) {
        subLabel.setText(subText);
    }
}