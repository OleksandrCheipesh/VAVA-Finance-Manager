package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.User;

import java.util.Arrays;
import java.util.function.Consumer;

public class EditUserDialog {

    private static final double FIELD_HEIGHT = 44.0;

    public static void show(Stage owner, User user, ObservableList<User> users, Consumer<User> onSuccess) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        Scene ownerScene = owner.getScene();
        Paint originalFill = ownerScene.getFill();
        ownerScene.setFill(Color.web(Themes.TEXT_DARK));

        Node backgroundRoot = ownerScene.getRoot();
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.3);
        GaussianBlur blur = new GaussianBlur(15);
        blur.setInput(darken);
        backgroundRoot.setEffect(blur);

        modal.setOnHidden(e -> {
            backgroundRoot.setEffect(null);
            ownerScene.setFill(originalFill);
        });

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 10);"
        );
        root.setPrefWidth(480);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label(I18n.t("Edit User"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label subtitle = new Label(I18n.t("Update information for") + " " + user.getName() + " " + user.getSurname());
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subtitle);

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-text-fill: #9CA3AF;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 0;"
        );
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // Form
        VBox form = new VBox(18);
        VBox.setMargin(form, new Insets(10, 0, 0, 0));

        TextField nameField = createTextField(user.getName());
        VBox nameBox = createLabeledField(I18n.t("NAME"), nameField);

        TextField surnameField = createTextField(user.getSurname());
        VBox surnameBox = createLabeledField(I18n.t("SURNAME"), surnameField);

        GridPane nameGrid = new GridPane();
        nameGrid.setHgap(15);
        ColumnConstraints r1c1 = new ColumnConstraints(); r1c1.setPercentWidth(50);
        ColumnConstraints r1c2 = new ColumnConstraints(); r1c2.setPercentWidth(50);
        nameGrid.getColumnConstraints().addAll(r1c1, r1c2);
        nameGrid.add(nameBox, 0, 0);
        nameGrid.add(surnameBox, 1, 0);

        TextField emailField = createTextField(user.getEmail());
        VBox emailBox = createLabeledField(I18n.t("EMAIL ADDRESS"), emailField);

        ComboBox<Position> roleCombo = new ComboBox<>();

        if (user.getPosition() == Position.Director) {
            roleCombo.getItems().add(Position.Director);
            roleCombo.setDisable(true);
        } else {
            roleCombo.getItems().addAll(
                    Arrays.stream(Position.values())
                            .filter(p -> p != Position.Director)
                            .toList()
            );
        }

        roleCombo.setValue(user.getPosition());
        roleCombo.setMinHeight(FIELD_HEIGHT); roleCombo.setPrefHeight(FIELD_HEIGHT); roleCombo.setMaxHeight(FIELD_HEIGHT);
        roleCombo.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 15; -fx-border-width: 0; -fx-font-size: 14px;");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        VBox roleBox = createLabeledField(I18n.t("ACCESS ROLE"), roleCombo);

        form.getChildren().addAll(nameGrid, emailBox, roleBox);

        // Footer
        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setStyle("-fx-padding: 10 0 0 0;");
        VBox.setMargin(actionBox, new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button(I18n.t("Cancel"));
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand; -fx-background-insets: 0; -fx-padding: 10 15 10 0;");
        cancelBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Button saveBtn = new Button(I18n.t("Save Changes"));
        saveBtn.setMinHeight(48);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(saveBtn, Priority.ALWAYS);

        String normalStyle = "-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;";
        String hoverStyle = "-fx-background-color: " + Themes.BTN_PRIMARY_HOVER + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;";

        saveBtn.setStyle(normalStyle);
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle(hoverStyle));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle(normalStyle));

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: " + Themes.TEXT_ERROR + "; -fx-font-size: 12px;");
        errorLabel.setVisible(false);

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String email = emailField.getText().trim();
            Position position = roleCombo.getValue();

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || position == null) {
                errorLabel.setText(I18n.t("All fields are required."));
                errorLabel.setVisible(true);
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                errorLabel.setText(I18n.t("Please enter a valid email address."));
                errorLabel.setVisible(true);
                return;
            }

            user.setName(name);
            user.setSurname(surname);
            user.setEmail(email);
            user.setPosition(position);

            onSuccess.accept(user);
            closeWithAnimation(modal, shadowWrapper);
        });

        actionBox.getChildren().addAll(cancelBtn, saveBtn);
        root.getChildren().addAll(header, form, errorLabel, actionBox);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(EditUserDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load global.css for EditUserDialog.");
        }

        modal.setScene(scene);

        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper);
        fadeIn.setToValue(1);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper);
        slideUp.setToY(0);

        ParallelTransition entranceAnimation = new ParallelTransition(fadeIn, slideUp);
        modal.show();
        entranceAnimation.play();
    }

    private static TextField createTextField(String value) {
        TextField tf = new TextField(value);
        tf.setMinHeight(FIELD_HEIGHT); tf.setPrefHeight(FIELD_HEIGHT); tf.setMaxHeight(FIELD_HEIGHT);
        tf.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 15; -fx-border-width: 0; -fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        return tf;
    }

    private static VBox createLabeledField(String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 11px; -fx-letter-spacing: 0.5px;");
        VBox box = new VBox(6, label, field);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode);
        fadeOut.setToValue(0);
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode);
        slideDown.setToY(30);
        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);
        exitAnimation.setOnFinished(e -> modal.close());
        exitAnimation.play();
    }
}