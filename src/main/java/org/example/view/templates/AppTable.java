package org.example.view.templates;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class AppTable<T> extends TableView<T> {

    public AppTable(String emptyMessage) {
        super();

        Label placeholder = new Label(emptyMessage);
        placeholder.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        this.setPlaceholder(placeholder);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load the Figma CSS stylesheet we just created!
        String css = getClass().getResource("/styles/table.css").toExternalForm();
        this.getStylesheets().add(css);
    }
}