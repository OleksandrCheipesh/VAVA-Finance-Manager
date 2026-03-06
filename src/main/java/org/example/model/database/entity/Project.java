package org.example.model.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

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

    public Project() {}

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

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }
    public void setBudgetLimit(BigDecimal budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

