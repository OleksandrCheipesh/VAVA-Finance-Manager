package org.example.view.templates;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class AppTable<T> extends TableView<T> {

    public AppTable(String emptyMessage) {
        super();

        // Empty state built-in
        Label placeholder = new Label(emptyMessage);
        placeholder.setStyle("-fx-text-fill: #B0B7C3; -fx-font-size: 16px; -fx-font-style: italic;");
        this.setPlaceholder(placeholder);

        // Standard styling for table, fonts, alternating colors, and headers
        this.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-selection-bar: #E5F7F6;" + // Light primary color for selection
                        "-fx-selection-bar-non-focused: #E5F7F6;"
        );

        // Note: To perfectly style alternating row colors and headers in JavaFX,
        // it is highly recommended to add a CSS file for the TableView specifically,
        // as inline styling for internal table nodes is very difficult in JavaFX.
        // E.g., this.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());

        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Columns stretch to fit
    }
}