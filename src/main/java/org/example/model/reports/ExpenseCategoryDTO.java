package org.example.model.reports;

import java.math.BigDecimal;

public class ExpenseCategoryDTO {

    private final String     category;
    private final BigDecimal amount;
    private final double     percentage;

    public ExpenseCategoryDTO(String category, BigDecimal amount, double percentage) {
        this.category   = category;
        this.amount     = amount;
        this.percentage = percentage;
    }

    public String     getCategory()   { return category; }
    public BigDecimal getAmount()     { return amount; }
    public double     getPercentage() { return percentage; }
}
