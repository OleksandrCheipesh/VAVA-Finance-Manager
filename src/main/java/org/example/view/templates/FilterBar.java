package org.example.view.templates;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.example.view.templates.Themes;

public class FilterBar extends HBox {

    private final TextField searchInput;
    private final ComboBox<String> filterDropdown;
    private final DatePicker datePicker;
    private final Button clearButton;

    public FilterBar() {
        super(15); // Consistent spacing
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 8;");

        // Search Input
        searchInput = new TextField();
        searchInput.setPromptText("Search...");
        searchInput.setStyle("-fx-background-color: " + Themes.BG_FIELD + "; -fx-border-color: " + Themes.BORDER_FIELD + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
        HBox.setHgrow(searchInput, Priority.ALWAYS); // Search bar takes up remaining space

        // Dropdown Filter
        filterDropdown = new ComboBox<>();
        filterDropdown.setPromptText("Filter by...");
        filterDropdown.setStyle("-fx-background-color: " + Themes.BG_FIELD + "; -fx-border-color: " + Themes.BORDER_FIELD + "; -fx-border-radius: 6; -fx-background-radius: 6;");

        // Calendar Date Picker
        datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setStyle("-fx-background-color: " + Themes.BG_FIELD + "; -fx-border-color: " + Themes.BORDER_FIELD + ";");

        // Clear Filters Button
        clearButton = new StateButton("Clear", StateButton.ButtonType.SECONDARY);
        clearButton.setOnAction(e -> clearAll());

        this.getChildren().addAll(searchInput, filterDropdown, datePicker, clearButton);
    }

    public void clearAll() {
        searchInput.clear();
        filterDropdown.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }

    // Getters so your Views can attach logic/listeners to these controls
    public TextField getSearchInput() { return searchInput; }
    public ComboBox<String> getFilterDropdown() { return filterDropdown; }
    public DatePicker getDatePicker() { return datePicker; }
    public Button getClearButton() { return clearButton; }
}