package org.example.model.validation;

import org.example.model.database.entity.Client;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ClientValidatorTest {

    private Client validClient() {
        Client c = new Client();
        c.setName("John");
        c.setSurname("Doe");
        c.setEmail("john@doe.com");
        c.setPhone("+421 910 123 456");
        c.setMonthlyIncome(new BigDecimal("1000"));
        return c;
    }

    @Test
    void valid_client_passes() {
        assertDoesNotThrow(() -> ClientValidator.validate(validClient()));
    }

    @Test
    void null_client_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> ClientValidator.validate(null));
    }



    @Test
    void name_too_short_throws() {
        Client c = validClient();
        c.setName("A");

        assertThrows(IllegalArgumentException.class,
                () -> ClientValidator.validate(c));
    }

    @Test
    void name_too_long_throws() {
        Client c = validClient();
        c.setName("A".repeat(129));

        assertThrows(IllegalArgumentException.class,
                () -> ClientValidator.validate(c));
    }

    @Test
    void name_boundary_2_chars_passes() {
        Client c = validClient();
        c.setName("AB");

        assertDoesNotThrow(() -> ClientValidator.validate(c));
    }

    @Test
    void name_trimmed_too_short_throws() {
        Client c = validClient();
        c.setName("  A ");

        assertThrows(IllegalArgumentException.class,
                () -> ClientValidator.validate(c));
    }



    @Test
    void surname_too_short_throws() {
        Client c = validClient();
        c.setSurname("A");

        assertThrows(IllegalArgumentException.class,
                () -> ClientValidator.validate(c));
    }



    @Test
    void invalid_email_throws() {
        Client c = validClient();
        c.setEmail("abc@");

        assertThrows(IllegalArgumentException.class,
                () -> ClientValidator.validate(c));
    }

    @Test
    void valid_email_passes() {
        Client c = validClient();
        c.setEmail("a@b.com");

        assertDoesNotThrow(() -> ClientValidator.validate(c));
    }

    @Test
    void null_email_passes() {
        Client c = validClient();
        c.setEmail(null);

        assertDoesNotThrow(() -> ClientValidator.validate(c));
    }

    @Test
    void blank_email_passes() {
        Client c = validClient();
        c.setEmail("   ");

        assertDoesNotThrow(() -> ClientValidator.validate(c));
    }



    @Test
    void invalid_phone_throws() {
        Client c = validClient();
        c.setPhone("abc");

        assertThrows(IllegalArgumentException.class,
                () -> ClientValidator.validate(c));
    }

    @Test
    void valid_phone_passes() {
        Client c = validClient();
        c.setPhone("+421 910 123 456");

        assertDoesNotThrow(() -> ClientValidator.validate(c));
    }

    @Test
    void null_phone_passes() {
        Client c = validClient();
        c.setPhone(null);

        assertDoesNotThrow(() -> ClientValidator.validate(c));
    }



    @Test
    void negative_monthly_income_throws() {
        Client c = validClient();
        c.setMonthlyIncome(new BigDecimal("-1"));

        assertThrows(IllegalArgumentException.class,
                () -> ClientValidator.validate(c));
    }

    @Test
    void zero_monthly_income_passes() {
        Client c = validClient();
        c.setMonthlyIncome(BigDecimal.ZERO);

        assertDoesNotThrow(() -> ClientValidator.validate(c));
    }

    @Test
    void null_monthly_income_passes() {
        Client c = validClient();
        c.setMonthlyIncome(null);

        assertDoesNotThrow(() -> ClientValidator.validate(c));
    }
}