package org.example.model.reports;

import java.math.BigDecimal;

public class ProjectSummaryDTO {

    private final int    projectId;
    private final String projectName;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal netProfit;
    private final Double monthOverMonthPct;

    public ProjectSummaryDTO(int projectId, String projectName,
                             BigDecimal totalIncome, BigDecimal totalExpense,
                             BigDecimal netProfit, Double monthOverMonthPct) {
        this.projectId          = projectId;
        this.projectName        = projectName;
        this.totalIncome        = totalIncome;
        this.totalExpense       = totalExpense;
        this.netProfit          = netProfit;
        this.monthOverMonthPct  = monthOverMonthPct;
    }

    public int        getProjectId()         { return projectId; }
    public String     getProjectName()       { return projectName; }
    public BigDecimal getTotalIncome()       { return totalIncome; }
    public BigDecimal getTotalExpense()      { return totalExpense; }
    public BigDecimal getNetProfit()         { return netProfit; }
    public Double     getMonthOverMonthPct() { return monthOverMonthPct; }
}
