package org.example.model.validation;

import org.example.model.database.entity.Company;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.model.validation.CompanyValExept.CompanyErrorCode;

class CompanyValidatorTest {

    private Company validCompany() {
        Company c = new Company();
        c.setName("TestCompany");
        c.setIndustry("IT");
        c.setCountry("Slovakia");
        c.setCurrency("EUR");
        return c;
    }

    @Test
    void valid_company_passes() {
        assertDoesNotThrow(() -> CompanyValidator.validate(validCompany()));
    }

    @Test
    void null_company_throws() {
        CompanyValExept ex = assertThrows(CompanyValExept.class,
                () -> CompanyValidator.validate(null));

        assertEquals(CompanyErrorCode.COMPANY_ERR, ex.getCode());
    }



    @Test
    void null_name_throws() {
        Company c = validCompany();
        c.setName(null);

        CompanyValExept ex = assertThrows(CompanyValExept.class,
                () -> CompanyValidator.validate(c));

        assertEquals(CompanyErrorCode.NAME_ERR, ex.getCode());
    }

    @Test
    void blank_name_throws() {
        Company c = validCompany();
        c.setName("   ");

        CompanyValExept ex = assertThrows(CompanyValExept.class,
                () -> CompanyValidator.validate(c));

        assertEquals(CompanyErrorCode.NAME_ERR, ex.getCode());
    }



    @Test
    void blank_industry_throws() {
        Company c = validCompany();
        c.setIndustry("");

        CompanyValExept ex = assertThrows(CompanyValExept.class,
                () -> CompanyValidator.validate(c));

        assertEquals(CompanyErrorCode.INDUSTRY_ERR, ex.getCode());
    }


    @Test
    void blank_country_throws() {
        Company c = validCompany();
        c.setCountry(" ");

        CompanyValExept ex = assertThrows(CompanyValExept.class,
                () -> CompanyValidator.validate(c));

        assertEquals(CompanyErrorCode.COUNTRY_ERR, ex.getCode());
    }


    @Test
    void blank_currency_throws() {
        Company c = validCompany();
        c.setCurrency(null);

        CompanyValExept ex = assertThrows(CompanyValExept.class,
                () -> CompanyValidator.validate(c));

        assertEquals(CompanyErrorCode.CURRENCY_ERR, ex.getCode());
    }
}