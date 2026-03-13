package org.example.view.templates;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.view.templates.Themes;

import java.util.function.UnaryOperator;

public class FormField extends VBox {

    private final Node inputControl;
    private final Label errorLabel;
    private final String baseStyle;

    // Factory method for standard Text Input
    public static FormField createTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        return new FormField(field, getStandardInputStyle());
    }

    // Factory method for Numeric Input (rejects letters)
    public static FormField createNumericField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);

        // TextFormatter to reject anything that isn't a digit
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
        return new FormField(field, getStandardInputStyle());
    }

    // Factory method for Dropdown
    public static FormField createComboBox(String placeholder) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(placeholder);
        combo.setMaxWidth(Double.MAX_VALUE);
        return new FormField(combo, getStandardInputStyle());
    }

    // Factory method for Date Picker
    public static FormField createDatePicker(String placeholder) {
        DatePicker picker = new DatePicker();
        picker.setPromptText(placeholder);
        picker.setMaxWidth(Double.MAX_VALUE);
        return new FormField(picker, getStandardInputStyle());
    }

    // Private constructor wrapper
    private FormField(Node inputControl, String baseStyle) {
        super(5); // 5px spacing between input and error label
        this.inputControl = inputControl;
        this.baseStyle = baseStyle;

        this.inputControl.setStyle(baseStyle);

        // Error Label setup
        this.errorLabel = new Label();
        this.errorLabel.setStyle("-fx-text-fill: " + Themes.TEXT_ERROR + "; -fx-font-size: 12px;");
        this.errorLabel.setVisible(false);
        this.errorLabel.setManaged(false); // Doesn't take up space when hidden

        // Focus State Listener
        this.inputControl.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                this.inputControl.setStyle(baseStyle + "-fx-border-color: " + Themes.STATE_FOCUS_RING + "; -fx-border-width: 2;");
            } else {
                this.inputControl.setStyle(baseStyle); // Revert to default
            }
        });

        this.getChildren().addAll(inputControl, errorLabel);
    }

    public void setError(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            errorLabel.setText(errorMessage);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            inputControl.setStyle(baseStyle + "-fx-border-color: " + Themes.STATE_ERROR_RING + "; -fx-border-width: 2;");
        } else {
            clearError();
        }
    }

    public void clearError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        inputControl.setStyle(baseStyle);
    }

    public void setDisabledState(boolean disabled) {
        inputControl.setDisable(disabled);
        this.setOpacity(disabled ? 0.5 : 1.0);
    }

    public Node getControl() {
        return inputControl;
    }

    private static String getStandardInputStyle() {
        return "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                "-fx-border-color: " + Themes.BORDER_LARGE + ";" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-padding: 10; -fx-font-size: " + Themes.FONT_FIELD + "px;" +
                "-fx-text-fill: " + Themes.TEXT_PRIMARY + ";";
    }
}