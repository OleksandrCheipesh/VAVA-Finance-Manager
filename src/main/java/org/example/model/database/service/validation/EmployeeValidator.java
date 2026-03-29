package org.example.model.database.service.validation;

import org.example.model.database.entity.Employee;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class EmployeeValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public static void validate(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        if (employee.getCompanyId() <= 0) {
            throw new IllegalArgumentException("Company ID must be positive.");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        if (employee.getSurname() == null || employee.getSurname().trim().isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be empty.");
        }

        if (employee.getPosition() == null || employee.getPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be empty.");
        }
        if (employee.getEmail() != null && !employee.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(employee.getEmail()).matches()) {
                throw new IllegalArgumentException("Email format is invalid.");
            }
        }

        if (employee.getAge() != null) {
            if (employee.getAge() < 18 || employee.getAge() > 100) {
                throw new IllegalArgumentException("Age must be between 18 and 100.");
            }
        }

        if (employee.getSalary() != null && employee.getSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
    }
}