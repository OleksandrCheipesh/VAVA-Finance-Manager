package org.example.view.authentication;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.model.database.entity.Employee;
import org.example.view.templates.*;
import org.example.viewModel.EmployeesViewModel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class EmployeesView extends BaseView {

    // 1. Instantiate the ViewModel
    private final EmployeesViewModel viewModel = new EmployeesViewModel();

    private BorderPane root;
    private VBox contentArea;
    private AppTable<Employee> table;

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root); // Inherited from BaseView

        contentArea = new VBox(25);

        // --- Header ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = buildContentTitle("Employees");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StateButton addBtn = new StateButton("+ Add Employee", StateButton.ButtonType.PRIMARY);
        addBtn.setOnAction(e -> {
            // Open Modal. When it succeeds, pass the new employee to the ViewModel!
            AddEmployeeDialog.show(stage, null, newEmployee -> {
                viewModel.addEmployee(newEmployee);
            });
        });

        header.getChildren().addAll(title, spacer, addBtn);

        // --- Filter Bar ---
        FilterBar filterBar = new FilterBar();

        // --- Table Setup ---
        table = new AppTable<>("No employees found. Click '+ Add Employee' to start.");

        // 2. Bind the table directly to the ViewModel's list
        table.setItems(viewModel.getEmployees());
        VBox.setVgrow(table, Priority.ALWAYS);

        // Columns mapped to Entity getters
        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Employee, String> posCol = new TableColumn<>("Position");
        posCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, BigDecimal> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Employee, OffsetDateTime> dateCol = new TableColumn<>("Hire Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("hiredAt"));

        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        surnameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        posCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        salaryCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));

        table.getColumns().addAll(nameCol, surnameCol, posCol, salaryCol, dateCol);

        contentArea.getChildren().addAll(header, filterBar, table);
        root.setCenter(contentArea);

        scene = new Scene(root);
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        // 3. Listen to the ViewModel for messages to show Toasts!
        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });
    }
}