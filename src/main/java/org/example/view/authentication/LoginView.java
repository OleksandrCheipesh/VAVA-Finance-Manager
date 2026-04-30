package org.example.view.authentication;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.SessionManager;
import org.example.view.mainStages.DashBoardView;
import org.example.view.templates.BaseView;
import org.example.view.templates.I18n;
import org.example.view.templates.Themes;
import org.example.view.templates.UIFactory;
import org.example.viewModel.LoginViewModel;

public class LoginView extends BaseView {

    private final LoginViewModel viewModel = new LoginViewModel();

    private HBox root;
    private StackPane leftPanel;
    private VBox rightPanel, formBox, buttonBox;
    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label titleLabel, subtitleLabel, messageLabel;
    private Hyperlink registerLink;

    @Override
    protected void setContent() {
        root = new HBox();

        // Left brand panel with logo and background image
        String imageUrl = getClass().getResource("/images/pen-and-notebook.png").toExternalForm();
        leftPanel = UIFactory.brandPanel(imageUrl);

        // Right panel with login form
        rightPanel = new VBox();
        formBox    = new VBox(15);
        buttonBox  = new VBox(10);

        titleLabel    = new Label("Log in");
        subtitleLabel = new Label("Log in to your account");

        // Input fields
        emailField    = UIFactory.inputField("Email");
        passwordField = UIFactory.passwordField("Password");

        // Submit button and navigation link
        loginButton  = UIFactory.primaryButton("Log in", Themes.FORM_MAX_WIDTH);

        loginButton.setOnMousePressed(e -> {
            loginButton.setScaleX(0.98);
            loginButton.setScaleY(0.98);
        });

        loginButton.setOnMouseReleased(e -> {
            loginButton.setScaleX(1.0);
            loginButton.setScaleY(1.0);
        });

        registerLink = UIFactory.navLink("Switch to Registration");

        Hyperlink forgotPasswordLink = UIFactory.navLink("Forgot Password?");
        forgotPasswordLink.setOnAction(e -> navigateTo(new ResetPasswordView()));

        // Message label for success/error feedback
        messageLabel = UIFactory.messageLabel();
        messageLabel.textProperty().bind(viewModel.messageProperty());

        buttonBox.getChildren().addAll(loginButton, forgotPasswordLink, registerLink);
        formBox.getChildren().addAll(titleLabel, subtitleLabel, emailField, passwordField, buttonBox, messageLabel);
        rightPanel.getChildren().add(formBox);
        root.getChildren().addAll(leftPanel, rightPanel);

        scene = new Scene(root, 800, 500);
    }

    @Override
    protected void setStyle() {
        // Split screen 50/50
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
        // Send credentials to ViewModel
        loginButton.setOnAction(e ->
                viewModel.login(emailField.getText(), passwordField.getText())
        );

        // Navigate to registration
        registerLink.setOnAction(e -> navigateTo(new RegistrationView()));

        // React to login result
        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.contains("successful")) {
                messageLabel.setTextFill(Color.web(Themes.TEXT_SUCCESS));
                I18n.setLanguage(SessionManager.getInstance().getLanguage());
                navigateTo(new DashBoardView());
            } else if (newVal != null && !newVal.isEmpty()) {
                messageLabel.setTextFill(Color.web(Themes.TEXT_ERROR));
            }
        });
    }
}