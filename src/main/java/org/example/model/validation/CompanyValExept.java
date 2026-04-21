package org.example.model.validation;

public class CompanyValExept extends IllegalArgumentException {

    public enum CompanyErrorCode {
        COMPANY_ERR,
        NAME_ERR,
        INDUSTRY_ERR,
        COUNTRY_ERR,
        CURRENCY_ERR
    }

    private final CompanyErrorCode code;

    public CompanyValExept(CompanyErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public CompanyErrorCode getCode() {
        return code;
    }
}