package org.example.view.templates;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.function.UnaryOperator;

public class FormField extends VBox {

    private final Node inputControl;
    private final Label errorLabel;
    private final String baseStyle;

    // --- FACTORY METHODS ---

    public static FormField createTextField(String labelText, String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        return new FormField(createTopLabel(labelText), field, getFigmaInputStyle());
    }

    public static FormField createNumericField(String labelText, String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);

        // Rejects letters
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getText().matches("[0-9]*")) return change;
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
        return new FormField(createTopLabel(labelText), field, getFigmaInputStyle());
    }

    public static FormField createComboBox(String labelText, String placeholder) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(placeholder);
        combo.setMaxWidth(Double.MAX_VALUE);
        return new FormField(createTopLabel(labelText), combo, getFigmaInputStyle());
    }

    public static FormField createDatePicker(String labelText, String placeholder) {
        DatePicker picker = new DatePicker();
        picker.setPromptText(placeholder);
        picker.setMaxWidth(Double.MAX_VALUE);
        return new FormField(createTopLabel(labelText), picker, getFigmaInputStyle());
    }

    // --- HELPER & CONSTRUCTOR ---

    private static Label createTopLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-weight: bold;");
        return label;
    }

    private FormField(Label topLabel, Node inputControl, String baseStyle) {
        super(5);
        this.inputControl = inputControl;
        this.baseStyle = baseStyle;

        this.inputControl.setStyle(baseStyle);

        this.errorLabel = new Label();
        this.errorLabel.setStyle("-fx-text-fill: " + Themes.TEXT_ERROR + "; -fx-font-size: 12px;");
        this.errorLabel.setVisible(false);
        this.errorLabel.setManaged(false);

        this.inputControl.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                this.inputControl.setStyle(baseStyle + "-fx-border-color: " + Themes.STATE_FOCUS_RING + "; -fx-border-width: 2;");
            } else {
                this.inputControl.setStyle(baseStyle);
            }
        });

        this.getChildren().addAll(topLabel, inputControl, errorLabel);
    }

    // --- PUBLIC METHODS ---

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

    private static String getFigmaInputStyle() {
        return "-fx-background-color: " + Themes.BG_DASHBOARD + ";" +
                "-fx-border-color: " + Themes.BORDER_LIGHT + ";" +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;" +
                "-fx-font-size: 14px;";
    }
}