package org.example.view.mainStages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.User;
import org.example.model.validation.CompanyValExept;
import org.example.view.templates.*;
import org.example.viewModel.SettingsViewModel;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class SettingsView extends BaseView {

    private final SettingsViewModel viewModel = new SettingsViewModel();

    private VBox contentArea;
    private TextField companyNameField;
    private ComboBox<String> countryBox;
    private ComboBox<String> currencyBox;
    private ComboBox<String> industryBox;
    private Button saveCompanyBtn;

    private Button enBtn;
    private Button skBtn;
    private ComboBox<String> dateBox;
    private Button applyPrefsBtn;

    private TableView<User> table;
    private Button addUserBtn;

    private PasswordField currentPassField;
    private PasswordField newPassField;
    private PasswordField confirmPassField;
    private Button savePassBtn;

    @Override
    protected void setContent() {
        BorderPane root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);

        Label title = new Label(I18n.t("Settings"));
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Settings")));

        Label subtitle = new Label(I18n.t("Configure your organization's parameters and customize your portal experience."));
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        I18n.language.addListener((obs, o, v) -> subtitle.setText(I18n.t("Configure your organization's parameters and customize your portal experience.")));

        titleBox.getChildren().addAll(title, subtitle);
        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));
        topBar.getChildren().add(titleBox);

        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        HBox topRow = new HBox(25);
        VBox companyProfile = buildCompanyProfile();
        HBox.setHgrow(companyProfile, Priority.ALWAYS);
        VBox preferences = buildPreferences();
        preferences.setMinWidth(300);
        topRow.getChildren().addAll(companyProfile, preferences);

        VBox userManagement = buildUserManagement();
        VBox security = buildSecurity();

        contentArea.getChildren().addAll(topRow, userManagement, security);

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        mainContainer.getChildren().addAll(topBar, scrollPane);
        root.setCenter(mainContainer);

        scene = new Scene(root);

        try {
            var tableCss = getClass().getResource("/styles/table.css");
            var globalCss = getClass().getResource("/styles/global.css");
            if (tableCss != null) scene.getStylesheets().add(tableCss.toExternalForm());
            if (globalCss != null) scene.getStylesheets().add(globalCss.toExternalForm());
        } catch (Exception e) {
            System.out.println("Warning: CSS files not found.");
        }

        stage.setTitle("Admin - Settings");
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        companyNameField.textProperty().bindBidirectional(viewModel.companyNameProperty());
        countryBox.valueProperty().bindBidirectional(viewModel.countryProperty());
        currencyBox.valueProperty().bindBidirectional(viewModel.currencyProperty());
        industryBox.valueProperty().bindBidirectional(viewModel.industryProperty());
        dateBox.valueProperty().bindBidirectional(viewModel.dateFormatProperty());

        table.setItems(viewModel.getUsers());

        String activeStyle = "-fx-background-color: white; -fx-background-radius: 16; -fx-text-fill: " + Themes.PRIMARY + "; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 4, 0, 0, 1); -fx-background-insets: 0;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: normal; -fx-background-insets: 0;";

        if (viewModel.languageProperty().get().equals("Slovak")) {
            skBtn.setStyle(activeStyle); enBtn.setStyle(inactiveStyle);
        } else {
            enBtn.setStyle(activeStyle); skBtn.setStyle(inactiveStyle);
        }

        enBtn.setOnAction(e -> {
            enBtn.setStyle(activeStyle); skBtn.setStyle(inactiveStyle);
            viewModel.languageProperty().set("English");
        });

        skBtn.setOnAction(e -> {
            skBtn.setStyle(activeStyle); enBtn.setStyle(inactiveStyle);
            viewModel.languageProperty().set("Slovak");
        });

        saveCompanyBtn.setOnAction(e -> {
            String normalStyleField = "-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 15; -fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 8;";
            String errorStyleField = "-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 15; -fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 2; -fx-border-radius: 8;";

            String normalStyleCombo = "-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 5; -fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 8;";
            String errorStyleCombo = "-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 5; -fx-border-color: " + Themes.TEXT_ERROR + "; -fx-border-width: 2; -fx-border-radius: 8;";

            try {
                industryBox.setStyle(normalStyleCombo);
                countryBox.setStyle(normalStyleCombo);
                currencyBox.setStyle(normalStyleCombo);
                companyNameField.setStyle(normalStyleField);

                viewModel.saveCompanyProfile(this.companyNameField.getText(),this.industryBox.getValue(),this.countryBox.getValue(),this.currencyBox.getValue());

                ToastManager.showSuccess(stage, "Company profile updated successfully.");
            } catch (CompanyValExept ex) {
                switch (ex.getCode()) {
                    case NAME_ERR:
                        companyNameField.setStyle(errorStyleField);
                        break;
                    case INDUSTRY_ERR:
                        industryBox.setStyle(errorStyleCombo);
                        break;
                    case COUNTRY_ERR:
                        countryBox.setStyle(errorStyleCombo);
                        break;
                    case CURRENCY_ERR:
                        currencyBox.setStyle(errorStyleCombo);
                        break;
                }
                ToastManager.showError(stage, ex.getMessage());
            }
        });
        applyPrefsBtn.setOnAction(e -> viewModel.applyPreferences());

        addUserBtn.setOnAction(e -> AddUserDialog.show(stage, viewModel.getUsers(), viewModel::addUser));
        I18n.language.addListener((obs, o, v) -> table.refresh());

        savePassBtn.setOnAction(e -> {
            try {
                viewModel.changePassword(currentPassField.getText(), newPassField.getText(), confirmPassField.getText());
                currentPassField.clear(); newPassField.clear(); confirmPassField.clear();
                ToastManager.showSuccess(stage, "Password changed successfully.");
            } catch (Exception ex) {
                ToastManager.showError(stage, ex.getMessage());
            }
        });

        addClickEffect(saveCompanyBtn);
        addClickEffect(applyPrefsBtn);
        addClickEffect(addUserBtn);
        addClickEffect(savePassBtn);
    }

    private void addClickEffect(Button btn) {
        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.95);
            btn.setScaleY(0.95);
        });
        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
    }

    private VBox buildCompanyProfile() {
        VBox card = createCard();

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = createIcon("fas-building", Themes.PRIMARY, Themes.PRIMARY + "22");

        Label title = new Label(I18n.t("Company Profile"));
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Company Profile")));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        saveCompanyBtn = new Button(I18n.t("Save Changes"));
        I18n.language.addListener((obs, o, v) -> saveCompanyBtn.setText(I18n.t("Save Changes")));
        saveCompanyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;");

        header.getChildren().addAll(icon, title, spacer, saveCompanyBtn);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15);
        companyNameField = createTextField("Mint Management");
        grid.add(createField("COMPANY NAME", companyNameField), 0, 0, 2, 1);
        countryBox = createComboBox("Slovakia", "Slovakia", "Czechia", "USA");
        currencyBox = createComboBox("EUR (€)", "EUR (€)", "USD ($)", "CZK");
        grid.add(createField("COUNTRY", countryBox), 0, 1);
        grid.add(createField("CURRENCY", currencyBox), 1, 1);
        industryBox = createComboBox("Financial Services", "Financial Services", "IT", "Healthcare");
        grid.add(createField("INDUSTRY", industryBox), 0, 2, 2, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        card.getChildren().addAll(header, grid);

        return card;
    }

    private VBox buildPreferences() {
        VBox card = createCard();

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = createIcon("fas-sliders-h", Themes.TEXT_MUTED, "#F3F4F6");

        Label title = new Label(I18n.t("Preferences"));
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Preferences")));
        header.getChildren().addAll(icon, title);

        Label langLabel = new Label(I18n.t("LANGUAGE"));
        langLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        I18n.language.addListener((obs, o, v) -> langLabel.setText(I18n.t("LANGUAGE")));

        HBox langToggle = new HBox();
        langToggle.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 20; -fx-padding: 4;");
        enBtn = new Button(I18n.t("English")); skBtn = new Button(I18n.t("Slovak"));
        I18n.language.addListener((obs, o, v) -> { enBtn.setText(I18n.t("English")); skBtn.setText(I18n.t("Slovak")); });
        enBtn.setMaxWidth(Double.MAX_VALUE); skBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(enBtn, Priority.ALWAYS); HBox.setHgrow(skBtn, Priority.ALWAYS);
        langToggle.getChildren().addAll(enBtn, skBtn);

        dateBox = createComboBox("DD/MM/YYYY", "DD/MM/YYYY", "MM/DD/YYYY", "YYYY-MM-DD");

        applyPrefsBtn = new Button(I18n.t("Apply Settings"));
        I18n.language.addListener((obs, o, v) -> applyPrefsBtn.setText(I18n.t("Apply Settings")));
        applyPrefsBtn.setMaxWidth(Double.MAX_VALUE);
        applyPrefsBtn.setMinHeight(44);
        applyPrefsBtn.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;");

        card.getChildren().addAll(header, langLabel, langToggle, createField("DATE FORMAT", dateBox), applyPrefsBtn);

        return card;
    }

    private VBox buildUserManagement() {
        VBox card = createCard();

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = createIcon("fas-users", Themes.PRIMARY, Themes.PRIMARY + "22");

        Label title = new Label(I18n.t("User Management"));
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("User Management")));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addUserBtn = new Button(I18n.t("+ Add User"));
        I18n.language.addListener((obs, o, v) -> addUserBtn.setText(I18n.t("+ Add User")));
        addUserBtn.setStyle("-fx-background-color: #111827; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-cursor: hand; -fx-background-insets: 0;");

        header.getChildren().addAll(icon, title, spacer, addUserBtn);

        table = new TableView<>();
        table.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        table.setFixedCellSize(50);

        String headerStyle = "-fx-font-weight: bold; -fx-alignment: center-left; -fx-padding: 0 0 0 10;";

        TableColumn<User, String> nameCol = new TableColumn<>(I18n.t("NAME"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle(headerStyle + " -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> nameCol.setText(I18n.t("NAME")));

        TableColumn<User, String> surnameCol = new TableColumn<>(I18n.t("SURNAME"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        surnameCol.setStyle(headerStyle + " -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> surnameCol.setText(I18n.t("SURNAME")));

        TableColumn<User, String> emailCol = new TableColumn<>(I18n.t("EMAIL"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-alignment: center-left; -fx-padding: 0 0 0 10;");
        I18n.language.addListener((obs, o, v) -> emailCol.setText(I18n.t("EMAIL")));

        TableColumn<User, Position> roleCol = new TableColumn<>(I18n.t("POSITION"));
        I18n.language.addListener((obs, o, v) -> roleCol.setText(I18n.t("POSITION")));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        roleCol.setStyle("-fx-alignment: center-left; -fx-padding: 0 0 0 10;");
        roleCol.setCellFactory(col -> new TableCell<User, Position>() {
            @Override protected void updateItem(Position position, boolean empty) {
                super.updateItem(position, empty);
                if (empty || position == null) { setGraphic(null); return; }
                Label pill = new Label(position.name());
                String bgColor = position == Position.Director ? Themes.PRIMARY + "33" : "#F3F4F6";
                String textColor = position == Position.Director ? Themes.PRIMARY : "#374151";
                pill.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-padding: 4 12; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");
                setGraphic(pill);
            }
        });

        TableColumn<User, Void> actionCol = new TableColumn<>(I18n.t("ACTION"));
        I18n.language.addListener((obs, o, v) -> actionCol.setText(I18n.t("ACTION")));
        actionCol.setStyle("-fx-alignment: center-left; -fx-padding: 0 0 0 10;");
        actionCol.setCellFactory(col -> new TableCell<User, Void>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }

                User rowUser = getTableView().getItems().get(getIndex());
                HBox actionContainer = new HBox(8);
                actionContainer.setAlignment(Pos.CENTER_LEFT);

                if (rowUser.getPosition() == Position.Director) {
                    Label protectedLbl = new Label(I18n.t("Protected"));
                    protectedLbl.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 12px;");
                    actionContainer.getChildren().add(protectedLbl);
                } else {
                    FontIcon editIcon = new FontIcon("fas-pen");
                    editIcon.setIconSize(14);
                    editIcon.setIconColor(Color.web(Themes.PRIMARY));

                    StackPane editWrapper = new StackPane(editIcon);
                    editWrapper.setAlignment(Pos.CENTER_LEFT);
                    editWrapper.setStyle("-fx-cursor: hand; -fx-padding: 0 0 0 14;");
                    editWrapper.setOnMouseClicked(e -> EditUserDialog.show(stage, rowUser, viewModel.getUsers(), updatedUser -> {
                        try {
                            viewModel.editUser(updatedUser);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }));

                    FontIcon trashIcon = new FontIcon("fas-trash");
                    trashIcon.setIconSize(14);
                    trashIcon.setIconColor(Color.web(Themes.TEXT_ERROR));

                    StackPane trashWrapper = new StackPane(trashIcon);
                    trashWrapper.setAlignment(Pos.CENTER_LEFT);
                    trashWrapper.setStyle("-fx-cursor: hand; -fx-padding: 0 0 0 14;");
                    trashWrapper.setOnMouseClicked(e -> DeleteConfirmationDialog.show(
                            stage,
                            rowUser.getName() + " " + rowUser.getSurname(),
                            () -> {
                                try {
                                    viewModel.deleteUser(rowUser.getId());
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                    ));

                    actionContainer.getChildren().addAll(editWrapper, trashWrapper);
                }

                setGraphic(actionContainer);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        table.getColumns().addAll(nameCol, surnameCol, emailCol, roleCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        card.getChildren().addAll(header, table);

        return card;
    }

    private VBox buildSecurity() {
        VBox card = createCard();

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = createIcon("fas-shield-alt", Themes.TEXT_ERROR, Themes.TEXT_ERROR + "22");

        Label title = new Label(I18n.t("Security"));
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Security")));
        header.getChildren().addAll(icon, title);

        currentPassField = new PasswordField();

        HBox newPassRow = new HBox(20);
        newPassField = new PasswordField();
        confirmPassField = new PasswordField();

        VBox newPass = createSecurePasswordFieldWrapper("NEW PASSWORD", "Min. 8 chars", newPassField);
        VBox confPass = createSecurePasswordFieldWrapper("CONFIRM PASSWORD", "Re-enter password", confirmPassField);
        HBox.setHgrow(newPass, Priority.ALWAYS); HBox.setHgrow(confPass, Priority.ALWAYS);
        newPassRow.getChildren().addAll(newPass, confPass);

        savePassBtn = new Button(I18n.t("Save New Password"));
        I18n.language.addListener((obs, o, v) -> savePassBtn.setText(I18n.t("Save New Password")));
        savePassBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;");

        HBox btnContainer = new HBox(savePassBtn);
        btnContainer.setAlignment(Pos.CENTER);
        card.getChildren().addAll(header, createSecurePasswordFieldWrapper("CURRENT PASSWORD", "••••••••••••", currentPassField), newPassRow, btnContainer);

        return card;
    }

    private VBox createCard() {
        VBox card = new VBox(25);

        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 16; " +
                        "-fx-border-radius: 16; " +
                        "-fx-border-color: " + Themes.BORDER_LIGHT + "; " +
                        "-fx-border-width: 1; " +
                        "-fx-padding: 30; " +
                        "-fx-background-insets: 0;"
        );

        card.setEffect(new javafx.scene.effect.DropShadow(10, Color.rgb(0, 0, 0, 0.03)));

        return card;
    }

    private StackPane createIcon(String iconCode, String colorHex, String bgColorHex) {
        StackPane pane = new StackPane();
        pane.setMinSize(40, 40); pane.setMaxSize(40, 40);
        pane.setStyle("-fx-background-color: " + bgColorHex + "; -fx-background-radius: 12;");

        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(16);
        icon.setIconColor(Color.web(colorHex));

        pane.getChildren().add(icon);

        return pane;
    }

    private VBox createField(String labelText, Control inputControl) {
        VBox box = new VBox(8);

        Label label = new Label(I18n.t(labelText));
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        I18n.language.addListener((obs, o, v) -> label.setText(I18n.t(labelText)));

        inputControl.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(label, inputControl);

        return box;
    }

    private TextField createTextField(String prompt) {
        TextField tf = new TextField();

        tf.setPromptText(prompt);
        tf.setMinHeight(44); tf.setPrefHeight(44); tf.setMaxHeight(44);
        tf.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 15; -fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-insets: 0;");

        return tf;
    }

    private ComboBox<String> createComboBox(String prompt, String... items) {
        ComboBox<String> cb = new ComboBox<>();

        cb.getItems().addAll(items);
        cb.setPromptText(prompt);
        cb.setMinHeight(44); cb.setPrefHeight(44); cb.setMaxHeight(44);
        cb.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 5; -fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-insets: 0;");
        cb.setMaxWidth(Double.MAX_VALUE);

        return cb;
    }

    private VBox createSecurePasswordFieldWrapper(String labelText, String prompt, PasswordField pf) {
        VBox box = new VBox(8);
        Label label = new Label(I18n.t(labelText));
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        I18n.language.addListener((obs, o, v) -> label.setText(I18n.t(labelText)));

        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER_RIGHT);

        pf.setPromptText(prompt);
        pf.setMinHeight(44); pf.setPrefHeight(44); pf.setMaxHeight(44);
        pf.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 45 0 15; -fx-border-width: 0;");

        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMinHeight(44); tf.setPrefHeight(44); tf.setMaxHeight(44);
        tf.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8; -fx-padding: 0 45 0 15; -fx-border-width: 0;");
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
        box.getChildren().addAll(label, pane);

        return box;
    }
}