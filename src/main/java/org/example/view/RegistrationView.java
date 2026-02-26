package org.example.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.viewModel.RegistrationViewModel;
public class RegistrationView extends Application {

    private final RegistrationViewModel viewModel = new RegistrationViewModel();

    @Override
    public void start(Stage stage) {

        // main container
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER); // Центруємо по вертикалі та горизонталі
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: white;");

        // containers for forms
        VBox formBox = new VBox(20);
        formBox.setMaxWidth(400);
        formBox.setAlignment(Pos.CENTER);

        // labels
        Label titleLabel = new Label("Registration");
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 32));
        titleLabel.setStyle("-fx-text-fill: #000000;");

        // login
        TextField loginField = new TextField();
        loginField.setPromptText("Login");
        loginField.setPrefHeight(45);
        loginField.setMaxWidth(Double.MAX_VALUE);
        loginField.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 8 15 8 15;"
        );

        // email
        TextField emailField = new TextField();
        emailField.setPromptText("E-mail");
        emailField.setPrefHeight(45);
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 8 15 8 15;"
        );

        // password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(45);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 8 15 8 15;"
        );

        // button Next
        Button registerButton = new Button("Next");
        registerButton.setPrefWidth(200);
        registerButton.setPrefHeight(44);
        registerButton.setStyle(
                        "-fx-background-color: #0ABAB5;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-cursor: hand;"
        );


        // hover effect for button
        registerButton.setOnMouseEntered(e ->
                registerButton.setStyle(
                        "-fx-background-color: #4F52E0;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 12px;" +
                                "-fx-cursor: hand;"
                )
        );

        registerButton.setOnMouseExited(e ->
                registerButton.setStyle(
                        "-fx-background-color: #0ABAB5;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 12px;" +
                                "-fx-cursor: hand;"
                )
        );

        // massage label
        Label messageLabel = new Label();
        messageLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-wrap-text: true;" +
                        "-fx-alignment: center;"
        );
        messageLabel.setMaxWidth(300);

        // binding with ViewModel
        messageLabel.textProperty().bind(viewModel.messageProperty());

        // changing of the colour depend on massage
        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.contains("successful")) {
                messageLabel.setStyle(
                        "-fx-text-fill: #10B981;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-alignment: center;"
                );
                openDashboard(stage);
            } else if (newVal != null && !newVal.isEmpty()) {
                messageLabel.setStyle(
                        "-fx-text-fill: #EF4444;" +
                                "-fx-font-size: 13px;" +
                                "-fx-wrap-text: true;" +
                                "-fx-alignment: center;"
                );
            }
        });

        // creating the user via ViewModel
        registerButton.setOnAction(e -> {
            viewModel.register(
                    loginField.getText(),
                    emailField.getText(),
                    passwordField.getText()
            );
        });

        // container for button
        VBox buttonBox = new VBox(registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));

        //adding all elements to form
        formBox.getChildren().addAll(
                titleLabel,
                loginField,
                emailField,
                passwordField,
                buttonBox,
                messageLabel
        );

        // giving the form to container
        root.getChildren().add(formBox);

        // creating the scene
        Scene scene = new Scene(root);

        // setting scene
        stage.setTitle("FinaM");
        stage.setScene(scene);
        stage.setMaximized(true);

        stage.show();
    }

    // opening of the dash board
    private void openDashboard(Stage stage) {
        DashBoardView dashBoardView = new DashBoardView();
        dashBoardView.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}