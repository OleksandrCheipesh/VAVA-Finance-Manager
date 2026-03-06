package org.example.view.mainStages;

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
import org.example.view.templates.BaseView;
import org.example.viewModel.CreateCompanyViewModel;

import java.io.File;

/**
 *  Create Company window - View class
 */
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

    // Style constants
    private final String FIELD_STYLE = "-fx-background-color: #D4EDEC; -fx-control-inner-background: #D4EDEC; -fx-border-color: #6B8A89; -fx-border-width: 2.5; -fx-border-radius: 18; -fx-background-radius: 18; -fx-padding: 15; -fx-font-size: 22px; -fx-text-fill: #6B8A89; -fx-prompt-text-fill: #6B8A89;";

    private final String AREA_STYLE = "-fx-control-inner-background: #D4EDEC; -fx-background-color: #D4EDEC; -fx-background-radius: 18; -fx-border-color: #6B8A89; -fx-border-width: 2.5; -fx-border-radius: 18; -fx-font-size: 22px; -fx-text-fill: #6B8A89; -fx-prompt-text-fill: #6B8A89; -fx-padding: 10; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

    private final String COMBO_STYLE = "-fx-background-color: #D4EDEC; -fx-border-color: #6B8A89; -fx-border-width: 2.5; -fx-border-radius: 18; -fx-background-radius: 18; -fx-font-size: 22px; -fx-text-fill: #6B8A89; -fx-padding: 0; -fx-background-insets: 0;";

    // Initialize and assemble UI components and layouts
    @Override
    protected void setContent() {
        mainContainer = new VBox(20);
        mainContainer.setMaxHeight(Region.USE_PREF_SIZE);

        titleLabel = new Label("Company");
        nameField = new TextField(); nameField.setPromptText("Company name");

        // Description
        descArea = new TextArea();
        descArea.setWrapText(true);
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(4);
        descArea.setPrefHeight(100);
        descArea.setMinHeight(100);

        industryField = new TextField(); industryField.setPromptText("Industry");
        currencyCombo = new ComboBox<>(viewModel.getCurrencies()); currencyCombo.setPromptText("Currency");

        // Industry and Currency layout
        HBox indCurBox = new HBox(20);
        VBox indBox = createLabeledNode("Industry", industryField);
        VBox curBox = createLabeledNode("Currency", currencyCombo);
        HBox.setHgrow(indBox, Priority.ALWAYS); HBox.setHgrow(curBox, Priority.ALWAYS);
        indCurBox.getChildren().addAll(indBox, curBox);

        // Main Information
        VBox mainInfoSection = createSection("Main Information");
        mainInfoSection.getChildren().addAll(createLabeledNode("Company Name", nameField), createLabeledNode("Description", descArea), indCurBox);

        countryCombo = new ComboBox<>(viewModel.getCountries()); countryCombo.setPromptText("Country");
        cityField = new TextField(); cityField.setPromptText("City");

        // Country and City layout
        HBox countryCityBox = new HBox(20);
        VBox countryBox = createLabeledNode("Country", countryCombo);
        VBox cityBox = createLabeledNode("City", cityField);
        HBox.setHgrow(countryBox, Priority.ALWAYS); HBox.setHgrow(cityBox, Priority.ALWAYS);
        countryCityBox.getChildren().addAll(countryBox, cityBox);

        addressField = new TextField(); addressField.setPromptText("Address");

        // Location
        VBox locationSection = createSection("Location");
        locationSection.getChildren().addAll(countryCityBox, createLabeledNode("Address", addressField));

        // Logo
        logoPane = new StackPane();
        logoLabel = new Label("Choose from gallery");
        logoLabel.setStyle("-fx-text-fill: #6B8A89; -fx-font-size: 24px; -fx-font-weight: normal;");

        logoImageView = new ImageView();
        logoImageView.setFitWidth(270);
        logoImageView.setFitHeight(190);
        logoImageView.setPreserveRatio(true);

        logoPane.getChildren().addAll(logoLabel, logoImageView);
        VBox logoBox = createLabeledNode("Logo", logoPane);

        webField = new TextField(); webField.setPromptText("Website");
        taxField = new TextField(); taxField.setPromptText("ID");

        // Web and Tax
        VBox webTaxBox = new VBox(15);
        webTaxBox.getChildren().addAll(createLabeledNode("Website", webField), createLabeledNode("Tax ID", taxField));
        HBox.setHgrow(webTaxBox, Priority.ALWAYS);

        HBox otherContentBox = new HBox(20);
        otherContentBox.getChildren().addAll(logoBox, webTaxBox);

        VBox otherSection = createSection("Other");
        otherSection.getChildren().add(otherContentBox);

        registerBtn = new Button("Register");

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 20px; -fx-font-weight: bold;");

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

        VBox scrollContentWrapper = new VBox(mainContainer);
        scrollContentWrapper.setAlignment(Pos.TOP_CENTER);
        scrollContentWrapper.setPadding(new Insets(60, 0, 60, 0));

        scrollRoot = new ScrollPane(scrollContentWrapper);
        scrollRoot.setFitToWidth(true); scrollRoot.setFitToHeight(true);

        // Root Pane with image + overlay
        StackPane rootPane = new StackPane();
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/images/pen-and-notebook.png"));
            BackgroundImage bImg = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, true));
            rootPane.setBackground(new Background(bImg));
        } catch (Exception e) {}

        Region colorOverlay = new Region();
        colorOverlay.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(86, 212, 208, 0.85) 0%, rgba(7, 155, 151, 0.85) 93%);");

        rootPane.getChildren().addAll(colorOverlay, scrollRoot);
        scene = new Scene(rootPane);

        // Remove inner shadow and Scrollbars
        String css =
                ".text-area .scroll-pane { -fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; }" +
                        ".text-area .scroll-pane .viewport { -fx-background-color: transparent; }" +
                        ".text-area .scroll-pane .content { -fx-background-color: #D4EDEC; -fx-background-insets: 0; }" +
                        ".text-area .scroll-bar { -fx-shape: \" \"; -fx-padding: 0; -fx-background-color: transparent; -fx-pref-width: 0; -fx-pref-height: 0; }" +
                        ".text-area .scroll-bar:vertical, .text-area .scroll-bar:horizontal { -fx-opacity: 0; -fx-max-width: 0; -fx-max-height: 0; }";

        scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));
    }

    // Sizing, Alignment, Visual styles for container and it's children
    @Override
    protected void setStyle() {
        scrollRoot.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setMaxWidth(1100);
        mainContainer.setPrefWidth(1100);

        mainContainer.setPadding(new Insets(100, 120, 60, 120));
        mainContainer.setStyle("-fx-background-color: #F0F9F8; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        titleLabel.setStyle("-fx-font-size: 72px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        // Field styles
        nameField.setStyle(FIELD_STYLE);
        descArea.setStyle(AREA_STYLE);
        industryField.setStyle(FIELD_STYLE);
        currencyCombo.setStyle(COMBO_STYLE);
        countryCombo.setStyle(COMBO_STYLE);
        cityField.setStyle(FIELD_STYLE);
        addressField.setStyle(FIELD_STYLE);
        webField.setStyle(FIELD_STYLE);
        taxField.setStyle(FIELD_STYLE);

        double fixH = 70;
        double splitW = 420;

        Node[] all = {nameField, industryField, cityField, addressField, webField, taxField, currencyCombo, countryCombo};
        for (Node f : all) { if (f instanceof Region r) { r.setMinHeight(fixH); r.setPrefHeight(fixH); r.setMaxHeight(fixH); } }

        Node[] split = {industryField, currencyCombo, countryCombo, cityField};
        for (Node f : split) { if (f instanceof Region r) { r.setMinWidth(splitW); r.setPrefWidth(splitW); r.setMaxWidth(splitW); } }

        nameField.setMaxWidth(Double.MAX_VALUE);
        addressField.setMaxWidth(Double.MAX_VALUE);
        webField.setMaxWidth(Double.MAX_VALUE);
        taxField.setMaxWidth(Double.MAX_VALUE);

        logoPane.setPrefSize(280, 200);
        logoPane.setStyle("-fx-background-color: #D4EDEC; -fx-border-color: #6B8A89; -fx-border-width: 2.5; -fx-border-radius: 18; -fx-background-radius: 18;");

        registerBtn.setStyle("-fx-background-color: #56D4D0; -fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold; -fx-background-radius: 60; -fx-padding: 20 120;");
    }

    // Binding properties and attachment of listeners
    @Override
    protected void setLogic() {
        // MVVM Data Binding
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

        // Dynamic Description height
        descArea.textProperty().addListener((obs, old, val) -> {
            Platform.runLater(() -> {
                Node textNode = descArea.lookup(".text");
                if (textNode != null) {
                    double targetHeight = (val == null || val.isEmpty()) ? 100 : Math.max(100, textNode.getLayoutBounds().getHeight() + 25);
                    descArea.setPrefHeight(targetHeight);
                    descArea.setMinHeight(targetHeight);
                }
            });
        });

        registerBtn.setOnAction(e -> {
            nameField.setStyle(FIELD_STYLE);
            countryCombo.setStyle(COMBO_STYLE);
            currencyCombo.setStyle(COMBO_STYLE);
            errorLabel.setVisible(false);

            viewModel.registerCompany();

            boolean nameMissing = nameField.getText() == null || nameField.getText().trim().isEmpty();
            boolean countryMissing = countryCombo.getValue() == null;
            boolean currencyMissing = currencyCombo.getValue() == null;

            if (!nameMissing && !countryMissing && !currencyMissing) {
                navigateTo(new org.example.view.mainStages.DashBoardView());
            } else {
                String errorBorder = "-fx-border-color: #E74C3C;";

                if (nameMissing) nameField.setStyle(FIELD_STYLE + errorBorder);
                if (countryMissing) countryCombo.setStyle(COMBO_STYLE + errorBorder);
                if (currencyMissing) currencyCombo.setStyle(COMBO_STYLE + errorBorder);

                if (nameMissing) {
                    errorLabel.setText("Company Name is required !");
                } else if (countryMissing) {
                    errorLabel.setText("Country is required !");
                } else if (currencyMissing) {
                    errorLabel.setText("Currency is required !");
                }

                errorLabel.setVisible(true);
            }
        });

        // Button Hover Effects
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle("-fx-background-color: #4CB8B5; -fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold; -fx-background-radius: 60; -fx-padding: 20 120; -fx-cursor: hand;"));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle("-fx-background-color: #56D4D0; -fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold; -fx-background-radius: 60; -fx-padding: 20 120;"));

        // Logo Selection
        logoPane.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Company Logo");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(logoPane.getScene().getWindow());
            if (selectedFile != null) viewModel.logoFileProperty().set(selectedFile);
        });

        viewModel.logoFileProperty().addListener((obs, old, newValue) -> {
            if (newValue != null) {
                logoImageView.setImage(new Image(newValue.toURI().toString()));
                logoLabel.setVisible(false);
            }
        });

        // Logo Hover effects
        logoPane.setOnMouseEntered(e -> logoPane.setStyle("-fx-background-color: #C4E5E3; -fx-border-color: #6B8A89; -fx-border-width: 2.5; -fx-border-radius: 18; -fx-background-radius: 18; -fx-cursor: hand;"));
        logoPane.setOnMouseExited(e -> logoPane.setStyle("-fx-background-color: #D4EDEC; -fx-border-color: #6B8A89; -fx-border-width: 2.5; -fx-border-radius: 18; -fx-background-radius: 18;"));
    }

    // Adjusting ComboBoxes
    private void setupComboBoxStability(ComboBox<String> comboBox, String prompt, double width) {
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty || item == null ? prompt : item);
                setStyle("-fx-text-fill: #6B8A89; -fx-font-size: 22px; -fx-padding: 0 0 0 15; -fx-background-color: transparent;");
                setAlignment(Pos.CENTER_LEFT); setPrefHeight(68);
            }
        });

        comboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                Platform.runLater(() -> {
                    Node popup = comboBox.lookup(".combo-box-popup");

                    if (popup != null) {
                        popup.setStyle("-fx-effect: null; -fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 0;");

                        popup.setTranslateX(-1);

                        Node listView = popup.lookup(".list-view");

                        if (listView != null) {
                            listView.setStyle("-fx-background-color: #6B8A89; -fx-padding: 2.5; -fx-background-insets: 0; -fx-pref-width: " + (width + 2) + "px; -fx-max-width: " + (width + 2) + "px; -fx-background-radius: 18; -fx-border-radius: 18;");
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
                    setText(null); setStyle("-fx-background-color: #D4EDEC;");
                } else {
                    setText(item);

                    String cellBase = "-fx-background-color: #D4EDEC; -fx-text-fill: #6B8A89; -fx-font-size: 22px; -fx-padding: 10 15; -fx-background-insets: 0;";

                    setStyle(cellBase);

                    setOnMouseEntered(e -> setStyle(cellBase + "-fx-background-color: #C4E5E3; -fx-cursor: hand;"));
                    setOnMouseExited(e -> setStyle(cellBase));
                }
            }
        });
    }

    // Horizontal line to divide sections
    private VBox createSection(String title) {
        VBox section = new VBox(15);

        HBox separatorBox = new HBox(10);

        separatorBox.setAlignment(Pos.CENTER);

        Region l1 = new Region(); HBox.setHgrow(l1, Priority.ALWAYS); l1.setMinHeight(2.5); l1.setMaxHeight(2.5); l1.setStyle("-fx-background-color: #6B8A89;");
        Region l2 = new Region(); HBox.setHgrow(l2, Priority.ALWAYS); l2.setMinHeight(2.5); l2.setMaxHeight(2.5); l2.setStyle("-fx-background-color: #6B8A89;");

        Label sl = new Label(title); sl.setStyle("-fx-text-fill: #6B8A89; -fx-font-size: 28px; -fx-font-weight: bold;");

        separatorBox.getChildren().addAll(l1, sl, l2);
        section.getChildren().add(separatorBox);

        return section;
    }

    // Bold labels
    private VBox createLabeledNode(String labelText, Node node) {
        VBox box = new VBox(5);

        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #6B8A89; -fx-font-size: 20px; -fx-font-weight: bold;");

        box.getChildren().addAll(label, node);

        return box;
    }
}