package org.example.view.templates;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import org.example.view.templates.StateButton; // Adjust import if needed
import org.example.logging.AppLog;

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
            var logger = AppLog.getLogger(FilterBar.class);
            logger.warn("Could not load styles for FilterBar: {}", e.getMessage());
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

        HBox promptBox = new HBox(8);
        promptBox.setAlignment(Pos.CENTER_LEFT);
        promptBox.setPadding(new Insets(0, 0, 0, 12));
        promptBox.setMouseTransparent(true);

        // Draw the professional SVG magnifying glass (No emojis!)
        if (promptText.contains("Search")) {
            SVGPath searchIcon = new SVGPath();
            searchIcon.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
            searchIcon.setFill(Color.web(Themes.TEXT_MUTED));
            searchIcon.setScaleX(0.8);
            searchIcon.setScaleY(0.8);
            promptBox.getChildren().add(searchIcon);
            promptText = promptText.replace("\uD83D\uDD0D ", ""); // Strip out the old emoji if passed
        }

        Label prompt = new Label(promptText);
        prompt.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        promptBox.getChildren().add(prompt);

        if (control instanceof TextField tf) {
            tf.textProperty().addListener((obs, old, val) -> promptBox.setVisible(val.isEmpty()));
        } else if (control instanceof DatePicker dp) {
            dp.getEditor().textProperty().addListener((obs, old, val) -> promptBox.setVisible(val.isEmpty()));
        }

        stack.getChildren().addAll(control, promptBox);
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