package org.example.model.validation;

import org.example.model.database.entity.Client;

import java.math.BigDecimal;

public class ClientValidator {

    public static void validate(Client client) throws IllegalArgumentException {
        if (client == null)
            throw new IllegalArgumentException("Client cannot be null.");

        String name = client.getName() == null ? "" : client.getName().trim();
        if (name.length() < 2 || name.length() > 128)
            throw new IllegalArgumentException("Name must be 2–128 characters.");

        String surname = client.getSurname() == null ? "" : client.getSurname().trim();
        if (surname.length() < 2 || surname.length() > 128)
            throw new IllegalArgumentException("Surname must be 2–128 characters.");

        if (client.getEmail() != null && !client.getEmail().isBlank()) {
            if (!client.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
                throw new IllegalArgumentException("Email is not valid.");
        }

        if (client.getPhone() != null && !client.getPhone().isBlank()) {
            if (!client.getPhone().matches("^\\+?[0-9 \\-()]{7,20}$"))
                throw new IllegalArgumentException("Phone number is not valid (e.g. +1 555-000-0000).");
        }

        if (client.getMonthlyIncome() != null && client.getMonthlyIncome().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Monthly income must be ≥ 0.");
    }
}