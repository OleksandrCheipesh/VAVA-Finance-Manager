package org.example.model;

import org.example.entity.User;
import org.example.validation.RegisterValidator;


public class RegistrationModel {
    private User user;

    public void register(String login, String email, String password) {

       RegisterValidator.validate(login, email, password);

        // here will be adding to database

        this.user = new User(login, email, password);
    }
}
