package org.example.model.validation;

import org.example.model.database.entity.Company;

import static org.example.model.validation.CompanyValExept.CompanyErrorCode;

public class CompanyValidator {

    public static void validate(Company company) throws CompanyValExept {

        if (company == null) {
            throw new CompanyValExept(CompanyErrorCode.COMPANY_ERR, "Company cannot be null.");
        }

        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new CompanyValExept(CompanyErrorCode.NAME_ERR, "Company name cannot be empty.");
        }

        if (company.getIndustry() == null || company.getIndustry().trim().isEmpty()) {
            throw new CompanyValExept(CompanyErrorCode.INDUSTRY_ERR, "Industry cannot be empty.");
        }

        if (company.getCountry() == null || company.getCountry().trim().isEmpty()) {
            throw new CompanyValExept(CompanyErrorCode.COUNTRY_ERR, "Country cannot be empty.");
        }

        if (company.getCurrency() == null || company.getCurrency().trim().isEmpty()) {
            throw new CompanyValExept(CompanyErrorCode.CURRENCY_ERR, "Currency cannot be empty.");
        }
    }
}