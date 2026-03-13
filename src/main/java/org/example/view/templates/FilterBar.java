package org.example.view.templates;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.example.view.templates.StateButton;

public class FilterBar extends HBox {

    private final TextField searchInput;
    private final ComboBox<String> filterDropdown;
    private final DatePicker datePicker;
    private final Button clearButton;

    public FilterBar() {
        super(15);
        this.setAlignment(Pos.CENTER_LEFT);

        // Figma Container (White card, padding, light border)
        this.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-border-color: " + Themes.BORDER_LIGHT + ";" +
                        "-fx-border-radius: 12; -fx-background-radius: 12;" +
                        "-fx-padding: 12 20;" // Slightly tighter padding to match Figma
        );

        // Figma Input Style (Light gray, no harsh borders)
        String figmaInputStyle =
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 6; -fx-background-radius: 6;" +
                        "-fx-padding: 8 12; -fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_DARK + ";";

        searchInput = new TextField();
        searchInput.setPromptText("\uD83D\uDD0D Search by name, email or role...");
        searchInput.setStyle(figmaInputStyle);
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        filterDropdown = new ComboBox<>();
        filterDropdown.setPromptText("Department");
        filterDropdown.setStyle(figmaInputStyle);

        datePicker = new DatePicker();
        datePicker.setPromptText("Filter Date");
        datePicker.setStyle(figmaInputStyle);

        clearButton = new StateButton("Clear", StateButton.ButtonType.SECONDARY);
        // Make the clear button text-only like Figma
        clearButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;");
        clearButton.setOnAction(e -> clearAll());

        this.getChildren().addAll(searchInput, filterDropdown, datePicker, clearButton);
    }

    public void clearAll() {
        searchInput.clear();
        filterDropdown.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }

    public TextField getSearchInput() { return searchInput; }
    public ComboBox<String> getFilterDropdown() { return filterDropdown; }
    public DatePicker getDatePicker() { return datePicker; }
    public Button getClearButton() { return clearButton; }
}