package org.example.model.database.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private Project createProject(BigDecimal spend, BigDecimal budget) {
        Project p = new Project();
        p.setCurrentSpend(spend);
        p.setBudgetLimit(budget);
        return p;
    }

    @Test
    void spend_percentage_normal() {
        Project p = createProject(
                new BigDecimal("50"),
                new BigDecimal("200")
        );

        assertEquals(0.25, p.getSpendPercentage());
    }

    @Test
    void spend_percentage_full() {
        Project p = createProject(
                new BigDecimal("200"),
                new BigDecimal("200")
        );

        assertEquals(1.0, p.getSpendPercentage());
    }

    @Test
    void spend_percentage_zero_spend() {
        Project p = createProject(
                BigDecimal.ZERO,
                new BigDecimal("200")
        );

        assertEquals(0.0, p.getSpendPercentage());
    }

    @Test
    void spend_percentage_null_spend() {
        Project p = createProject(
                null,
                new BigDecimal("200")
        );

        assertEquals(0.0, p.getSpendPercentage());
    }

    @Test
    void spend_percentage_null_budget() {
        Project p = createProject(
                new BigDecimal("100"),
                null
        );

        assertEquals(0.0, p.getSpendPercentage());
    }

    @Test
    void spend_percentage_zero_budget() {
        Project p = createProject(
                new BigDecimal("100"),
                BigDecimal.ZERO
        );

        assertEquals(0.0, p.getSpendPercentage());
    }

    @Test
    void spend_percentage_over_budget() {
        Project p = createProject(
                new BigDecimal("250"),
                new BigDecimal("200")
        );

        assertEquals(1.25, p.getSpendPercentage());
    }

    @Test
    void spend_percentage_rounding_half_up() {
        Project p = createProject(
                new BigDecimal("1"),
                new BigDecimal("3")
        );

        // 1 / 3 = 0.3333 (HALF_UP до 4 знаков)
        assertEquals(0.3333, p.getSpendPercentage());
    }
}