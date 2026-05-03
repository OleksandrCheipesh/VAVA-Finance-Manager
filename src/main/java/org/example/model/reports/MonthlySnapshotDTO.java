package org.example.model.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
public class MonthlySnapshotDTO {

    private final YearMonth period;
    private final BigDecimal income;
    private final BigDecimal expense;
    private final BigDecimal netProfit;
}