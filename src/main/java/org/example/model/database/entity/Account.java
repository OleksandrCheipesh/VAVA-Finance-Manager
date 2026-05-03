package org.example.model.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Account {

    private int id;
    private int companyId;
    private String accountName;
    private BigDecimal currentBalance;
    private String currency;
    private OffsetDateTime createdAt;
    private Integer limitAmount;
    private AccountCategory category;
    private AccountCycle cycle;

    public Account(int companyId, String accountName, BigDecimal currentBalance, String currency) {
        this.companyId = companyId;
        this.accountName = accountName;
        this.currentBalance = currentBalance;
        this.currency = currency;
    }
}