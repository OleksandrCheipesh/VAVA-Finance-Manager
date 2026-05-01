package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Employee;
import org.example.model.validation.EmployeeValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeService {
    public Employee addEmployee(Employee employee) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(EmployeeService.class);
        EmployeeValidator.validate(employee);
        String sql = "INSERT INTO employees (company_id, name, surname, email, age, salary, position, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, hired_at";
        logger.info("Adding employee: name={} surname={} email={} companyId={}", employee.getName(), employee.getSurname(), employee.getEmail(), employee.getCompanyId());
        try {
            try (Connection connection = ConnectionProvider.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employee.getCompanyId());
                preparedStatement.setString(2, employee.getName());
                preparedStatement.setString(3, employee.getSurname());

                if (employee.getEmail() != null) {
                    preparedStatement.setString(4, employee.getEmail());
                } else {
                    preparedStatement.setNull(4, Types.VARCHAR);
                }

                if (employee.getAge() != null)
                    preparedStatement.setInt(5, employee.getAge());
                 else
                    preparedStatement.setNull(5, Types.INTEGER);

                if (employee.getSalary() != null)
                    preparedStatement.setBigDecimal(6, employee.getSalary());
                else
                    preparedStatement.setNull(6, Types.NUMERIC);

                preparedStatement.setString(7, employee.getPosition());

                preparedStatement.setObject(8, employee.getStatus(), Types.OTHER);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        employee.setId(resultSet.getInt("id"));
                        employee.setHiredAt(resultSet.getObject("hired_at", java.time.OffsetDateTime.class));
                    }
                }
            }
            logger.info("Employee added: id={} name={} email={} companyId={}", employee.getId(), employee.getName(), employee.getEmail(), employee.getCompanyId());
            return employee;
        } catch (SQLException e) {
            logger.error("Failed to add employee: name={} email={} companyId={}", employee.getName(), employee.getEmail(), employee.getCompanyId(), e);
            throw e;
        }
    }

    public Optional<Employee> getEmployeeById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive.");
        }
        var logger = org.example.logging.AppLog.getLogger(EmployeeService.class);
        String sql = "SELECT * FROM employees WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next())
                    return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load employee by id={}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<Employee> getEmployeeByEmail(String email) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(EmployeeService.class);
        String sql = "SELECT * FROM employees WHERE email = ? ORDER BY id";
        List<Employee> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load employee(s) by email={}", email, e);
            throw e;
        }
        return list;
    }

    public List<Employee> getEmployeesByCompanyId(int companyId) throws SQLException {
        if (companyId <= 0) {
            throw new IllegalArgumentException("Сompany ID must be positive.");
        }
        var logger = org.example.logging.AppLog.getLogger(EmployeeService.class);
        String sql = "SELECT * FROM employees WHERE company_id = ? ORDER BY id";
        List<Employee> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load employees for companyId={}", companyId, e);
            throw e;
        }
        return list;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(EmployeeService.class);
        String sql = "SELECT * FROM employees ORDER BY id";
        List<Employee> list = new ArrayList<>();
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                list.add(mapRow(resultSet));
        } catch (SQLException e) {
            logger.error("Failed to load all employees", e);
            throw e;
        }
        return list;
    }

    public boolean updateEmployee(Employee employee) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(EmployeeService.class);
        EmployeeValidator.validate(employee);
        Employee before = getEmployeeById(employee.getId()).orElse(null);
        String sql = "UPDATE employees SET company_id = ?, name = ?, surname = ?, age = ?, " +
                     "salary = ?, position = ?,email = ?, status = ? WHERE id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employee.getCompanyId());
            preparedStatement.setString(2, employee.getName());
            preparedStatement.setString(3, employee.getSurname());

            if (employee.getAge() != null) {
                preparedStatement.setInt(4, employee.getAge());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }

            if (employee.getSalary() != null) {
                preparedStatement.setBigDecimal(5, employee.getSalary());
            } else {
                preparedStatement.setNull(5, Types.NUMERIC);
            }

            preparedStatement.setString(6, employee.getPosition());
            if (employee.getEmail() != null)
                preparedStatement.setString(7, employee.getEmail());
            else
                preparedStatement.setNull(7, Types.VARCHAR);
            preparedStatement.setObject(8, employee.getStatus(), Types.OTHER);
            preparedStatement.setInt(9, employee.getId());
            boolean updated = preparedStatement.executeUpdate() > 0;
            if (updated) {
                StringBuilder changes = new StringBuilder();
                if (before != null) {
                    if (!java.util.Objects.equals(before.getName(), employee.getName())) changes.append("name changed; ");
                    if (!java.util.Objects.equals(before.getSurname(), employee.getSurname())) changes.append("surname changed; ");
                    if (!java.util.Objects.equals(before.getEmail(), employee.getEmail())) changes.append(String.format("email: %s->%s; ", before.getEmail(), employee.getEmail()));
                    if (!java.util.Objects.equals(before.getAge(), employee.getAge())) changes.append(String.format("age: %s->%s; ", before.getAge(), employee.getAge()));
                    if (!java.util.Objects.equals(before.getSalary(), employee.getSalary())) changes.append(String.format("salary: %s->%s; ", before.getSalary(), employee.getSalary()));
                    if (!java.util.Objects.equals(before.getPosition(), employee.getPosition())) changes.append(String.format("position: %s->%s; ", before.getPosition(), employee.getPosition()));
                }
                logger.info("Employee updated: id={} changes={}", employee.getId(), changes.toString());
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Failed to update employee id={}", employee.getId(), e);
            throw e;
        }
    }

    public boolean deleteEmployee(int id) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(EmployeeService.class);
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive.");
        }
        Employee before = getEmployeeById(id).orElse(null);
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            boolean deleted = preparedStatement.executeUpdate() > 0;
            if (deleted) {
                if (before != null) {
                    logger.info("Employee deleted: id={} name={} companyId={}", before.getId(), before.getName(), before.getCompanyId());
                } else {
                    logger.info("Employee deleted: id={}", id);
                }
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Failed to delete employee id={}", id, e);
            throw e;
        }
    }

    private Employee mapRow(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();
        employee.setId(resultSet.getInt("id"));
        employee.setCompanyId(resultSet.getInt("company_id"));
        employee.setName(resultSet.getString("name"));
        employee.setSurname(resultSet.getString("surname"));

        employee.setEmail(resultSet.getString("email"));
        Integer age = resultSet.getObject("age") != null ? resultSet.getInt("age") : null;
        employee.setAge(age);
        employee.setSalary(resultSet.getBigDecimal("salary"));

        employee.setPosition(resultSet.getString("position"));
        employee.setHiredAt(resultSet.getObject("hired_at", java.time.OffsetDateTime.class));
        String status = resultSet.getString("status");
        if (status != null) {
            employee.setStatus(status);
        }
        return employee;
    }
}

