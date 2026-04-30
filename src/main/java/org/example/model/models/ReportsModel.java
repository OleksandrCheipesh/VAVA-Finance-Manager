package org.example.model.models;

import org.example.model.database.service.ReportService;
import org.example.model.reports.ExpenseCategoryDTO;
import org.example.model.reports.IncomeBreakdownDTO;
import org.example.model.reports.MonthlySnapshotDTO;
import org.example.model.reports.ProjectSummaryDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReportsModel {
    private final ReportService reportService = new ReportService();

    public List<ProjectSummaryDTO> getProjectSummaries(int companyId, LocalDate dateFrom, LocalDate dateTo, Integer projectId, String projectStatus) throws SQLException {
        return reportService.getProjectSummaries(companyId, dateFrom, dateTo, projectId, projectStatus);
    }

    public List<MonthlySnapshotDTO> getMonthlySummaries(int companyId, LocalDate dateFrom, LocalDate dateTo, Integer projectId, String projectStatus) throws SQLException {
        return reportService.getMonthlySnapshots(companyId, dateFrom, dateTo, projectId, projectStatus);
    }

    public List<ExpenseCategoryDTO> getExpenseBreakdown(int companyId, LocalDate dateFrom, LocalDate dateTo, Integer projectId) throws SQLException {
        return reportService.getExpenseBreakdown(companyId, dateFrom, dateTo, projectId);
    }

    public List<IncomeBreakdownDTO> getIncomeBreakdown(int companyId, LocalDate dateFrom, LocalDate dateTo, Integer projectId, String projectStatus) throws SQLException {
        return reportService.getIncomeBreakdown(companyId, dateFrom, dateTo, projectId, projectStatus);
    }
}

