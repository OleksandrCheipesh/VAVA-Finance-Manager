package org.example.model.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Project {

    private int id;
    private int companyId;
    private String name;
    private String description;
    private BigDecimal budgetLimit;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private OffsetDateTime createdAt;
    private BigDecimal currentSpend = BigDecimal.ZERO;

    public Project(int companyId, String name, String description, BigDecimal budgetLimit,
                   LocalDate startDate, LocalDate endDate, boolean isActive) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.budgetLimit = budgetLimit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    public BigDecimal getCurrentSpend() {
        return currentSpend == null ? BigDecimal.ZERO : currentSpend;
    }

    public double getSpendPercentage() {
        if (budgetLimit == null || budgetLimit.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return getCurrentSpend().divide(budgetLimit, 4, java.math.RoundingMode.HALF_UP).doubleValue();
    }
}