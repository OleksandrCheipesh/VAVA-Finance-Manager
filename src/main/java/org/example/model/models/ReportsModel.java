package org.example.model.models;

import org.example.model.database.entity.Employee;
import org.example.model.database.entity.Project;
import org.example.model.database.service.ProjectService;
import org.example.model.database.service.ReportService;
import org.example.model.reports.ExpenseCategoryDTO;
import org.example.model.reports.IncomeBreakdownDTO;
import org.example.model.reports.MonthlySnapshotDTO;
import org.example.model.reports.ProjectSummaryDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

public class ReportsModel {
    private final ReportService reportService = new ReportService();

    private final ProjectService projectService = new ProjectService();

    public List<ProjectSummaryDTO> getProjectSummaries(int companyId, LocalDate from, LocalDate to, Integer projectId) throws SQLException {
        return reportService.getProjectSummaries(companyId, from, to, projectId, null);
    }

    public List<MonthlySnapshotDTO> getMonthlySnapshots(int companyId, LocalDate from, LocalDate to, Integer projectId) throws SQLException {
        return reportService.getMonthlySnapshots(companyId, from, to, projectId, null);
    }

    public List<ExpenseCategoryDTO> getExpenseBreakdown(int companyId, LocalDate from, LocalDate to, Integer projectId, BigDecimal minAmount) throws SQLException {
        List<ExpenseCategoryDTO> raw = reportService.getExpenseBreakdown(companyId, from, to, projectId);
        if (minAmount != null) raw.removeIf(d -> d.getAmount().compareTo(minAmount) < 0);
        return raw;
    }

    public List<IncomeBreakdownDTO> getIncomeBreakdown(int companyId, LocalDate from, LocalDate to, Integer projectId, BigDecimal minAmount) throws SQLException {
        List<IncomeBreakdownDTO> raw = reportService.getIncomeBreakdown(companyId, from, to, projectId, null);
        if (minAmount != null) raw.removeIf(d -> d.getAmount().compareTo(minAmount) < 0);
        return raw;
    }

    public List<Employee> getEmployees(int companyId, Integer projectId) throws SQLException {
        return reportService.getEmployees(companyId, projectId);
    }

    public List<Project> getProjects(int companyId) throws SQLException {
        return projectService.getProjectsByCompanyId(companyId);
    }
}
