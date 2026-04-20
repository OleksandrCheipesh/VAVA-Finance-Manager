package org.example.model.reports;

import java.math.BigDecimal;

public class IncomeBreakdownDTO {

    private final String     projectName;
    private final BigDecimal amount;
    private final double     percentage;

    public IncomeBreakdownDTO(String projectName, BigDecimal amount, double percentage) {
        this.projectName = projectName;
        this.amount      = amount;
        this.percentage  = percentage;
    }

    public String     getProjectName() { return projectName; }
    public BigDecimal getAmount()      { return amount; }
    public double     getPercentage()  { return percentage; }
}
