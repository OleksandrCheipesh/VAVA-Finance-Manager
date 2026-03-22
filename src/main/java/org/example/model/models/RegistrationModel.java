package org.example.model.models;

import org.example.model.database.entity.User;
import org.example.model.database.entity.Position;
import org.example.model.database.service.UserService;
import org.example.model.validation.RegisterValidator;

import java.sql.SQLException;

public class RegistrationModel {
    private final UserService userService = new UserService();

    public void register(String name, String surname, String email, String passwordHash) throws SQLException {
        // Run the validator first!
        RegisterValidator.validate(name, surname, email, passwordHash);

        User user = new User(name, surname, email, passwordHash, Position.Director, null);
        userService.addUser(user);
    }
}