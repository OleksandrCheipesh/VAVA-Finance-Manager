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

        contentArea = new VBox(25);

        // --- 1. Header (Title + Add Button) ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = buildContentTitle("Employees");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addBtn = new StateButton("+ Add Employee", StateButton.ButtonType.PRIMARY);
        header.getChildren().addAll(title, spacer, addBtn);

        // --- 2. Filter Bar ---
        filterBar = new FilterBar();

        // --- 3. Summary Widgets (Figma Cards) ---
        HBox summaryContainer = new HBox(20);
        summaryContainer.getChildren().addAll(
                createSummaryCard("TOTAL EMPLOYEES", "124", "+4 this month", "#10B981"), // Green text
                createSummaryCard("ACTIVE", "118", "95.2% rate", "#9CA3AF"),            // Gray text
                createSummaryCard("ONBOARDING", "3", "Action needed", "#F59E0B")        // Orange text
        );

        // --- 4. Table Setup ---
        table = new AppTable<>("No employees found. Click '+ Add Employee' to start.");
        table.setItems(viewModel.getEmployees());
        VBox.setVgrow(table, Priority.ALWAYS);

        // Name (Combined Name + Surname)
        TableColumn<Employee, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue();
            return new SimpleStringProperty(emp.getName() + " " + emp.getSurname());
        });

        // Role (Position)
        TableColumn<Employee, String> roleCol = new TableColumn<>("ROLE");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        // Email
        TableColumn<Employee, String> emailCol = new TableColumn<>("EMAIL");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Salary
        TableColumn<Employee, BigDecimal> salaryCol = new TableColumn<>("SALARY");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        // Status (Custom Pill Design)
        TableColumn<Employee, String> statusCol = new TableColumn<>("STATUS");
        // We simulate a status since your entity doesn't have one natively
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty("Active"));
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(item);
                    // Green Figma pill styling
                    statusLabel.setStyle(
                            "-fx-background-color: #D1FAE5; -fx-text-fill: #10B981;" +
                                    "-fx-padding: 4 12; -fx-background-radius: 12; -fx-border-radius: 12;" +
                                    "-fx-font-size: 12px; -fx-font-weight: bold;"
                    );
                    setGraphic(statusLabel);
                }
            }
        });

        // Action Button (Custom 3-dots button)
        TableColumn<Employee, Void> actionCol = new TableColumn<>("ACTION");
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button actionBtn = new Button("⋮"); // Unicode for 3 vertical dots
            {
                actionBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 28px; -fx-text-fill: #9CA3AF; -fx-cursor: hand; -fx-padding: 0;");
                actionBtn.setOnAction(e -> {
                    Employee selectedEmp = getTableView().getItems().get(getIndex());
                    System.out.println("Action clicked for: " + selectedEmp.getName());
                    // TODO: Open edit/delete dropdown menu here
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBtn);
            }
        });

        // Distribute column widths
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        roleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        emailCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        salaryCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

        table.getColumns().addAll(nameCol, roleCol, emailCol, salaryCol, statusCol, actionCol);

        contentArea.getChildren().addAll(header, filterBar, summaryContainer, table);
        root.setCenter(contentArea);

        scene = new Scene(root);
        stage.setTitle("App Manager - Employees");
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        addBtn.setOnAction(e -> {
            AddEmployeeDialog.show(stage, newEmployee -> {
                viewModel.addEmployee(newEmployee);
            });
        });

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });
    }

    // --- Helper Method to build the Figma Summary Cards ---
    private VBox createSummaryCard(String title, String mainValue, String subText, String subTextColor) {
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-border-color: " + Themes.BORDER_LIGHT + ";" +
                        "-fx-border-radius: 12; -fx-background-radius: 12;" +
                        "-fx-padding: 20;"
        );
        HBox.setHgrow(card, Priority.ALWAYS); // Ensures the 3 cards stretch to fill the row evenly

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