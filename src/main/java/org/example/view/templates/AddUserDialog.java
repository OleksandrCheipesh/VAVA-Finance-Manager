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
import org.example.model.PasswordUtil;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.User;
import org.example.model.validation.RegisterValidator;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Arrays;
import java.util.function.Consumer;

public class AddUserDialog {

    private static final double FIELD_HEIGHT = 44.0;

    public static void show(Stage owner, ObservableList<User> users, Consumer<User> onSuccess) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        // --- Zhodný Blur a Tmavosť s ostatnými dialógmi ---
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

        shadowWrapper.setOnMouseClicked(e -> {
            if (e.getTarget() == shadowWrapper) closeWithAnimation(modal, shadowWrapper);
        });

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Add User");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label subtitle = new Label("Grant access permissions for a new team member");
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subtitle);

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-text-fill: " + Themes.TEXT_MUTED + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 0;"
        );
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // Form
        VBox form = new VBox(15);
        VBox.setMargin(form, new Insets(10, 0, 0, 0));

        TextField nameField = UIFactory.inputField("e.g. Julian");
        nameField.setMinHeight(FIELD_HEIGHT); nameField.setPrefHeight(FIELD_HEIGHT); nameField.setMaxHeight(FIELD_HEIGHT);
        VBox nameBox = createLabeledField("NAME", nameField);

        TextField surnameField = UIFactory.inputField("e.g. Sterling");
        surnameField.setMinHeight(FIELD_HEIGHT); surnameField.setPrefHeight(FIELD_HEIGHT); surnameField.setMaxHeight(FIELD_HEIGHT);
        VBox surnameBox = createLabeledField("SURNAME", surnameField);

        GridPane nameGrid = new GridPane();
        nameGrid.setHgap(15);
        ColumnConstraints r1c1 = new ColumnConstraints(); r1c1.setPercentWidth(50);
        ColumnConstraints r1c2 = new ColumnConstraints(); r1c2.setPercentWidth(50);
        nameGrid.getColumnConstraints().addAll(r1c1, r1c2);
        nameGrid.add(nameBox, 0, 0);
        nameGrid.add(surnameBox, 1, 0);

        TextField emailField = UIFactory.inputField("julian.s@mintmanagement.com");
        emailField.setMinHeight(FIELD_HEIGHT); emailField.setPrefHeight(FIELD_HEIGHT); emailField.setMaxHeight(FIELD_HEIGHT);
        VBox emailBox = createLabeledField("EMAIL ADDRESS", emailField);

        // Password field so zhodným dizajnom
        PasswordField passField = new PasswordField();
        VBox passwordBox = createSecurePasswordFieldWrapper("PASSWORD", "••••••••••••", passField);

        // ComboBox so zhodným dizajnom
        boolean hasDirector = users.stream().anyMatch(u -> u.getPosition() == Position.Director);
        ComboBox<Position> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(
                Arrays.stream(Position.values())
                        .filter(p -> p != Position.Director || !hasDirector)
                        .toList()
        );
        roleCombo.setPromptText("Select organization role");
        roleCombo.setMinHeight(FIELD_HEIGHT);
        roleCombo.setPrefHeight(FIELD_HEIGHT);
        roleCombo.setMaxHeight(FIELD_HEIGHT);
        roleCombo.setMaxWidth(Double.MAX_VALUE); // Aby sa natiahol úplne doprava

        // Dynamické skopírovanie štýlu z UIFactory, aby bol ComboBox na 100% zhodný s TextFieldom
        ComboBox<String> dummyCombo = UIFactory.inputComboBox("");
        roleCombo.getStyleClass().setAll(dummyCombo.getStyleClass());
        roleCombo.setStyle(dummyCombo.getStyle());

        VBox roleBox = createLabeledField("ACCESS ROLE", roleCombo);

        form.getChildren().addAll(nameGrid, emailBox, passwordBox, roleBox);

        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: " + Themes.TEXT_ERROR + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.visibleProperty().addListener((obs, oldVal, newVal) -> errorLabel.setManaged(newVal));

        // Footer
        HBox actionBox = new HBox(20);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.setStyle("-fx-padding: 10 0 0 0;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setMinWidth(Region.USE_PREF_SIZE); // Zabraňuje "Ca..." scvrknutiu
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 10 20;");
        cancelBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        StateButton saveBtn = new StateButton("Save User", StateButton.ButtonType.PRIMARY);
        saveBtn.setMinHeight(50);
        saveBtn.setPrefWidth(340);
        HBox.setHgrow(saveBtn, Priority.ALWAYS);

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passField.getText().trim();
            Position selectedPosition = roleCombo.getValue();

            errorLabel.setVisible(false);

            if (selectedPosition == null) {
                errorLabel.setText("Please select a role.");
                errorLabel.setVisible(true);
                return;
            }

            try {
                RegisterValidator.validate(name, surname, email, password);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
                return;
            }

            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(600); } catch (InterruptedException ex) {}
                javafx.application.Platform.runLater(() -> {
                    String hashedPassword = PasswordUtil.hash(password);
                    User newUser = new User(name, surname, email, hashedPassword, selectedPosition, null);
                    onSuccess.accept(newUser);
                    closeWithAnimation(modal, shadowWrapper);
                });
            }).start();
        });

        actionBox.getChildren().addAll(cancelBtn, saveBtn);
        root.getChildren().addAll(header, form, errorLabel, actionBox);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(AddUserDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load global.css for AddUserDialog.");
        }

        modal.setScene(scene);

        // Animation
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

    private static VBox createLabeledField(String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");

        VBox box = new VBox(5, label, field);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);

        return box;
    }

    private static VBox createSecurePasswordFieldWrapper(String labelText, String prompt, PasswordField pf) {
        VBox box = new VBox(5);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");

        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER_RIGHT);
        pane.setMaxWidth(Double.MAX_VALUE); // Aby sa wrapper natiahol na maximum

        TextField dummy = UIFactory.inputField(""); // Získame CSS štýly priamo z tvojej továrne

        pf.setPromptText(prompt);
        pf.setMinHeight(FIELD_HEIGHT); pf.setPrefHeight(FIELD_HEIGHT); pf.setMaxHeight(FIELD_HEIGHT);
        pf.setMaxWidth(Double.MAX_VALUE);
        pf.getStyleClass().setAll(dummy.getStyleClass()); // Aplikujeme CSS od UIFactory
        pf.setStyle(dummy.getStyle() + "; -fx-padding: 0 45 0 15;"); // Zachováme miesto pre ikonku oka

        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMinHeight(FIELD_HEIGHT); tf.setPrefHeight(FIELD_HEIGHT); tf.setMaxHeight(FIELD_HEIGHT);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.getStyleClass().setAll(dummy.getStyleClass());
        tf.setStyle(dummy.getStyle() + "; -fx-padding: 0 45 0 15;");
        tf.setManaged(false); tf.setVisible(false);
        tf.textProperty().bindBidirectional(pf.textProperty());

        FontIcon eyeIcon = new FontIcon("fas-eye");
        eyeIcon.setIconSize(14);
        eyeIcon.setIconColor(Color.web(Themes.TEXT_MUTED));

        StackPane eyePane = new StackPane(eyeIcon);
        eyePane.setPadding(new Insets(0, 15, 0, 0));
        eyePane.setStyle("-fx-cursor: hand;");
        eyePane.setMaxWidth(45);
        eyePane.setAlignment(Pos.CENTER_RIGHT);

        eyePane.setOnMousePressed(e -> {
            pf.setVisible(false); pf.setManaged(false);
            tf.setVisible(true); tf.setManaged(true);
            eyeIcon.setIconColor(Color.web(Themes.PRIMARY));
        });
        eyePane.setOnMouseReleased(e -> {
            tf.setVisible(false); tf.setManaged(false);
            pf.setVisible(true); pf.setManaged(true);
            eyeIcon.setIconColor(Color.web(Themes.TEXT_MUTED));
        });

        pane.getChildren().addAll(pf, tf, eyePane);

        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        box.getChildren().addAll(label, pane);

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