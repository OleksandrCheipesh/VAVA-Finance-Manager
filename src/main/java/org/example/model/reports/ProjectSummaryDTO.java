package org.example.model.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProjectSummaryDTO {

    private final int projectId;
    private final String projectName;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal netProfit;
    private final Double monthOverMonthPct;
}