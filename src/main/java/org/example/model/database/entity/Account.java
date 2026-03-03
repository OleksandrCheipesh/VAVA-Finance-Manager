package org.example.model.database.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Account {
    private int id;
    private int companyId;
    private String accountName;
    private BigDecimal currentBalance;
    private String currency;
    private OffsetDateTime createdAt;

    public Account() {}

    public Account(int companyId, String accountName, BigDecimal currentBalance, String currency) {
        this.companyId = companyId;
        this.accountName = accountName;
        this.currentBalance = currentBalance;
        this.currency = currency;
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

    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

