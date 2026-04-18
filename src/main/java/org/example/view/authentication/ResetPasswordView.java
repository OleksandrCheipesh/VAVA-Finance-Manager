package org.example.view.authentication;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.view.templates.BaseView;
import org.example.view.templates.Themes;
import org.example.view.templates.UIFactory;

public class ResetPasswordView extends BaseView {

    private HBox root;
    private StackPane leftPanel;
    private VBox rightPanel, formBox, buttonBox;
    private TextField emailField;
    private PasswordField oldPasswordField, newPasswordField, confirmPasswordField;
    private Button resetButton;
    private Label titleLabel, subtitleLabel, messageLabel;
    private Hyperlink backToLoginLink;

    @Override
    protected void setContent() {
        root = new HBox();

        String imageUrl = getClass().getResource("/images/pen-and-notebook.png").toExternalForm();
        leftPanel = UIFactory.brandPanel(imageUrl);

        rightPanel = new VBox();
        formBox = new VBox(15);
        buttonBox = new VBox(10);

        titleLabel = new Label("Reset Password");
        subtitleLabel = new Label("Enter your email and new password");

        emailField = UIFactory.inputField("Email");
        oldPasswordField = UIFactory.passwordField("Current Password");
        newPasswordField = UIFactory.passwordField("New Password");
        confirmPasswordField = UIFactory.passwordField("Confirm Password");

        resetButton = UIFactory.primaryButton("Reset Password", Themes.FORM_MAX_WIDTH);
        backToLoginLink = UIFactory.navLink("Back to Login");

        messageLabel = UIFactory.messageLabel();

        buttonBox.getChildren().addAll(resetButton, backToLoginLink);
        formBox.getChildren().addAll(titleLabel, subtitleLabel, emailField, oldPasswordField, newPasswordField, confirmPasswordField, buttonBox, messageLabel);
        rightPanel.getChildren().add(formBox);
        root.getChildren().addAll(leftPanel, rightPanel);

        scene = new Scene(root, 800, 500);
    }

    @Override
    protected void setStyle() {
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        leftPanel.setPrefWidth(Themes.PANEL_WIDTH);
        rightPanel.setPrefWidth(Themes.PANEL_WIDTH);

        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setStyle("-fx-background-color: " + Themes.BG_RIGHT + ";");
        formBox.setMaxWidth(Themes.FORM_MAX_WIDTH);

        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        subtitleLabel.setStyle("-fx-text-fill: " + Themes.TEXT_SUBTITLE + "; -fx-padding: 0 0 10 0;");

        buttonBox.setAlignment(Pos.CENTER);
    }

    @Override
    protected void setLogic() {
        backToLoginLink.setOnAction(e -> navigateTo(new LoginView()));

        resetButton.setOnAction(e -> {
            String email = emailField.getText();
            String oldPw = oldPasswordField.getText();
            String newPw = newPasswordField.getText();
            String confirmPw = confirmPasswordField.getText();

            if (email.isEmpty() || oldPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
                messageLabel.setText("All fields are required.");
                messageLabel.setTextFill(Color.web(Themes.TEXT_ERROR));
                return;
            }

            if (!newPw.equals(confirmPw)) {
                messageLabel.setText("Passwords do not match.");
                messageLabel.setTextFill(Color.web(Themes.TEXT_ERROR));
                return;
            }

            try {
                org.example.model.database.service.UserService userService = new org.example.model.database.service.UserService();
                java.util.Optional<org.example.model.database.entity.User> userOpt = userService.getUserByEmail(email);

                if (userOpt.isPresent()) {
                    org.example.model.database.entity.User user = userOpt.get();
                    if (!org.example.model.PasswordUtil.verify(oldPw, user.getPasswordHash())) {
                        messageLabel.setText("Incorrect current password.");
                        messageLabel.setTextFill(Color.web(Themes.TEXT_ERROR));
                        return;
                    }
                    user.setPasswordHash(org.example.model.PasswordUtil.hash(newPw));
                    userService.updateUser(user);
                    messageLabel.setText("Password successfully reset!");
                    messageLabel.setTextFill(Color.web(Themes.TEXT_SUCCESS));
                } else {
                    messageLabel.setText("User with this email not found.");
                    messageLabel.setTextFill(Color.web(Themes.TEXT_ERROR));
                }
            } catch (Exception ex) {
                messageLabel.setText("Failed to reset password.");
                messageLabel.setTextFill(Color.web(Themes.TEXT_ERROR));
            }
        });
    }
}

