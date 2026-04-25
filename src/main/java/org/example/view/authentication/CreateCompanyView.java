package org.example.view.authentication;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.example.view.mainStages.DashBoardView;
import org.example.view.templates.BaseView;
import org.example.view.templates.Themes;
import org.example.view.templates.UIFactory;
import org.example.viewModel.CreateCompanyViewModel;
import org.example.logging.AppLog;

import java.io.File;
import java.sql.SQLException;

public class CreateCompanyView extends BaseView {

    private final CreateCompanyViewModel viewModel = new CreateCompanyViewModel();

    private ScrollPane scrollRoot;
    private VBox mainContainer;
    private Label titleLabel;
    private TextField nameField, industryField, cityField, addressField, webField, taxField;
    private TextArea descArea;
    private ComboBox<String> currencyCombo, countryCombo;
    private Button registerBtn;
    private Label errorLabel;
    private StackPane logoPane;
    private ImageView logoImageView;
    private Label logoLabel;


    @Override
    protected void setContent() {
        mainContainer = new VBox(20);
        mainContainer.setMaxHeight(Region.USE_PREF_SIZE);

        titleLabel = new Label("Company");

        // Main Information section fields
        nameField = UIFactory.largeInputField("Company name");
        descArea  = UIFactory.largeTextArea("Description", 4);

        industryField = UIFactory.largeInputField("Industry");
        currencyCombo = UIFactory.largeComboBox("Currency");
        currencyCombo.setItems(viewModel.getCurrencies());

        // Industry and Currency side by side
        HBox indCurBox = new HBox(20);
        VBox indBox = createLabeledNode("Industry", industryField);
        VBox curBox = createLabeledNode("Currency", currencyCombo);
        HBox.setHgrow(indBox, Priority.ALWAYS);
        HBox.setHgrow(curBox, Priority.ALWAYS);
        indCurBox.getChildren().addAll(indBox, curBox);

        VBox mainInfoSection = createSection("Main Information");
        mainInfoSection.getChildren().addAll(
                createLabeledNode("Company Name", nameField),
                createLabeledNode("Description", descArea),
                indCurBox
        );

        // Location section fields
        countryCombo = UIFactory.largeComboBox("Country");
        countryCombo.setItems(viewModel.getCountries());
        cityField = UIFactory.largeInputField("City");

        // Country and City side by side
        HBox countryCityBox = new HBox(20);
        VBox countryBox = createLabeledNode("Country", countryCombo);
        VBox cityBox    = createLabeledNode("City", cityField);
        HBox.setHgrow(countryBox, Priority.ALWAYS);
        HBox.setHgrow(cityBox, Priority.ALWAYS);
        countryCityBox.getChildren().addAll(countryBox, cityBox);

        addressField = UIFactory.largeInputField("Address");

        VBox locationSection = createSection("Location");
        locationSection.getChildren().addAll(
                countryCityBox,
                createLabeledNode("Address", addressField)
        );

        // Logo picker
        logoPane  = new StackPane();
        logoLabel = new Label("Choose from gallery");
        logoLabel.setStyle("-fx-text-fill: " + Themes.TEXT_PRIMARY + "; -fx-font-size: " + Themes.FONT_LOGO_LABEL + "px;");
        logoImageView = new ImageView();
        logoImageView.setFitWidth(180);
        logoImageView.setFitHeight(140);
        logoImageView.setPreserveRatio(true);
        logoPane.getChildren().addAll(logoLabel, logoImageView);

        // Website and Tax ID fields stacked vertically
        webField = UIFactory.largeInputField("Website");
        taxField = UIFactory.largeInputField("ID");

        VBox webTaxBox = new VBox(15);
        webTaxBox.getChildren().addAll(
                createLabeledNode("Website", webField),
                createLabeledNode("Tax ID", taxField)
        );
        HBox.setHgrow(webTaxBox, Priority.ALWAYS);

        // Logo and web/tax side by side
        HBox otherContentBox = new HBox(20);
        otherContentBox.getChildren().addAll(createLabeledNode("Logo", logoPane), webTaxBox);

        VBox otherSection = createSection("Other");
        otherSection.getChildren().add(otherContentBox);

        // Register button and validation error label
        registerBtn = new Button("Register");
        errorLabel  = new Label();
        errorLabel.setStyle("-fx-text-fill: " + Themes.TEXT_ERROR + "; -fx-font-size: " + Themes.FONT_ERROR_LARGE + "px; -fx-font-weight: bold;");
        errorLabel.setWrapText(true);
        errorLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setMinHeight(90);
        errorLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
        errorLabel.setMinWidth(Region.USE_PREF_SIZE);
        errorLabel.setMaxWidth(1100);
        errorLabel.setEllipsisString("");
        errorLabel.setVisible(false);

        VBox.setMargin(registerBtn, new Insets(60, 0, 0, 0));
        mainContainer.getChildren().addAll(titleLabel, mainInfoSection, locationSection, otherSection, registerBtn, errorLabel);

        // Wrap in scroll pane centered on screen
        VBox scrollContentWrapper = new VBox(mainContainer);
        scrollContentWrapper.setAlignment(Pos.TOP_CENTER);
        scrollContentWrapper.setPadding(new Insets(60, 0, 60, 0));

        scrollRoot = new ScrollPane(scrollContentWrapper);
        scrollRoot.setFitToWidth(true);
        scrollRoot.setFitToHeight(true);
        scrollRoot.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollRoot.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Background image with teal gradient overlay
        StackPane rootPane = new StackPane();
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/images/pen-and-notebook.png"));
            BackgroundImage bImg = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(1.0, 1.0, true, true, false, true));
            rootPane.setBackground(new Background(bImg));
        } catch (Exception ignored) {}

        Region colorOverlay = new Region();
        colorOverlay.setStyle("-fx-background-color: " + Themes.PRIMARY_GRADIENT + ";");
        rootPane.getChildren().addAll(colorOverlay, scrollRoot);
        scene = new Scene(rootPane);

        // Hide internal scrollbars of the TextArea
        String css =
                ".text-area .scroll-pane { -fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; }" +
                        ".text-area .scroll-pane .viewport { -fx-background-color: transparent; }" +
                        ".text-area .scroll-pane .content { -fx-background-color: " + Themes.BG_FIELD_LARGE + "; -fx-background-insets: 0; }" +
                        ".text-area .scroll-bar { -fx-shape: \" \"; -fx-padding: 0; -fx-background-color: transparent; -fx-pref-width: 0; -fx-pref-height: 0; }" +
                        ".text-area .scroll-bar:vertical, .text-area .scroll-bar:horizontal { -fx-opacity: 0; -fx-max-width: 0; -fx-max-height: 0; }";
        scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            var logger = AppLog.getLogger(CreateCompanyView.class);
            logger.warn("Global CSS not found for CreateCompanyView: {}", e.getMessage());
        }
    }

    @Override
    protected void setStyle() {
        scrollRoot.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Central white card
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setMaxWidth(800);
        mainContainer.setPrefWidth(800);
        mainContainer.setPadding(new Insets(50, 60, 40, 60));
        mainContainer.setStyle(
                "-fx-background-color: #F0F9F8;" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);"
        );

        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        // Apply fixed height to all input fields and combos
        double fixH   = Themes.FIELD_HEIGHT;
        double splitW = Themes.FIELD_SPLIT_W;

        Node[] allFields = { nameField, industryField, cityField, addressField, webField, taxField, currencyCombo, countryCombo };
        for (Node f : allFields) {
            if (f instanceof Region r) { r.setMinHeight(fixH); r.setPrefHeight(fixH); r.setMaxHeight(fixH); }
        }

        // Fixed width for split (two-column) fields
        double exactSplitW = 330;

        Node[] splitFields = { industryField, currencyCombo, countryCombo, cityField };

        for (Node f : splitFields) {
            if (f instanceof Region r) {
                r.setMinWidth(exactSplitW);
                r.setPrefWidth(exactSplitW);
                r.setMaxWidth(exactSplitW);
            }
        }

        // Full-width fields
        nameField.setMaxWidth(Double.MAX_VALUE);
        addressField.setMaxWidth(Double.MAX_VALUE);
        webField.setMaxWidth(Double.MAX_VALUE);
        taxField.setMaxWidth(Double.MAX_VALUE);

        // Logo pane matches field height
        logoPane.setPrefSize(200, 48);
        logoPane.setStyle(
                "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                        "-fx-border-color: " + Themes.BORDER_LARGE + ";" +
                        "-fx-border-width: 1; -fx-border-radius: 18; -fx-background-radius: 18;"
        );

        String base  = "-fx-background-color: " + Themes.BTN_PRIMARY + "; -fx-text-fill: white; -fx-font-size: " + Themes.FONT_BTN_LARGE + "px; -fx-font-weight: bold; -fx-background-radius: 60; -fx-padding: 12 80; -fx-cursor: hand;";
        registerBtn.setStyle(base);
    }

    @Override
    protected void setLogic() {
        // Bind fields to ViewModel properties
        nameField.textProperty().bindBidirectional(viewModel.companyNameProperty());
        descArea.textProperty().bindBidirectional(viewModel.descriptionProperty());
        industryField.textProperty().bindBidirectional(viewModel.industryProperty());
        currencyCombo.valueProperty().bindBidirectional(viewModel.currencyProperty());
        countryCombo.valueProperty().bindBidirectional(viewModel.countryProperty());
        cityField.textProperty().bindBidirectional(viewModel.cityProperty());
        addressField.textProperty().bindBidirectional(viewModel.addressProperty());
        webField.textProperty().bindBidirectional(viewModel.websiteProperty());
        taxField.textProperty().bindBidirectional(viewModel.taxIdProperty());

        setupComboBoxStability(currencyCombo, "Currency", 420);
        setupComboBoxStability(countryCombo, "Country", 420);

        // Grow description area dynamically as user types
        descArea.textProperty().addListener((obs, old, val) -> Platform.runLater(() -> {
            Node textNode = descArea.lookup(".text");
            if (textNode != null) {
                double h = (val == null || val.isEmpty()) ? 100 : Math.max(100, textNode.getLayoutBounds().getHeight() + 25);
                descArea.setPrefHeight(h);
                descArea.setMinHeight(h);
            }
        }));

        registerBtn.setOnMousePressed(e -> {
            registerBtn.setScaleX(0.98);
            registerBtn.setScaleY(0.98);
        });

        registerBtn.setOnMouseReleased(e -> {
            registerBtn.setScaleX(1.0);
            registerBtn.setScaleY(1.0);
        });

        // Validate required fields on register
        registerBtn.setOnAction(e -> {
            nameField.setStyle(UIFactory.LARGE_FIELD_STYLE);
            countryCombo.setStyle(UIFactory.LARGE_COMBO_STYLE);
            currencyCombo.setStyle(UIFactory.LARGE_COMBO_STYLE);
            errorLabel.setVisible(false);

            boolean nameMissing     = nameField.getText() == null || nameField.getText().trim().isEmpty();
            boolean countryMissing  = countryCombo.getValue() == null;
            boolean currencyMissing = currencyCombo.getValue() == null;

            if (!nameMissing && !countryMissing && !currencyMissing) {
                try {
                    viewModel.registerCompany();
                    navigateTo(new DashBoardView());
                } catch (SQLException ex) {
                    errorLabel.setText("Failed to save company. Please try again.");
                    errorLabel.setVisible(true);
                }
            } else {
                // Highlight invalid fields with red border
                String errorBorder = "-fx-border-color: " + Themes.TEXT_ERROR + ";";
                if (nameMissing)     nameField.setStyle(UIFactory.LARGE_FIELD_STYLE + errorBorder);
                if (countryMissing)  countryCombo.setStyle(UIFactory.LARGE_COMBO_STYLE + errorBorder);
                if (currencyMissing) currencyCombo.setStyle(UIFactory.LARGE_COMBO_STYLE + errorBorder);

                if (nameMissing)          errorLabel.setText("Company Name is required !");
                else if (countryMissing)  errorLabel.setText("Country is required !");
                else if (currencyMissing) errorLabel.setText("Currency is required !");

                errorLabel.setVisible(true);
            }
        });


        // Open file chooser on logo click
        logoPane.setOnMouseClicked(event -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Company Logo");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(logoPane.getScene().getWindow());
            if (file != null) viewModel.logoFileProperty().set(file);
        });

        // Show selected logo image and hide placeholder label
        viewModel.logoFileProperty().addListener((obs, old, newValue) -> {
            if (newValue != null) {
                logoImageView.setImage(new Image(newValue.toURI().toString()));
                logoLabel.setVisible(false);
            }
        });

        // Logo pane hover effect
        String logoBase  = "-fx-background-color: " + Themes.BG_FIELD_LARGE + "; -fx-border-color: " + Themes.BORDER_LARGE + "; -fx-border-width: 1; -fx-border-radius: 18; -fx-background-radius: 18;";
        String logoHover = "-fx-background-color: " + Themes.BG_FIELD_HOVER + "; -fx-border-color: " + Themes.BORDER_LARGE + "; -fx-border-width: 1; -fx-border-radius: 18; -fx-background-radius: 18; -fx-cursor: hand;";
        logoPane.setOnMouseEntered(e -> logoPane.setStyle(logoHover));
        logoPane.setOnMouseExited(e -> logoPane.setStyle(logoBase));
    }

    // Wraps a node with a bold label above it
    private VBox createLabeledNode(String labelText, Node node) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: " + Themes.TEXT_PRIMARY + "; -fx-font-size: " + Themes.FONT_LABEL + "px; -fx-font-weight: bold;");
        VBox box = new VBox(5);
        box.getChildren().addAll(label, node);
        return box;
    }

    // Section header with two horizontal lines and a centered title
    private VBox createSection(String title) {
        VBox section = new VBox(15);
        HBox separatorBox = new HBox(10);
        separatorBox.setAlignment(Pos.CENTER);

        Region l1 = new Region(); HBox.setHgrow(l1, Priority.ALWAYS); l1.setMinHeight(1); l1.setMaxHeight(1); l1.setStyle("-fx-background-color: " + Themes.BORDER_LARGE + ";");
        Region l2 = new Region(); HBox.setHgrow(l2, Priority.ALWAYS); l2.setMinHeight(1); l2.setMaxHeight(1); l2.setStyle("-fx-background-color: " + Themes.BORDER_LARGE + ";");
        Label sl = new Label(title);
        sl.setStyle("-fx-text-fill: " + Themes.TEXT_PRIMARY + "; -fx-font-size: " + Themes.FONT_SECTION_TITLE + "px; -fx-font-weight: bold;");

        separatorBox.getChildren().addAll(l1, sl, l2);
        section.getChildren().add(separatorBox);
        return section;
    }

    // Styles the combo box button cell, dropdown list, and hover effects
    private void setupComboBoxStability(ComboBox<String> comboBox, String prompt, double width) {
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? prompt : item);
                setStyle("-fx-text-fill: " + Themes.TEXT_PRIMARY + "; -fx-font-size: " + Themes.FONT_FIELD + "px; -fx-padding: 0 0 0 10; -fx-background-color: transparent;");
                setAlignment(Pos.CENTER_LEFT);
                setPrefHeight(48);
            }
        });

        comboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                Platform.runLater(() -> {
                    Node popup = comboBox.lookup(".combo-box-popup");

                    if (popup != null) {
                        popup.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

                        popup.setTranslateX(-1);
                        Node listView = popup.lookup(".list-view");

                        if (listView != null) {
                            listView.setStyle(
                                    "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                                            "-fx-border-color: " + Themes.BORDER_LARGE + ";" +
                                            "-fx-border-width: 1;" +
                                            "-fx-pref-width: " + (width + 2) + "px;" +
                                            "-fx-background-radius: 18; -fx-border-radius: 18;"
                            );
                        }
                    }
                });
            }
        });

        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + Themes.BG_FIELD_LARGE + ";");
                } else {
                    setText(item);
                    String baseStyle = "-fx-background-color: " + Themes.BG_FIELD_LARGE + "; -fx-text-fill: " + Themes.TEXT_PRIMARY + "; -fx-font-size: " + Themes.FONT_FIELD + "px;";
                    String hoverStyle = "-fx-background-color: " + Themes.BG_FIELD_HOVER + "; -fx-text-fill: " + Themes.TEXT_PRIMARY + "; -fx-font-size: " + Themes.FONT_FIELD + "px; -fx-cursor: hand;";

                    setStyle(baseStyle);
                    setOnMouseEntered(e -> setStyle(hoverStyle));
                    setOnMouseExited(e -> setStyle(baseStyle));
                }
            }
        });
    }
}