package org.example.model.reports;

import java.math.BigDecimal;

public class SummaryDTO {

    private final BigDecimal totalRevenue;
    private final BigDecimal totalCosts;
    private final BigDecimal grossProfit;
    private final BigDecimal netProfit;

    public SummaryDTO(BigDecimal totalRevenue,
                      BigDecimal totalCosts,
                      BigDecimal grossProfit,
                      BigDecimal netProfit) {
        this.totalRevenue = totalRevenue;
        this.totalCosts = totalCosts;
        this.grossProfit = grossProfit;
        this.netProfit = netProfit;
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getTotalCosts() { return totalCosts; }
    public BigDecimal getGrossProfit() { return grossProfit; }
    public BigDecimal getNetProfit() { return netProfit; }
}
