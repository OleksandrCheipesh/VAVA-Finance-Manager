package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.SessionManager;
import org.example.model.database.entity.Employee;
import org.example.model.database.service.EmployeeService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class EmployeesViewModel {

    // The list that the TableView will automatically watch for changes
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();

    // For passing success/error messages to the View's Toasts
    private final StringProperty message = new SimpleStringProperty("");
    private EmployeeService db = new EmployeeService();

    public EmployeesViewModel() {
        loadEmployees();
    }

    public void loadEmployees() {
        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        try {
            List<Employee> dbEmployees = db.getEmployeesByCompanyId(companyId);

            employees.clear();
            employees.addAll(dbEmployees);

            message.set("Success: Employees loaded successfully!");

        } catch (Exception e) {
            message.set("Error: Failed to load employees. " + e.getMessage());
        }
    }

    // 2. Add a new employee to the database and the UI
    public void addEmployee(Employee newEmployee) {
        try {
            db.addEmployee(newEmployee);

            // If database save is successful, add to the UI list
            employees.add(newEmployee);

            // Trigger success message (Starts with "Success:" so the View knows which Toast to show)
            message.set("Success: Employee added successfully!");

        } catch (Exception e) {
            // Trigger error message
            message.set("Error: Failed to add employee. " + e.getMessage());
        }
    }

    public ObservableList<Employee> getEmployees() {
        return employees;
    }

    public StringProperty messageProperty() {
        return message;
    }
}