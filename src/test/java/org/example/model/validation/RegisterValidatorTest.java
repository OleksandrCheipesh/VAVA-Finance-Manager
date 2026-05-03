package org.example.model.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterValidatorTest {


    @Test
    void valid_registration_passes() {
        assertDoesNotThrow(() ->
                RegisterValidator.validate("John", "Doe", "a@b.com", "Secure@1"));
    }

    @Test
    void blank_name_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validate(null, "Doe", "a@b.com", "Secure@1"));
    }

    @Test
    void blank_surname_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validate("John", " ", "a@b.com", "Secure@1"));
    }

    @Test
    void invalid_email_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validate("John", "Doe", "abc@", "Secure@1"));
    }

    @Test
    void weak_password_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validate("John", "Doe", "a@b.com", "123"));
    }

    @Test
    void password_null_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validatePassword(null));
    }

    @Test
    void password_blank_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validatePassword(" "));
    }

    @Test
    void password_too_short_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validatePassword("A1@b"));
    }

    @Test
    void password_no_uppercase_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validatePassword("secure@1"));
    }

    @Test
    void password_no_lowercase_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validatePassword("SECURE@1"));
    }

    @Test
    void password_no_special_char_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RegisterValidator.validatePassword("Secure123"));
    }

    @Test
    void valid_password_passes() {
        assertDoesNotThrow(() ->
                RegisterValidator.validatePassword("Secure@1"));
    }

    @Test
    void boundary_8_chars_passes() {
        assertDoesNotThrow(() ->
                RegisterValidator.validatePassword("Abcdef@1"));
    }
}