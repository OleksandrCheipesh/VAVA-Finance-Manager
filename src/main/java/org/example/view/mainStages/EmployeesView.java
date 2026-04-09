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

        // --- 1. Top Bar ---
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        Label title = new Label("Employees");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        HBox.setMargin(title, new Insets(0, 0, 25, 0));

        Region sep = new Region();
        sep.setPrefSize(2, 30);
        sep.setMaxSize(2, 30);
        sep.setStyle("-fx-background-color: " + Themes.BORDER_LIGHT + ";");
        HBox.setMargin(sep, new Insets(0, 10, 25, 10));

        // New Figma Tabs
        HBox tabs = new HBox(30);
        tabs.setAlignment(Pos.BOTTOM_LEFT);

        String activeTab = "-fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-border-color: " + Themes.PRIMARY + "; -fx-border-width: 0 0 3 0; -fx-padding: 0 0 20 0; -fx-cursor: hand;";
        String inactiveTab = "-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-padding: 0 0 20 0; -fx-cursor: hand;";

        Label tabAll = new Label("All Staff");
        Label tabActive = new Label("Active");
        Label tabInactive = new Label("Inactive");
        Label tabContractors = new Label("Contractors");

        tabAll.setStyle(activeTab);
        tabActive.setStyle(inactiveTab);
        tabInactive.setStyle(inactiveTab);
        tabContractors.setStyle(inactiveTab);

        // UI Tabs Logic (Backend filtering to be handled by ViewModel)
        tabAll.setOnMouseClicked(e -> { tabAll.setStyle(activeTab); tabActive.setStyle(inactiveTab); tabInactive.setStyle(inactiveTab); tabContractors.setStyle(inactiveTab); });
        tabActive.setOnMouseClicked(e -> { tabActive.setStyle(activeTab); tabAll.setStyle(inactiveTab); tabInactive.setStyle(inactiveTab); tabContractors.setStyle(inactiveTab); });
        tabInactive.setOnMouseClicked(e -> { tabInactive.setStyle(activeTab); tabAll.setStyle(inactiveTab); tabActive.setStyle(inactiveTab); tabContractors.setStyle(inactiveTab); });
        tabContractors.setOnMouseClicked(e -> { tabContractors.setStyle(activeTab); tabAll.setStyle(inactiveTab); tabActive.setStyle(inactiveTab); tabInactive.setStyle(inactiveTab); });

        tabs.getChildren().addAll(tabAll, tabActive, tabInactive, tabContractors);

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
        summaryContainer.getChildren().addAll(
                createSummaryCard("TOTAL EMPLOYEES", "124", "+4 this month", Themes.TEXT_SUCCESS),
                createSummaryCard("ACTIVE", "118", "95.2% rate", Themes.TEXT_MUTED),
                createSummaryCard("ONBOARDING", "3", "Action needed", "#F59E0B")
        );

        table = new AppTable<>("No employees found. Click '+ Add Employee' to start.");
        table.setItems(viewModel.getEmployees());
        VBox.setVgrow(table, Priority.ALWAYS);

        // Columns
        TableColumn<Employee, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName() + " " + cellData.getValue().getSurname()));

        TableColumn<Employee, String> roleCol = new TableColumn<>("ROLE");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, String> deptCol = new TableColumn<>("DEPARTMENT");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Employee, String> emailCol = new TableColumn<>("EMAIL");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> statusCol = new TableColumn<>("STATUS");
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
                    else { pillBg = "#F1F5F9"; pillTxt = "#64748B"; } // Inactive/Other

                    statusLabel.setStyle("-fx-background-color: " + pillBg + "; -fx-text-fill: " + pillTxt + ";" +
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
                actionBtn.setOnAction(e -> openDetailsModal(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBtn);
            }
        });

        // Also open modal when a row is clicked
        table.setOnMouseClicked(e -> {
            if (table.getSelectionModel().getSelectedItem() != null) {
                openDetailsModal(table.getSelectionModel().getSelectedItem());
            }
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
        stage.setTitle("Admin - Employees");
    }

    private void openDetailsModal(Employee emp) {
        EmployeeDetailsDialog.show(stage, emp,
                updatedEmp -> {
                    table.refresh(); // Refreshes table after edit
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
        // Pass null to indicate a NEW employee is being created
        addBtn.setOnAction(e -> AddEmployeeDialog.show(stage, null, newEmployee -> viewModel.addEmployee(newEmployee)));

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });
    }

    private VBox createSummaryCard(String title, String mainValue, String subText, String subTextColor) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 20;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox valueBox = new HBox(10);
        valueBox.setAlignment(Pos.BASELINE_LEFT);

        Label lblValue = new Label(mainValue);
        lblValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label lblSub = new Label(subText);
        lblSub.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + subTextColor + ";");

        valueBox.getChildren().addAll(lblValue, lblSub);
        card.getChildren().addAll(lblTitle, valueBox);
        return card;
    }
}