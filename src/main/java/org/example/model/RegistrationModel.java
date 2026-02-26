package org.example.model;

import org.example.model.database.entity.User;
import org.example.model.validation.RegisterValidator;


public class RegistrationModel {
    private User user;

    public void register(String login, String email, String password) {

       // RegisterValidator.validate(login, email, password);

        // here will be adding to database

        this.user = new User(login, email, password);
    }
}
