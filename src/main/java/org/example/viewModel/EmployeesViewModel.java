package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.database.entity.Employee;
import org.example.model.database.service.EmployeeService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class EmployeesViewModel {

    // The list that the TableView will automatically watch for changes
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();

    // For passing success/error messages to the View's Toasts
    private final StringProperty message = new SimpleStringProperty("");
    private EmployeeService db = new EmployeeService();

    public EmployeesViewModel() {
        loadEmployees();
    }

    // 1. Fetch data from the database
    public void loadEmployees() {
        // TODO: Your backend team will eventually replace this dummy data
        // with something like: employees.addAll(employeeService.getAllEmployees());

        employees.clear();
        employees.addAll(
                new Employee(1, "Sarah", "Williams", "sarah.w@company.com", 32, new BigDecimal("85000"), "Senior Designer", OffsetDateTime.now().minusYears(2)),
                new Employee(1, "Marcus", "Chen", "m.chen@company.com", 28, new BigDecimal("95000"), "Full Stack Engineer", OffsetDateTime.now().minusMonths(6))
        );
    }

    // 2. Add a new employee to the database and the UI
    public void addEmployee(Employee newEmployee) {
        try {
            // TODO: Call backend to save to database here
            // employeeService.addEmployee(newEmployee);

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