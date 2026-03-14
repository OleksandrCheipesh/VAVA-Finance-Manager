package org.example.view.templates;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Factory class for creating consistent UI components across the application
public class UIFactory {

    // Internal logic to keep prompt visible on focus and hide it only when typing
    private static void addSmartPromptListener(TextInputControl control, String placeholder) {
        control.setPromptText(placeholder);

        // Update prompt visibility based on actual content
        control.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.isEmpty()) {
                control.setPromptText("");
            } else {
                control.setPromptText(placeholder);
            }
        });

        // Ensure prompt stays visible when field gains focus but remains empty
        control.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && control.getText().isEmpty()) {
                control.setPromptText(placeholder);
            }
        });
    }

    // Public style constants for validation or external overrides
    public static final String LARGE_FIELD_STYLE =
            "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                    "-fx-control-inner-background: " + Themes.BG_FIELD_LARGE + ";" +
                    "-fx-border-color: " + Themes.BORDER_LARGE + ";" +
                    "-fx-border-width: 1; -fx-border-radius: 18; -fx-background-radius: 18;" +
                    "-fx-padding: " + Themes.FIELD_PADDING + "; -fx-font-size: " + Themes.FONT_FIELD + "px;" +
                    "-fx-text-fill: " + Themes.TEXT_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + Themes.TEXT_PRIMARY + ";";

    public static final String LARGE_COMBO_STYLE =
            "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                    "-fx-border-color: " + Themes.BORDER_LARGE + ";" +
                    "-fx-border-width: 1; -fx-border-radius: 18; -fx-background-radius: 18;" +
                    "-fx-font-size: " + Themes.FONT_FIELD + "px; -fx-text-fill: " + Themes.TEXT_PRIMARY + ";" +
                    "-fx-padding: 0; -fx-background-insets: 0;";

    // Small Input Fields (Login / Registration)
    public static TextField inputField(String placeholder) {
        TextField field = new TextField();
        field.setStyle(
                "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                        "-fx-border-color: " + Themes.BORDER_LARGE + ";" +
                        "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 10;" +
                        "-fx-prompt-text-fill: " + Themes.TEXT_PRIMARY + ";" +
                        "-fx-text-fill: " + Themes.TEXT_PRIMARY + ";"
        );
        addSmartPromptListener(field, placeholder);
        return field;
    }

    public static PasswordField passwordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setStyle(
                "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                        "-fx-border-color: " + Themes.BORDER_LARGE + ";" +
                        "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 10;" +
                        "-fx-prompt-text-fill: " + Themes.TEXT_PRIMARY + ";" +
                        "-fx-text-fill: " + Themes.TEXT_PRIMARY + ";"
        );
        addSmartPromptListener(field, placeholder);
        return field;
    }

    // Large Input Fields (CreateCompany)
    public static TextField largeInputField(String placeholder) {
        TextField field = new TextField();
        field.setStyle(LARGE_FIELD_STYLE);
        addSmartPromptListener(field, placeholder);
        return field;
    }

    public static TextArea largeTextArea(String placeholder, int rows) {
        TextArea area = new TextArea();
        area.setWrapText(true);
        area.setPrefRowCount(rows);
        area.setPrefHeight(100);
        area.setMinHeight(100);
        area.setStyle(
                "-fx-control-inner-background: " + Themes.BG_FIELD_LARGE + ";" +
                        "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                        "-fx-background-radius: 18; -fx-border-color: " + Themes.BORDER_LARGE + ";" +
                        "-fx-border-width: 1; -fx-border-radius: 18; -fx-font-size: " + Themes.FONT_FIELD + "px;" +
                        "-fx-text-fill: " + Themes.TEXT_PRIMARY + ";" +
                        "-fx-prompt-text-fill: " + Themes.TEXT_PRIMARY + ";" +
                        "-fx-padding: " + Themes.FIELD_PADDING + "; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        );
        addSmartPromptListener(area, placeholder);
        return area;
    }

    public static ComboBox<String> largeComboBox(String placeholder) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(placeholder);
        combo.setStyle(LARGE_COMBO_STYLE);
        return combo;
    }

    // Buttons & Links
    public static Button primaryButton(String text, double width) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setStyle(
                "-fx-background-color: " + Themes.PRIMARY + ";" +
                        "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;" +
                        "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        );
        return btn;
    }

    public static Hyperlink navLink(String text) {
        Hyperlink link = new Hyperlink(text);
        link.setStyle(
                "-fx-text-fill: " + Themes.PRIMARY_DARK + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
        return link;
    }

    // Misc
    public static Label messageLabel() {
        Label lbl = new Label();
        lbl.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-wrap-text: true;"
        );
        lbl.setPrefWidth(Themes.FORM_MAX_WIDTH);
        lbl.setAlignment(Pos.CENTER);
        lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        lbl.setMinHeight(60);
        return lbl;
    }

    // Shared left brand panel used by Login and Registration views
    public static StackPane brandPanel(String imageUrl) {
        Region bgImage = new Region();
        bgImage.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover; -fx-background-position: center;");

        Region colorOverlay = new Region();
        colorOverlay.setStyle("-fx-background-color: " + Themes.PRIMARY_OVERLAY + ";");

        Circle logoCircle = new Circle(60, Color.web(Themes.LOGO_CIRCLE));

        Label brandName = new Label("FinaM");
        brandName.setFont(Font.font("System", FontWeight.BOLD, 36));
        brandName.setTextFill(Color.WHITE);

        Label brandMotto = new Label("Your finances, your complete clarity");
        brandMotto.setFont(Font.font("System", 14));
        brandMotto.setTextFill(Color.WHITE);

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(logoCircle, brandName, brandMotto);

        StackPane panel = new StackPane();
        panel.getChildren().addAll(bgImage, colorOverlay, content);
        return panel;
    }
}