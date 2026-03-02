package org.example.model;

import org.example.model.database.entity.User;
import org.example.model.database.service.UserService;
import org.example.model.validation.RegisterValidator;

import java.sql.SQLException;

public class RegistrationModel {
    private final UserService userService = new UserService();

    public void register(String name, String surname, String email, String passwordHash) throws SQLException {
        // RegisterValidator.validate(name, email, passwordHash);

        User user = new User(name, surname, email, passwordHash, "Director", null);
        userService.addUser(user);
    }
}
