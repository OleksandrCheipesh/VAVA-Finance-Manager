package org.example.model.database.entity;

import java.time.OffsetDateTime;

public class Company {
    private int id;
    private String name;
    private String industry;
    private String country;
    private String currency;
    private OffsetDateTime createdAt;

    public Company() {}

    public Company(String name, String industry, String country, String currency) {
        this.name = name;
        this.industry = industry;
        this.country = country;
        this.currency = currency;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
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

