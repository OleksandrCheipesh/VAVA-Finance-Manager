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
import org.example.viewModel.RegistrationViewModel;

public class RegistrationView extends BaseView {

    private final RegistrationViewModel viewModel = new RegistrationViewModel();

    private HBox root;
    private StackPane leftPanel;
    private VBox rightPanel, formBox, buttonBox;
    private TextField nameField, surnameField, emailField;
    private PasswordField passwordField;
    private Button registerButton;
    private Label titleLabel, subtitleLabel, messageLabel;
    private Hyperlink loginLink;

    @Override
    protected void setContent() {
        root = new HBox();

        // Left brand panel with logo and background image
        String imageUrl = getClass().getResource("/images/pen-and-notebook.png").toExternalForm();
        leftPanel = UIFactory.brandPanel(imageUrl);

        // Right panel with registration form
        rightPanel = new VBox();
        formBox    = new VBox(15);
        buttonBox  = new VBox(10);

        titleLabel    = new Label("Registration");
        subtitleLabel = new Label("Create account to get started");

        // Input fields
        nameField      = UIFactory.inputField("Name");
        surnameField   = UIFactory.inputField("Surname");
        emailField     = UIFactory.inputField("E-mail");
        passwordField  = UIFactory.passwordField("Password");

        // Submit button and navigation link
        registerButton = UIFactory.primaryButton("Create Account", Themes.FORM_MAX_WIDTH);

        registerButton.setOnMousePressed(e -> {
            registerButton.setScaleX(0.98);
            registerButton.setScaleY(0.98);
        });

        registerButton.setOnMouseReleased(e -> {
            registerButton.setScaleX(1.0);
            registerButton.setScaleY(1.0);
        });

        loginLink = UIFactory.navLink("Switch to Login");

        // Message label for success/error feedback
        messageLabel   = UIFactory.messageLabel();
        messageLabel.textProperty().bind(viewModel.messageProperty());

        buttonBox.getChildren().addAll(registerButton, loginLink);
        formBox.getChildren().addAll(titleLabel, subtitleLabel, nameField, surnameField, emailField, passwordField, buttonBox, messageLabel);
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
        // Send registration data to ViewModel
        registerButton.setOnAction(e ->
                viewModel.register(nameField.getText(), surnameField.getText(), emailField.getText(), passwordField.getText())
        );

        // Navigate back to login
        loginLink.setOnAction(e -> navigateTo(new LoginView()));

        // React to registration result
        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.contains("successful")) {
                messageLabel.setTextFill(Color.web(Themes.TEXT_SUCCESS));
                navigateTo(new CreateCompanyView());
            } else if (newVal != null && !newVal.isEmpty()) {
                messageLabel.setTextFill(Color.web(Themes.TEXT_ERROR));
            }
        });
    }
}