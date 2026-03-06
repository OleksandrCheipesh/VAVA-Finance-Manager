package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeService {

    public Employee addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (company_id, name, surname, age, salary, position) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, hired_at";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employee.getCompanyId());
            preparedStatement.setString(2, employee.getName());
            preparedStatement.setString(3, employee.getSurname());

            if (employee.getAge() != null)
                preparedStatement.setInt(4, employee.getAge());
             else
                preparedStatement.setNull(4, Types.INTEGER);

            if (employee.getSalary() != null)
                preparedStatement.setBigDecimal(5, employee.getSalary());
            else
                preparedStatement.setNull(5, Types.NUMERIC);

            preparedStatement.setString(6, employee.getPosition());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    employee.setId(resultSet.getInt("id"));
                    employee.setHiredAt(resultSet.getObject("hired_at", java.time.OffsetDateTime.class));
                }
            }
        }
        return employee;
    }

    public Optional<Employee> getEmployeeById(int id) throws SQLException {
        String sql = "SELECT * FROM employees WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next())
                    return Optional.of(mapRow(resultSet));
            }
        }
        return Optional.empty();
    }

    public List<Employee> getEmployeesByCompanyId(int companyId) throws SQLException {
        String sql = "SELECT * FROM employees WHERE company_id = ? ORDER BY id";
        List<Employee> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    list.add(mapRow(resultSet));
            }
        }
        return list;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "SELECT * FROM employees ORDER BY id";
        List<Employee> list = new ArrayList<>();
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                list.add(mapRow(resultSet));
        }
        return list;
    }

    public boolean updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET company_id = ?, name = ?, surname = ?, age = ?, " +
                     "salary = ?, position = ? WHERE id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employee.getCompanyId());
            preparedStatement.setString(2, employee.getName());
            preparedStatement.setString(3, employee.getSurname());

            if (employee.getAge() != null)
                preparedStatement.setInt(4, employee.getAge());
             else
                 preparedStatement.setNull(4, Types.INTEGER);

            if (employee.getSalary() != null)
                preparedStatement.setBigDecimal(5, employee.getSalary());
             else
                 preparedStatement.setNull(5, Types.NUMERIC);

            preparedStatement.setString(6, employee.getPosition());
            preparedStatement.setInt(7, employee.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    private Employee mapRow(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();
        employee.setId(resultSet.getInt("id"));
        employee.setCompanyId(resultSet.getInt("company_id"));
        employee.setName(resultSet.getString("name"));
        employee.setSurname(resultSet.getString("surname"));

        int age = resultSet.getInt("age");
        employee.setAge(resultSet.wasNull() ? null : age);
        employee.setSalary(resultSet.getBigDecimal("salary"));
        employee.setPosition(resultSet.getString("position"));
        employee.setHiredAt(resultSet.getObject("hired_at", java.time.OffsetDateTime.class));
        return employee;
    }
}

