package org.example.model.models;

import org.example.SessionManager;
import org.example.model.validation.RegisterValidator;
import org.example.model.PasswordUtil;

public class RegistrationModel {
    public void register(String name, String surname, String email, String passwordHash) {
        var logger = org.example.logging.AppLog.getLogger(RegistrationModel.class);
        logger.info("Registration attempt for email={}", email);
        try {
            RegisterValidator.validate(name, surname, email, passwordHash);
            String hashed = PasswordUtil.hash(passwordHash);
            SessionManager.getInstance().setPendingRegistration(name, surname, email, hashed);
            logger.info("Registration data stored for email={}", email);
        } catch (RuntimeException e) {
            logger.warn("Registration validation failed for email={}: {}", email, e.getMessage());
            throw e;
        }
    }
}