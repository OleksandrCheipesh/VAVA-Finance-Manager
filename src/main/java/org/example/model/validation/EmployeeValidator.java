package org.example.model.validation;

import org.example.model.database.entity.Employee;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import static org.example.model.validation.EmpValExept.EmployeeErrorCode;

public class EmployeeValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public static void validate(Employee employee) {

        if (employee == null) {
            throw new EmpValExept(EmployeeErrorCode.INVALID_COMPANY_ID, "Employee cannot be null.");
        }

        if (employee.getCompanyId() <= 0) {
            throw new EmpValExept(EmployeeErrorCode.INVALID_COMPANY_ID, "Company ID must be positive.");
        }

        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new EmpValExept(EmployeeErrorCode.NAME_EMPTY, "Name cannot be empty.");
        }

        if (employee.getSurname() == null || employee.getSurname().trim().isEmpty()) {
            throw new EmpValExept(EmployeeErrorCode.SURNAME_EMPTY, "Surname cannot be empty.");
        }

        if (employee.getPosition() == null || employee.getPosition().trim().isEmpty()) {
            throw new EmpValExept(EmployeeErrorCode.POSITION_EMPTY, "Position cannot be empty.");
        }

        if (employee.getEmail() != null && !employee.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(employee.getEmail()).matches()) {
                throw new EmpValExept(EmployeeErrorCode.INVALID_EMAIL, "Email format is invalid.");
            }
        }

        if (employee.getAge() != null) {
            if (employee.getAge() < 18 || employee.getAge() > 100) {
                throw new EmpValExept(EmployeeErrorCode.INVALID_AGE, "Age must be between 18 and 100.");
            }
        }

        if (employee.getSalary() != null && employee.getSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new EmpValExept(EmployeeErrorCode.NEGATIVE_SALARY, "Salary cannot be negative.");
        }
    }
}