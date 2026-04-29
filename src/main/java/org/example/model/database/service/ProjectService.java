package org.example.model.database.service;

import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Project;
import org.example.model.validation.ProjectValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectService {

    public Project addProject(Project project) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(ProjectService.class);
        ProjectValidator.validate(project);
        String sql = "INSERT INTO projects (company_id, name, description, budget_limit, start_date, end_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        logger.info("Adding project: name={} companyId={} startDate={} endDate={} active={}", project.getName(), project.getCompanyId(), project.getStartDate(), project.getEndDate(), project.isActive());
        try {
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
            logger.info("Project created: id={} name={} companyId={}", project.getId(), project.getName(), project.getCompanyId());
            return project;
        } catch (SQLException e) {
            logger.error("Failed to add project name={} companyId={}", project.getName(), project.getCompanyId(), e);
            throw e;
        }
    }

    public Optional<Project> getProjectById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Project ID must be positive.");
        }
        var logger = org.example.logging.AppLog.getLogger(ProjectService.class);
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement refreshStatement = connection.prepareStatement(
                     "UPDATE projects SET is_active = false WHERE end_date IS NOT NULL AND end_date < CURRENT_DATE AND is_active = true"
             );
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            refreshExpiredProjects(refreshStatement);
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to load project by id={}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<Project> getProjectsByCompanyId(int companyId) throws SQLException {
        if (companyId <= 0) {
            throw new IllegalArgumentException("Company ID must be positive.");
        }
        var logger = org.example.logging.AppLog.getLogger(ProjectService.class);
        String sql = "SELECT * FROM projects WHERE company_id = ? ORDER BY id";
        List<Project> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement refreshStatement = connection.prepareStatement(
                     "UPDATE projects SET is_active = false WHERE company_id = ? AND end_date IS NOT NULL AND end_date < CURRENT_DATE AND is_active = true"
             );
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            refreshExpiredProjects(refreshStatement, companyId);
            preparedStatement.setInt(1, companyId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to load projects for companyId={}", companyId, e);
            throw e;
        }
        return list;
    }

    public List<Project> getActiveProjectsByCompanyId(int companyId) throws SQLException {
        if (companyId <= 0) {
            throw new IllegalArgumentException("Company ID must be positive.");
        }
        var logger = org.example.logging.AppLog.getLogger(ProjectService.class);
        String sql = "SELECT * FROM projects WHERE company_id = ? AND is_active = true ORDER BY id";
        List<Project> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement refreshStatement = connection.prepareStatement(
                     "UPDATE projects SET is_active = false WHERE company_id = ? AND end_date IS NOT NULL AND end_date < CURRENT_DATE AND is_active = true"
             );
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            refreshExpiredProjects(refreshStatement, companyId);
            preparedStatement.setInt(1, companyId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to load active projects for companyId={}", companyId, e);
            throw e;
        }
        return list;
    }

    public List<Project> getAllProjects() throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(ProjectService.class);
        String sql = "SELECT * FROM projects ORDER BY id";
        List<Project> list = new ArrayList<>();

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement refreshStatement = connection.prepareStatement(
                     "UPDATE projects SET is_active = false WHERE end_date IS NOT NULL AND end_date < CURRENT_DATE AND is_active = true"
             );
             Statement statement = connection.createStatement()) {
            refreshExpiredProjects(refreshStatement);
            try (ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Failed to load all projects", e);
            throw e;
        }
        return list;
    }

    public boolean updateProject(Project project) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(ProjectService.class);
        ProjectValidator.validate(project);
        Project before = getProjectById(project.getId()).orElse(null);
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
            boolean updated = preparedStatement.executeUpdate() > 0;
            if (updated) {
                StringBuilder changes = new StringBuilder();
                if (before != null) {
                    if (!java.util.Objects.equals(before.getName(), project.getName())) changes.append(String.format("name: %s->%s; ", before.getName(), project.getName()));
                    if (!java.util.Objects.equals(before.getDescription(), project.getDescription())) changes.append("description changed; ");
                    if (!java.util.Objects.equals(before.getBudgetLimit(), project.getBudgetLimit())) changes.append(String.format("budget_limit: %s->%s; ", before.getBudgetLimit(), project.getBudgetLimit()));
                    if (!java.util.Objects.equals(before.getStartDate(), project.getStartDate())) changes.append(String.format("start_date: %s->%s; ", before.getStartDate(), project.getStartDate()));
                    if (!java.util.Objects.equals(before.getEndDate(), project.getEndDate())) changes.append(String.format("end_date: %s->%s; ", before.getEndDate(), project.getEndDate()));
                    if (before.isActive() != project.isActive()) changes.append(String.format("is_active: %s->%s; ", before.isActive(), project.isActive()));
                }
                logger.info("Project updated: id={} changes={}", project.getId(), changes.toString());
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Failed to update project id={}", project.getId(), e);
            throw e;
        }
    }

    public boolean deleteProject(int id) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(ProjectService.class);
        if (id <= 0) {
            throw new IllegalArgumentException("Project ID must be positive.");
        }
        Project before = getProjectById(id).orElse(null);
        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            boolean deleted = preparedStatement.executeUpdate() > 0;
            if (deleted) {
                if (before != null) {
                    logger.info("Project deleted: id={} name={} companyId={}", before.getId(), before.getName(), before.getCompanyId());
                } else {
                    logger.info("Project deleted: id={}", id);
                }
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Failed to delete project id={}", id, e);
            throw e;
        }
    }

    private Project mapRow(ResultSet resultSet) throws SQLException {
        Project project = new Project();
        project.setId(resultSet.getInt("id"));
        project.setCompanyId(resultSet.getInt("company_id"));
        project.setName(resultSet.getString("name"));
        project.setDescription(resultSet.getString("description"));

        project.setBudgetLimit(resultSet.wasNull() ? null : resultSet.getBigDecimal("budget_limit"));

        Date startDate = resultSet.getDate("start_date");
        project.setStartDate(startDate != null ? startDate.toLocalDate() : null);
        
        Date endDate = resultSet.getDate("end_date");
        project.setEndDate(endDate != null ? endDate.toLocalDate() : null);

        project.setActive(resultSet.getBoolean("is_active"));
        project.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
        return project;
    }

    private void refreshExpiredProjects(PreparedStatement refreshStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int index = i + 1;
            if (param instanceof Integer value) {
                refreshStatement.setInt(index, value);
            } else {
                refreshStatement.setObject(index, param);
            }
        }
        refreshStatement.executeUpdate();
    }
}

