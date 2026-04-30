package org.example.viewModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.example.model.database.entity.Position;

public class EmployeesViewModel {

    // The list that the TableView will automatically watch for changes
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();


    // For passing success/error messages to the View's Toasts
    private final StringProperty message = new SimpleStringProperty("");
    private EmployeeService db = new EmployeeService();

    // Summary
    private final IntegerProperty totalEmployees = new SimpleIntegerProperty(0);
    private final IntegerProperty activeEmployees = new SimpleIntegerProperty(0);
    private final IntegerProperty onboardingEmployees = new SimpleIntegerProperty(0);
    private final StringProperty totalEmployeesChangeText = new SimpleStringProperty("");
    private final StringProperty activeEmployeesRate = new SimpleStringProperty("");
    private final StringProperty onboardingEmployeesActionText = new SimpleStringProperty("");
    public IntegerProperty totalEmployeesProperty() { return totalEmployees; }
    public IntegerProperty activeEmployeesProperty() { return activeEmployees; }
    public IntegerProperty onboardingEmployeesProperty() { return onboardingEmployees; }
    public StringProperty totalEmployeesChangeTextProperty() { return totalEmployeesChangeText; }
    public StringProperty activeEmployeesRateProperty() { return activeEmployeesRate; }
    public StringProperty onboardingEmployeesActionTextProperty() { return onboardingEmployeesActionText; }

    public EmployeesViewModel() {
        loadEmployees();
    }


    private void recalculateSummary() {
        totalEmployees.set(employees.size());

        activeEmployees.set(
                (int) employees.stream()
                        .filter(e -> "ACTIVE".equals(e.getStatus().toUpperCase()))
                        .count()
        );

        onboardingEmployees.set(
                (int) employees.stream()
                        .filter(e -> "CONTRACTOR".equals(e.getStatus().toUpperCase()))
                        .count()
        );

        long addedThisMonth = (int) employees.stream()
                .filter(e -> e.getHiredAt() != null)
                .filter(e -> e.getHiredAt().isAfter(OffsetDateTime.now().minusMonths(1)))
                .count();

        totalEmployeesChangeText.set("+" + addedThisMonth + " this month");

        double rate = totalEmployees.get() == 0
                ? 0
                : (double) activeEmployees.get() / totalEmployees.get() * 100;

        activeEmployeesRate.set((int) rate + "% rate");

        onboardingEmployeesActionText.set(
                onboardingEmployees.get() > 0 ? "Action needed" : "No action needed"
        );
    }
    public void loadEmployees() {
        var logger = org.example.logging.AppLog.getLogger(EmployeesViewModel.class);
        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        logger.info("Loading employees for companyId={}", companyId);
        try {
            List<Employee> dbEmployees = db.getEmployeesByCompanyId(companyId);

            employees.clear();
            employees.addAll(dbEmployees);

            message.set("Success: Employees loaded successfully!");
            logger.info("Loaded {} employees for companyId={}", dbEmployees.size(), companyId);

        } catch (Exception e) {
            message.set("Error: Failed to load employees. " + e.getMessage());
            logger.error("Failed to load employees for companyId={}", companyId, e);
        }
        finally {
            recalculateSummary();
        }
    }
    public void updateEmployee(Employee updatedEmployee) {
        try {
            db.updateEmployee(updatedEmployee);
            for (int i = 0; i < employees.size(); i++) {
                if (employees.get(i).getId() == updatedEmployee.getId()) {
                    employees.set(i, updatedEmployee);
                    break;
                }
            }

            message.set("Success: Employee updated successfully!");

        } catch (Exception e) {
            message.set("Error: Failed to update employee. " + e.getMessage());
        }
        finally {
            recalculateSummary();
        }
    }

    public void deleteEmployee(Employee employee) {
        try {
            db.deleteEmployee(employee.getId());
            employees.removeIf(e -> e.getId() == employee.getId());

            message.set("Success: Employee deleted successfully!");

        } catch (Exception e) {
            message.set("Error: Failed to delete employee. " + e.getMessage());
        }
        finally {
            recalculateSummary();
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
        finally {
            recalculateSummary();
        }
    }

    public ObservableList<Employee> getEmployees() {
        return employees;
    }

    public StringProperty messageProperty() {
        return message;
    }

    private final BooleanProperty hasAccess = new SimpleBooleanProperty(
            SessionManager.getInstance().getPosition() == Position.Director ||
                    SessionManager.getInstance().getPosition() == Position.Accountant
    );
    public BooleanProperty hasAccessProperty() { return hasAccess; }

}