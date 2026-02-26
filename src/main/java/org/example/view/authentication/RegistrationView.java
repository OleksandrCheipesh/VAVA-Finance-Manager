package org.example.view.authentication;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.view.mainStages.DashBoardView;
import org.example.view.templates.BaseView;
import org.example.viewModel.RegistrationViewModel;

public class RegistrationView extends BaseView {

    private final RegistrationViewModel viewModel = new RegistrationViewModel();

    // fields
    private VBox root, formBox, buttonBox;
    private TextField loginField, emailField;
    private PasswordField passwordField;
    private Button registerButton;
    private Label titleLabel, messageLabel;


    @Override
    protected void setContent() {
        root = new VBox(20);
        formBox = new VBox(20);
        buttonBox = new VBox();

        titleLabel = new Label("Registration");

        loginField = new TextField();
        loginField.setPromptText("Login");

        emailField = new TextField();
        emailField.setPromptText("E-mail");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        registerButton = new Button("Next");
        buttonBox.getChildren().add(registerButton);

        messageLabel = new Label();
        messageLabel.textProperty().bind(viewModel.messageProperty());

        formBox.getChildren().addAll(
                titleLabel,
                loginField,
                emailField,
                passwordField,
                buttonBox,
                messageLabel
        );

        root.getChildren().add(formBox);
        scene = new Scene(root);
    }

    @Override
    protected void setStyle() {
        // root
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: white;");

        // form box
        formBox.setMaxWidth(400);
        formBox.setAlignment(Pos.CENTER);

        // title
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 32));
        titleLabel.setStyle("-fx-text-fill: #000000;");

        // fields
        String fieldStyle =
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 8 15 8 15;";

        loginField.setPrefHeight(45);
        loginField.setMaxWidth(Double.MAX_VALUE);
        loginField.setStyle(fieldStyle);

        emailField.setPrefHeight(45);
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle(fieldStyle);

        passwordField.setPrefHeight(45);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle(fieldStyle);

        // button
        registerButton.setPrefWidth(200);
        registerButton.setPrefHeight(44);
        registerButton.setStyle(buttonDefaultStyle());

        // button box
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));

        // message label
        messageLabel.setMaxWidth(300);
        messageLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-wrap-text: true;" +
                        "-fx-alignment: center;"
        );
    }

    @Override
    protected void setLogic() {
        // hover effect
        registerButton.setOnMouseEntered(e -> registerButton.setStyle(buttonHoverStyle()));
        registerButton.setOnMouseExited(e -> registerButton.setStyle(buttonDefaultStyle()));

        // register action
        registerButton.setOnAction(e ->
                viewModel.register(
                        loginField.getText(),
                        emailField.getText(),
                        passwordField.getText()
                )
        );

        // message listener
        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.contains("successful")) {
                messageLabel.setStyle(
                        "-fx-text-fill: #10B981;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-alignment: center;"
                );
                navigateTo(new DashBoardView());
            } else if (newVal != null && !newVal.isEmpty()) {
                messageLabel.setStyle(
                        "-fx-text-fill: #EF4444;" +
                                "-fx-font-size: 13px;" +
                                "-fx-wrap-text: true;" +
                                "-fx-alignment: center;"
                );
            }
        });
    }

    // BUTTON STYLES
    private String buttonDefaultStyle() {
        return  "-fx-background-color: #0ABAB5;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12px;" +
                "-fx-cursor: hand;";
    }

    private String buttonHoverStyle() {
        return  "-fx-background-color: #4F52E0;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12px;" +
                "-fx-cursor: hand;";
    }
}