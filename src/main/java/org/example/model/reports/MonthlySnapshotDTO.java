package org.example.model.reports;

import java.math.BigDecimal;
import java.time.YearMonth;

public class MonthlySnapshotDTO {

    private final YearMonth  period;
    private final BigDecimal income;
    private final BigDecimal expense;
    private final BigDecimal netProfit;

    public MonthlySnapshotDTO(YearMonth period,
                              BigDecimal income,
                              BigDecimal expense,
                              BigDecimal netProfit) {
        this.period    = period;
        this.income    = income;
        this.expense   = expense;
        this.netProfit = netProfit;
    }

    public YearMonth  getPeriod()    { return period; }
    public BigDecimal getIncome()    { return income; }
    public BigDecimal getExpense()   { return expense; }
    public BigDecimal getNetProfit() { return netProfit; }
}
