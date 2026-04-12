package org.example.model.models;

import org.example.SessionManager;
import org.example.model.database.entity.User;
import org.example.model.database.entity.Position;
import org.example.model.database.service.UserService;
import org.example.model.validation.RegisterValidator;

import java.sql.SQLException;

public class RegistrationModel {
    private final UserService userService = new UserService();

    public void register(String name, String surname, String email, String passwordHash) throws SQLException {
        var logger = org.example.logging.AppLog.getLogger(RegistrationModel.class);
        logger.info("Registration attempt for email={}", email);
        try {
            RegisterValidator.validate(name, surname, email, passwordHash);
            User user = new User(name, surname, email, passwordHash, Position.Director, null);
            userService.addUser(user);
            SessionManager.getInstance().login(user);
            logger.info("Registration successful: id={} email={}", user.getId(), user.getEmail());
        } catch (SQLException e) {
            logger.error("Registration failed for email={}", email, e);
            throw e;
        } catch (RuntimeException e) {
            logger.warn("Registration validation failed for email={}: {}", email, e.getMessage());
            throw e;
        }
    }
}