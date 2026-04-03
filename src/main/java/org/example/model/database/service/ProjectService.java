package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Project;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectService {

    public Project addProject(Project project) throws SQLException {
        String sql = "INSERT INTO projects (company_id, name, description, budget_limit, start_date, end_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, project.getCompanyId());
            preparedStatement.setString(2, project.getName());
            preparedStatement.setString(3, project.getDescription());

            if (project.getBudgetLimit() != null)
                preparedStatement.setBigDecimal(4, project.getBudgetLimit());
            else
                preparedStatement.setNull(4, Types.NUMERIC);

            if (project.getStartDate() != null)
                preparedStatement.setDate(5, Date.valueOf(project.getStartDate()));
            else
                preparedStatement.setNull(5, Types.DATE);

            if (project.getEndDate() != null)
                preparedStatement.setDate(6, Date.valueOf(project.getEndDate()));
            else
                preparedStatement.setNull(6, Types.DATE);

            preparedStatement.setBoolean(7, project.isActive());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    project.setId(resultSet.getInt("id"));
                    project.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
                }
            }
        }
        return project;
    }

    public Optional<Project> getProjectById(int id) throws SQLException {
        String sql = "SELECT * FROM projects WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public List<Project> getProjectsByCompanyId(int companyId) throws SQLException {
        String sql = "SELECT * FROM projects WHERE company_id = ? ORDER BY id";
        List<Project> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Project> getActiveProjectsByCompanyId(int companyId) throws SQLException {
        String sql = "SELECT * FROM projects WHERE company_id = ? AND is_active = true ORDER BY id";
        List<Project> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Project> getAllProjects() throws SQLException {
        String sql = "SELECT * FROM projects ORDER BY id";
        List<Project> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                list.add(mapRow(resultSet));
        }
        return list;
    }

    public boolean updateProject(Project project) throws SQLException {
        String sql = "UPDATE projects SET company_id = ?, name = ?, description = ?, budget_limit = ?, " +
                     "start_date = ?, end_date = ?, is_active = ? WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, project.getCompanyId());
            preparedStatement.setString(2, project.getName());
            preparedStatement.setString(3, project.getDescription());

            if (project.getBudgetLimit() != null)
                preparedStatement.setBigDecimal(4, project.getBudgetLimit());
            else
                preparedStatement.setNull(4, Types.NUMERIC);

            if (project.getStartDate() != null)
                preparedStatement.setDate(5, Date.valueOf(project.getStartDate()));
            else
                preparedStatement.setNull(5, Types.DATE);

            if (project.getEndDate() != null)
                preparedStatement.setDate(6, Date.valueOf(project.getEndDate()));
            else
                preparedStatement.setNull(6, Types.DATE);

            preparedStatement.setBoolean(7, project.isActive());
            preparedStatement.setInt(8, project.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteProject(int id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    private Project mapRow(ResultSet resultSet) throws SQLException {
        Project project = new Project();
        project.setId(resultSet.getInt("id"));
        project.setCompanyId(resultSet.getInt("company_id"));
        project.setName(resultSet.getString("name"));
        project.setDescription(resultSet.getString("description"));

        BigDecimal budgetLimit = resultSet.getBigDecimal("budget_limit");
        project.setBudgetLimit(resultSet.wasNull() ? null : budgetLimit);

        Date startDate = resultSet.getDate("start_date");
        project.setStartDate(startDate != null ? startDate.toLocalDate() : null);
        
        Date endDate = resultSet.getDate("end_date");
        project.setEndDate(endDate != null ? endDate.toLocalDate() : null);

        project.setActive(resultSet.getBoolean("is_active"));
        project.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
        return project;
    }
}

