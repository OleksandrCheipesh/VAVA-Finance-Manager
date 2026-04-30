package org.example.view.mainStages;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.example.logging.AppLog;
import org.example.model.database.entity.Employee;
import org.example.view.templates.*;
import org.example.viewModel.EmployeesViewModel;
import javafx.scene.effect.GaussianBlur;


public class EmployeesView extends BaseView {

    private final EmployeesViewModel viewModel = new EmployeesViewModel();

    private BorderPane root;
    private VBox contentArea;
    private AppTable<Employee> table;
    private StateButton addBtn;
    private HBox filterBar;
    private HBox topBar;

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        topBar = new HBox(20);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);
        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));

        Label title = new Label(I18n.t("Employees"));
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Employees")));

        Label subtitle = new Label(I18n.t("Manage your team members and their roles."));
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        I18n.language.addListener((obs, o, v) -> subtitle.setText(I18n.t("Manage your team members and their roles.")));

        titleBox.getChildren().addAll(title, subtitle);

        Region sep = new Region();
        sep.setPrefSize(2, 30);
        sep.setMaxSize(2, 30);
        sep.setStyle("-fx-background-color: " + Themes.BORDER_LIGHT + ";");
        HBox.setMargin(sep, new Insets(0, 10, 25, 10));

        HBox tabs = new HBox(30);
        tabs.setAlignment(Pos.BOTTOM_LEFT);

        String activeTab = "-fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-border-color: " + Themes.PRIMARY + "; -fx-border-width: 0 0 3 0; -fx-padding: 0 0 20 0; -fx-cursor: hand;";
        String inactiveTab = "-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-padding: 0 0 20 0; -fx-cursor: hand;";

        Label tabAll = new Label(I18n.t("All Staff"));
        Label tabActive = new Label(I18n.t("Active"));
        Label tabInactive = new Label(I18n.t("Inactive"));
        Label tabContractors = new Label(I18n.t("Contractors"));
        I18n.language.addListener((obs, o, v) -> {
            tabAll.setText(I18n.t("All Staff")); tabActive.setText(I18n.t("Active"));
            tabInactive.setText(I18n.t("Inactive")); tabContractors.setText(I18n.t("Contractors"));
        });

        tabAll.setStyle(activeTab);
        tabActive.setStyle(inactiveTab);
        tabInactive.setStyle(inactiveTab);
        tabContractors.setStyle(inactiveTab);

        tabAll.setOnMouseClicked(e -> { tabAll.setStyle(activeTab); tabActive.setStyle(inactiveTab); tabInactive.setStyle(inactiveTab); tabContractors.setStyle(inactiveTab); });
        tabActive.setOnMouseClicked(e -> { tabActive.setStyle(activeTab); tabAll.setStyle(inactiveTab); tabInactive.setStyle(inactiveTab); tabContractors.setStyle(inactiveTab); });
        tabInactive.setOnMouseClicked(e -> { tabInactive.setStyle(activeTab); tabAll.setStyle(inactiveTab); tabActive.setStyle(inactiveTab); tabContractors.setStyle(inactiveTab); });
        tabContractors.setOnMouseClicked(e -> { tabContractors.setStyle(activeTab); tabAll.setStyle(inactiveTab); tabActive.setStyle(inactiveTab); tabInactive.setStyle(inactiveTab); });

        tabs.getChildren().addAll(tabAll, tabActive, tabInactive, tabContractors);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addBtn = new StateButton(I18n.t("+ Add Employee"), StateButton.ButtonType.PRIMARY);
        I18n.language.addListener((obs, o, v) -> addBtn.setText(I18n.t("+ Add Employee")));
        HBox.setMargin(addBtn, new Insets(0, 0, 20, 0));

        addBtn.setOnMousePressed(e -> {
            addBtn.setScaleX(0.95);
            addBtn.setScaleY(0.95);
        });

        addBtn.setOnMouseReleased(e -> {
            addBtn.setScaleX(1.0);
            addBtn.setScaleY(1.0);
        });

        topBar.getChildren().addAll(titleBox, sep, tabs, spacer, addBtn);

        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        filterBar = createCustomFilterBar();

        HBox summaryContainer = new HBox(20);

        Label totalEmpLabel = new Label();
        Label activeLabel = new Label();
        Label onboardingLabel = new Label();

        Label totalEmpLabelSub = new Label();
        Label activeLabelSub = new Label();
        Label onboardingLabelSub = new Label();

        totalEmpLabel.textProperty().bind(viewModel.totalEmployeesProperty().asString());
        activeLabel.textProperty().bind(viewModel.activeEmployeesProperty().asString());
        onboardingLabel.textProperty().bind(viewModel.onboardingEmployeesProperty().asString());

        totalEmpLabelSub.textProperty().bind(viewModel.totalEmployeesChangeTextProperty());
        activeLabelSub.textProperty().bind(viewModel.activeEmployeesRateProperty());
        onboardingLabelSub.textProperty().bind(viewModel.onboardingEmployeesActionTextProperty());

        summaryContainer.getChildren().addAll(
                createSummaryCard(I18n.t("TOTAL EMPLOYEES"), totalEmpLabel, totalEmpLabelSub, Themes.TEXT_SUCCESS),
                createSummaryCard(I18n.t("ACTIVE"), activeLabel, activeLabelSub, Themes.TEXT_MUTED),
                createSummaryCard(I18n.t("ONBOARDING"), onboardingLabel, onboardingLabelSub, "#F59E0B")
        );

        table = new AppTable<>("");
        table.setItems(viewModel.getEmployees());
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox emptyStateBox = new VBox(5);
        emptyStateBox.setAlignment(Pos.CENTER);

        Label emptyTitle = new Label(I18n.t("No Employees Yet"));
        emptyTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: 900; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> emptyTitle.setText(I18n.t("No Employees Yet")));

        Label emptySub = new Label(I18n.t("Register your first employee"));
        emptySub.setStyle("-fx-font-size: 18px; -fx-text-fill: #869F9B;");
        I18n.language.addListener((obs, o, v) -> emptySub.setText(I18n.t("Register your first employee")));
        emptyStateBox.getChildren().addAll(emptyTitle, emptySub);

        table.setPlaceholder(emptyStateBox);

        TableColumn<Employee, String> nameCol = new TableColumn<>(I18n.t("NAME"));
        I18n.language.addListener((obs, o, v) -> nameCol.setText(I18n.t("NAME")));
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName() + " " + cellData.getValue().getSurname()));

        TableColumn<Employee, String> roleCol = new TableColumn<>(I18n.t("ROLE"));
        I18n.language.addListener((obs, o, v) -> roleCol.setText(I18n.t("ROLE")));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, String> deptCol = new TableColumn<>(I18n.t("DEPARTMENT"));
        I18n.language.addListener((obs, o, v) -> deptCol.setText(I18n.t("DEPARTMENT")));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Employee, String> emailCol = new TableColumn<>(I18n.t("EMAIL"));
        I18n.language.addListener((obs, o, v) -> emailCol.setText(I18n.t("EMAIL")));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> statusCol = new TableColumn<>(I18n.t("STATUS"));
        I18n.language.addListener((obs, o, v) -> statusCol.setText(I18n.t("STATUS")));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(item);
                    String pillBg, pillTxt;
                    if ("Active".equalsIgnoreCase(item)) { pillBg = "#D1FAE5"; pillTxt = "#10B981"; }
                    else if ("Contractor".equalsIgnoreCase(item)) { pillBg = "#FEF3C7"; pillTxt = "#D97706"; }
                    else { pillBg = "#F1F5F9"; pillTxt = "#64748B"; }

                    statusLabel.setStyle("-fx-background-color: " + pillBg + "; -fx-text-fill: " + pillTxt + ";" +
                            "-fx-padding: 4 12; -fx-background-radius: 12; -fx-border-radius: 12;" +
                            "-fx-font-size: 12px; -fx-font-weight: bold;");
                    setGraphic(statusLabel);
                }
            }
        });

        TableColumn<Employee, Void> actionCol = new TableColumn<>(I18n.t("ACTION"));
        I18n.language.addListener((obs, o, v) -> actionCol.setText(I18n.t("ACTION")));
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button actionBtn = new Button("⋮");
            {
                actionBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 24px; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-cursor: hand; -fx-padding: 0;");
                actionBtn.setOnAction(e -> openDetailsModal(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBtn);
            }
        });

        table.setOnMouseClicked(e -> {
            var selected = table.getSelectionModel().getSelectedItem();
            table.getSelectionModel().clearSelection();
            if (selected != null) openDetailsModal(selected);
        });

        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.18));
        roleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.18));
        deptCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        emailCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

        table.getColumns().addAll(nameCol, roleCol, deptCol, emailCol, statusCol, actionCol);

        contentArea.getChildren().addAll(filterBar, summaryContainer, table);
        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            var logger = AppLog.getLogger(EmployeesView.class);
            logger.warn("Could not load CSS files for EmployeesView: {}", e.getMessage());
            logger.debug("CSS load exception", e);
        }

        stage.setTitle("Admin - Employees");
    }

    private void openDetailsModal(Employee emp) {
        EmployeeDetailsDialog.show(stage, emp,
                updatedEmp -> {
                    table.refresh();
                    ToastManager.showSuccess(stage, "Employee profile updated!");
                },
                deletedEmp -> {
                    viewModel.getEmployees().remove(deletedEmp);
                    ToastManager.showSuccess(stage, "Employee deleted.");
                }
        );
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        if (!viewModel.hasAccessProperty().get()) {
            showAccessDenied();
            return;
        }

        addBtn.setOnAction(e -> AddEmployeeDialog.show(stage, null, newEmployee -> viewModel.addEmployee(newEmployee)));

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });
    }

    private VBox createSummaryCard(String title, Label valueLabel, Label subText, String subTextColor) {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 18 20;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox valueBox = new VBox(0);
        valueBox.setAlignment(Pos.CENTER_LEFT);

        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        subText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + subTextColor + ";");

        valueBox.getChildren().addAll(valueLabel, subText);
        card.getChildren().addAll(lblTitle, valueBox);

        return card;
    }

    private HBox createCustomFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12 20;");

        String filterStyle = "-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

        HBox searchContainer = new HBox(8);

        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle(filterStyle + " -fx-padding: 0 12;");
        searchContainer.setPrefHeight(40);

        SVGPath searchIcon = new SVGPath();

        searchIcon.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        searchIcon.setFill(Color.web(Themes.TEXT_MUTED));
        searchIcon.setScaleX(0.8);
        searchIcon.setScaleY(0.8);

        TextField search = new TextField();
        search.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0; -fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        StackPane searchPane = new StackPane();
        searchPane.setAlignment(Pos.CENTER_LEFT);

        Label searchPrompt = new Label("Search by name, role...");

        searchPrompt.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");
        searchPrompt.setMouseTransparent(true);
        search.textProperty().addListener((obs, old, val) -> searchPrompt.setVisible(val.isEmpty()));

        searchPane.getChildren().addAll(search, searchPrompt);

        HBox.setHgrow(searchPane, Priority.ALWAYS);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);

        searchContainer.getChildren().addAll(searchIcon, searchPane);

        ComboBox<String> dept = new ComboBox<>();

        dept.setPromptText("Department");
        dept.setStyle(filterStyle);
        dept.setPrefHeight(40);
        dept.setPrefWidth(150);
        dept.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Department" : item);
            }
        });

        DatePicker datePicker = new DatePicker();

        datePicker.setStyle(filterStyle);
        datePicker.getStyleClass().add("filter-item");
        datePicker.setPrefHeight(40);
        datePicker.setPrefWidth(150);

        StackPane datePane = new StackPane();

        datePane.setAlignment(Pos.CENTER_LEFT);

        Label datePrompt = new Label("Filter Date");

        datePrompt.setStyle("-fx-text-fill: #9CA3AF; -fx-padding: 0 0 0 12; -fx-font-size: 14px;");
        datePrompt.setMouseTransparent(true);
        datePicker.getEditor().textProperty().addListener((obs, old, val) -> datePrompt.setVisible(val.isEmpty()));
        datePane.getChildren().addAll(datePicker, datePrompt);

        Button clear = new Button("Clear");
        clear.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;");
        clear.setOnAction(e -> {
            search.clear();
            dept.setValue(null);
            datePicker.setValue(null);
            datePicker.getEditor().clear();
        });

        bar.getChildren().addAll(searchContainer, dept, datePane, clear);
        return bar;
    }
}