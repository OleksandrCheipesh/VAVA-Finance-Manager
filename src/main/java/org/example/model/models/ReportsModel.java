package org.example.model.models;

import org.example.model.database.entity.Employee;
import org.example.model.database.entity.Project;
import org.example.model.database.service.ProjectService;
import org.example.model.database.service.ReportService;
import org.example.model.reports.ExpenseCategoryDTO;
import org.example.model.reports.IncomeBreakdownDTO;
import org.example.model.reports.MonthlySnapshotDTO;
import org.example.model.reports.ProjectSummaryDTO;
import org.example.model.reports.SummaryDTO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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

    public void exportXML(File file, LocalDate from, LocalDate to,
                          SummaryDTO summary,
                          List<MonthlySnapshotDTO> monthlySummaries,
                          List<ProjectSummaryDTO> projectSummaries,
                          List<ExpenseCategoryDTO> expenseCategories) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().newDocument();

        Element root = doc.createElement("report");
        root.setAttribute("generatedAt", java.time.LocalDate.now().toString());
        if (from != null && to != null) {
            root.setAttribute("period", from + " - " + to);
        }
        doc.appendChild(root);

        Element summaryEl = doc.createElement("summary");
        Element revenue = doc.createElement("totalRevenue");
        revenue.setTextContent(summary.getTotalRevenue().toString());
        Element costs = doc.createElement("totalCosts");
        costs.setTextContent(summary.getTotalCosts().toString());
        Element gross = doc.createElement("grossProfit");
        gross.setTextContent(summary.getGrossProfit().toString());
        Element net = doc.createElement("netProfit");
        net.setTextContent(summary.getNetProfit().toString());
        summaryEl.appendChild(revenue);
        summaryEl.appendChild(costs);
        summaryEl.appendChild(gross);
        summaryEl.appendChild(net);
        root.appendChild(summaryEl);

        Element monthly = doc.createElement("monthlySnapshots");
        for (MonthlySnapshotDTO m : monthlySummaries) {
            Element month = doc.createElement("month");
            month.setAttribute("period",    m.getPeriod().toString());
            month.setAttribute("income",    m.getIncome().toString());
            month.setAttribute("expense",   m.getExpense().toString());
            month.setAttribute("netProfit", m.getNetProfit().toString());
            monthly.appendChild(month);
        }
        root.appendChild(monthly);

        Element projects = doc.createElement("projects");
        for (ProjectSummaryDTO p : projectSummaries) {
            Element project = doc.createElement("project");
            project.setAttribute("name",         p.getProjectName());
            project.setAttribute("totalIncome",  p.getTotalIncome().toString());
            project.setAttribute("totalExpense", p.getTotalExpense().toString());
            project.setAttribute("netProfit",    p.getNetProfit().toString());
            projects.appendChild(project);
        }
        root.appendChild(projects);

        Element expenses = doc.createElement("expenses");
        for (ExpenseCategoryDTO e : expenseCategories) {
            Element category = doc.createElement("category");
            category.setAttribute("name",       e.getCategory());
            category.setAttribute("amount",     e.getAmount().toString());
            category.setAttribute("percentage", String.valueOf(e.getPercentage()));
            expenses.appendChild(category);
        }
        root.appendChild(expenses);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }
}
