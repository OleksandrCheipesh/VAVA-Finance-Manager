package org.example.view.authentication;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.view.mainStages.DashBoardView;
import org.example.view.templates.BaseView;
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

        // --- LEFT PANEL (Figma Image + Teal Overlay) ---
        leftPanel = new StackPane();

        // 1. Bottom Layer: The Image
        Region bgImage = new Region();
        // Make sure the filename matches what you put in the resources/images folder!
        String imageUrl = getClass().getResource("/images/pen-and-notebook.png").toExternalForm();
        bgImage.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover; -fx-background-position: center;");

        // 2. Middle Layer: The Semi-transparent Teal Overlay
        Region colorOverlay = new Region();
        colorOverlay.setStyle("-fx-background-color: rgba(22, 184, 173, 0.85);"); // 0.85 is the opacity

        // 3. Top Layer: Your Logo and Text
        VBox leftContent = new VBox(15);
        leftContent.setAlignment(Pos.CENTER);
        Circle logoCircle = new Circle(60, Color.web("#0E9B92"));
        Label brandName = new Label("FinaM");
        Label brandMotto = new Label("Your finances, your complete clarity");
        leftContent.getChildren().addAll(logoCircle, brandName, brandMotto);

        // Stack them all together
        leftPanel.getChildren().addAll(bgImage, colorOverlay, leftContent);

        // --- RIGHT PANEL (Form) ---
        rightPanel = new VBox();
        formBox = new VBox(15);
        buttonBox = new VBox(10);

        titleLabel = new Label("Registration");
        subtitleLabel = new Label("Create account to get started");

        nameField = new TextField();
        nameField.setPromptText("Name");

        surnameField = new TextField();
        surnameField.setPromptText("Surname");

        emailField = new TextField();
        emailField.setPromptText("E-mail");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        registerButton = new Button("Create Account");

        loginLink = new Hyperlink("Switch to Login");

        buttonBox.getChildren().addAll(registerButton, loginLink);

        messageLabel = new Label();
        messageLabel.textProperty().bind(viewModel.messageProperty());

        formBox.getChildren().addAll(
                titleLabel, subtitleLabel,
                nameField, surnameField, emailField, passwordField,
                buttonBox, messageLabel
        );

        rightPanel.getChildren().add(formBox);
        root.getChildren().addAll(leftPanel, rightPanel);
        scene = new Scene(root, 800, 500); // Set standard window size
    }

    @Override
    protected void setStyle() {
        // Layout Split
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        leftPanel.setPrefWidth(400);
        rightPanel.setPrefWidth(400);

        // Left Panel Style
        VBox leftContent = (VBox) leftPanel.getChildren().get(2);

        Label brandName = (Label) leftContent.getChildren().get(1);
        brandName.setFont(Font.font("System", FontWeight.BOLD, 36));
        brandName.setTextFill(Color.WHITE);

        Label brandMotto = (Label) leftContent.getChildren().get(2);
        brandMotto.setFont(Font.font("System", 14));
        brandMotto.setTextFill(Color.WHITE);

        // Right Panel Style
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setStyle("-fx-background-color: #F4FAFA;");
        formBox.setMaxWidth(300);

        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        subtitleLabel.setStyle("-fx-text-fill: #7f9e9c; -fx-padding: 0 0 10 0;");

        // Input Fields (Figma Style)
        String fieldStyle = "-fx-background-color: #DDF0EF; -fx-border-color: #B0D6D3; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;";
        nameField.setStyle(fieldStyle);
        surnameField.setStyle(fieldStyle);
        emailField.setStyle(fieldStyle);
        passwordField.setStyle(fieldStyle);

        // Button Style
        buttonBox.setAlignment(Pos.CENTER);
        registerButton.setPrefWidth(300);
        registerButton.setStyle("-fx-background-color: #2BD2C6; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");

        loginLink.setStyle("-fx-text-fill: #16B8AD; -fx-font-weight: bold;");

        // Message Label Style
        messageLabel.setStyle("-fx-font-size: 13px; -fx-wrap-text: true; -fx-alignment: center;");
    }

    @Override
    protected void setLogic() {
        registerButton.setOnAction(e ->
                viewModel.register(nameField.getText(), surnameField.getText(), emailField.getText(), passwordField.getText())
        );

        // Navigation requested by Project Leader
        loginLink.setOnAction(e -> navigateTo(new LoginView()));

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.contains("successful")) {
                messageLabel.setTextFill(Color.web("#10B981")); // Green
                navigateTo(new org.example.view.mainStages.CreateCompanyView());
            } else if (newVal != null && !newVal.isEmpty()) {
                messageLabel.setTextFill(Color.web("#E74C3C")); // Red
            }
        });
    }
}