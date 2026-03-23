package org.example.view.templates;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import org.example.view.templates.StateButton; // Adjust import if needed

public class FilterBar extends HBox {

    private final TextField searchInput;
    private final ComboBox<String> filterDropdown;
    private final DatePicker datePicker;
    private final Button clearButton;

    public FilterBar() {
        super(15);
        this.setAlignment(Pos.CENTER_LEFT);

        // Explicitly load styles so the arrow-button fix applies
        try {
            this.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load styles for FilterBar.");
        }

        // Card Container
        this.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-border-color: " + Themes.BORDER_LIGHT + ";" +
                        "-fx-border-radius: 12; -fx-background-radius: 12;" +
                        "-fx-padding: 12 20;"
        );

        // The exact style from TransactionsView
        String filterStyle = "-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

        // Search
        searchInput = new TextField();
        searchInput.setStyle(filterStyle + " -fx-padding: 8 12;");
        searchInput.setPrefHeight(40);
        searchInput.getStyleClass().add("filter-item"); // Applies CSS fix
        StackPane searchPane = createPromptWrapper(searchInput, "\uD83D\uDD0D Search by name, role...");
        HBox.setHgrow(searchPane, Priority.ALWAYS);

        // Dropdown
        filterDropdown = new ComboBox<>();
        filterDropdown.setPromptText("Department");
        filterDropdown.setStyle(filterStyle);
        filterDropdown.getStyleClass().add("filter-item"); // Applies CSS fix
        filterDropdown.setPrefHeight(40);
        filterDropdown.setPrefWidth(150);

        // Date Picker
        datePicker = new DatePicker();
        datePicker.setStyle(filterStyle);
        datePicker.getStyleClass().add("filter-item"); // Applies CSS fix
        datePicker.setPrefHeight(40);
        datePicker.setPrefWidth(150);
        StackPane datePane = createPromptWrapper(datePicker, "Filter Date");

        // Clear Button
        clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;");
        clearButton.setOnAction(e -> clearAll());

        this.getChildren().addAll(searchPane, filterDropdown, datePane, clearButton);
    }

    private StackPane createPromptWrapper(Control control, String promptText) {
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);

        Label prompt = new Label(promptText);
        prompt.setStyle("-fx-text-fill: #9CA3AF; -fx-padding: 0 0 0 12; -fx-font-size: 14px;");
        prompt.setMouseTransparent(true);

        if (control instanceof TextField tf) {
            tf.textProperty().addListener((obs, old, val) -> prompt.setVisible(val.isEmpty()));
        } else if (control instanceof DatePicker dp) {
            dp.getEditor().textProperty().addListener((obs, old, val) -> prompt.setVisible(val.isEmpty()));
        }

        stack.getChildren().addAll(control, prompt);
        return stack;
    }

    public void clearAll() {
        searchInput.clear();
        filterDropdown.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        datePicker.getEditor().clear();
    }

    public TextField getSearchInput() { return searchInput; }
    public ComboBox<String> getFilterDropdown() { return filterDropdown; }
    public DatePicker getDatePicker() { return datePicker; }
    public Button getClearButton() { return clearButton; }
}