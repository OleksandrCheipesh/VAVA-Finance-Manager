package org.example.model.database.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Client extends Person {
    private int companyId;
    private String phone;
    private BigDecimal monthlyIncome;
    private OffsetDateTime createdAt;

    public Client() {}

    public Client(int companyId, String name, String surname, String email, String phone, BigDecimal monthlyIncome) {
        this.companyId = companyId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.monthlyIncome = monthlyIncome;
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }
    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
