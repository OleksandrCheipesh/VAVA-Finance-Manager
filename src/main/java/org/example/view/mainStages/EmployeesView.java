package org.example.view.mainStages;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.example.model.database.entity.Employee;
import org.example.view.templates.*;
import org.example.viewModel.EmployeesViewModel;

import java.math.BigDecimal;

public class EmployeesView extends BaseView {

    private final EmployeesViewModel viewModel = new EmployeesViewModel();

    private BorderPane root;
    private VBox contentArea;
    private AppTable<Employee> table;
    private StateButton addBtn;
    private FilterBar filterBar;

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        // --- 1. Top Bar (Matching TransactionsView) ---
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        Label title = new Label("Employees");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        HBox.setMargin(title, new Insets(0, 0, 25, 0));

        // Visual divider
        Region sep = new Region();
        sep.setPrefSize(2, 30);
        sep.setMaxSize(2, 30);
        sep.setStyle("-fx-background-color: " + Themes.BORDER_LIGHT + ";");
        HBox.setMargin(sep, new Insets(0, 10, 25, 10));

        // Tabs
        HBox tabs = new HBox(30);
        tabs.setAlignment(Pos.BOTTOM_LEFT);

        String activeTab = "-fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-border-color: " + Themes.PRIMARY + "; -fx-border-width: 0 0 3 0; -fx-padding: 0 0 20 0; -fx-cursor: hand;";
        String inactiveTab = "-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-padding: 0 0 20 0; -fx-cursor: hand;";

        Label tabAll = new Label("All Staff");
        Label tabContractors = new Label("Contractors");
        Label tabTeams = new Label("Teams");

        tabAll.setStyle(activeTab);
        tabContractors.setStyle(inactiveTab);
        tabTeams.setStyle(inactiveTab);

        // Tab click events
        tabAll.setOnMouseClicked(e -> {
            tabAll.setStyle(activeTab);
            tabContractors.setStyle(inactiveTab);
            tabTeams.setStyle(inactiveTab);
            // viewModel.filterByType("ALL");
        });
        tabContractors.setOnMouseClicked(e -> {
            tabContractors.setStyle(activeTab);
            tabAll.setStyle(inactiveTab);
            tabTeams.setStyle(inactiveTab);
            // viewModel.filterByType("CONTRACTORS");
        });
        tabTeams.setOnMouseClicked(e -> {
            tabTeams.setStyle(activeTab);
            tabAll.setStyle(inactiveTab);
            tabContractors.setStyle(inactiveTab);
            // viewModel.filterByType("TEAMS");
        });

        tabs.getChildren().addAll(tabAll, tabContractors, tabTeams);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addBtn = new StateButton("+ Add Employee", StateButton.ButtonType.PRIMARY);
        HBox.setMargin(addBtn, new Insets(0, 0, 20, 0));

        topBar.getChildren().addAll(title, sep, tabs, spacer, addBtn);

        // --- 2. Content Area ---
        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        filterBar = new FilterBar();

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
                createSummaryCard("TOTAL EMPLOYEES", totalEmpLabel, totalEmpLabelSub, Themes.TEXT_SUCCESS),
                createSummaryCard("ACTIVE", activeLabel, activeLabelSub, Themes.TEXT_MUTED),
                createSummaryCard("ONBOARDING", onboardingLabel, onboardingLabelSub, "#F59E0B")
        );

        table = new AppTable<>("No employees found. Click '+ Add Employee' to start.");
        table.setItems(viewModel.getEmployees());
        VBox.setVgrow(table, Priority.ALWAYS);

        // Columns
        TableColumn<Employee, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName() + " " + cellData.getValue().getSurname()));

        TableColumn<Employee, String> roleCol = new TableColumn<>("ROLE");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, String> emailCol = new TableColumn<>("EMAIL");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, BigDecimal> salaryCol = new TableColumn<>("SALARY");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Employee, String> statusCol = new TableColumn<>("STATUS");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty("Active"));
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(item);
                    statusLabel.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: " + Themes.TEXT_SUCCESS + ";" +
                            "-fx-padding: 4 12; -fx-background-radius: 12; -fx-border-radius: 12;" +
                            "-fx-font-size: 12px; -fx-font-weight: bold;");
                    setGraphic(statusLabel);
                }
            }
        });

        TableColumn<Employee, Void> actionCol = new TableColumn<>("ACTION");
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button actionBtn = new Button("⋮");
            {
                actionBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 24px; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-cursor: hand; -fx-padding: 0;");
                actionBtn.setOnAction(e -> System.out.println("Action clicked"));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBtn);
            }
        });

        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        roleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        emailCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        salaryCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

        table.getColumns().addAll(nameCol, roleCol, emailCol, salaryCol, statusCol, actionCol);

        contentArea.getChildren().addAll(filterBar, summaryContainer, table);
        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);
        stage.setTitle("Admin - Employees");
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        addBtn.setOnAction(e -> AddEmployeeDialog.show(stage, newEmployee -> viewModel.addEmployee(newEmployee)));

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });
    }

    private VBox createSummaryCard(String title, Label valueLabel, Label subText, String subTextColor) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 20;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox valueBox = new HBox(10);
        valueBox.setAlignment(Pos.BASELINE_LEFT);

        Label lblValue = valueLabel;
        lblValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label lblSub = subText;
        lblSub.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + subTextColor + ";");

        valueBox.getChildren().addAll(lblValue, lblSub);
        card.getChildren().addAll(lblTitle, valueBox);
        return card;
    }
}