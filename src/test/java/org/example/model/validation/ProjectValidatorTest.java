package org.example.model.validation;

import org.example.model.database.entity.Project;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.model.validation.ProjValExept.ProjErrorCode;

class ProjectValidatorTest {

    private Project validProject() {
        Project p = new Project();
        p.setName("Project X");
        p.setBudgetLimit(new BigDecimal("1000"));
        p.setCurrentSpend(new BigDecimal("200"));
        p.setStartDate(LocalDate.of(2025, 1, 1));
        p.setEndDate(LocalDate.of(2025, 12, 31));
        return p;
    }

    @Test
    void valid_project_passes() {
        assertDoesNotThrow(() -> ProjectValidator.validate(validProject()));
    }

    @Test
    void null_project_throws() {
        ProjValExept ex = assertThrows(ProjValExept.class,
                () -> ProjectValidator.validate(null));

        assertEquals(ProjErrorCode.PROJECT_NULL, ex.getCode());
    }


    @Test
    void blank_name_throws() {
        Project p = validProject();
        p.setName(" ");

        ProjValExept ex = assertThrows(ProjValExept.class,
                () -> ProjectValidator.validate(p));

        assertEquals(ProjErrorCode.NAME_EMPTY, ex.getCode());
    }


    @Test
    void negative_budget_throws() {
        Project p = validProject();
        p.setBudgetLimit(new BigDecimal("-1"));

        ProjValExept ex = assertThrows(ProjValExept.class,
                () -> ProjectValidator.validate(p));

        assertEquals(ProjErrorCode.BUDGET_NEGATIVE, ex.getCode());
    }

    @Test
    void zero_budget_passes() {
        Project p = validProject();
        p.setBudgetLimit(BigDecimal.ZERO);

        assertDoesNotThrow(() -> ProjectValidator.validate(p));
    }

    @Test
    void null_budget_passes() {
        Project p = validProject();
        p.setBudgetLimit(null);

        assertDoesNotThrow(() -> ProjectValidator.validate(p));
    }


    @Test
    void start_after_end_throws() {
        Project p = validProject();
        p.setStartDate(LocalDate.of(2025, 12, 31));
        p.setEndDate(LocalDate.of(2025, 1, 1));

        ProjValExept ex = assertThrows(ProjValExept.class,
                () -> ProjectValidator.validate(p));

        assertEquals(ProjErrorCode.DATE_INVALID_RANGE, ex.getCode());
    }

    @Test
    void start_equals_end_passes() {
        Project p = validProject();
        p.setStartDate(LocalDate.of(2025, 1, 1));
        p.setEndDate(LocalDate.of(2025, 1, 1));

        assertDoesNotThrow(() -> ProjectValidator.validate(p));
    }

    @Test
    void null_dates_pass() {
        Project p = validProject();
        p.setStartDate(null);
        p.setEndDate(null);

        assertDoesNotThrow(() -> ProjectValidator.validate(p));
    }


    @Test
    void spend_exceeds_budget_throws() {
        Project p = validProject();
        p.setCurrentSpend(new BigDecimal("2000"));

        ProjValExept ex = assertThrows(ProjValExept.class,
                () -> ProjectValidator.validate(p));

        assertEquals(ProjErrorCode.SPEND_EXCEEDS_BUDGET, ex.getCode());
    }

    @Test
    void spend_equal_to_budget_passes() {
        Project p = validProject();
        p.setCurrentSpend(new BigDecimal("1000"));

        assertDoesNotThrow(() -> ProjectValidator.validate(p));
    }

    @Test
    void spend_with_null_budget_passes() {
        Project p = validProject();
        p.setBudgetLimit(null);
        p.setCurrentSpend(new BigDecimal("999999"));

        assertDoesNotThrow(() -> ProjectValidator.validate(p));
    }
}