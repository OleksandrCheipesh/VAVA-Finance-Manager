package org.example.model.models;

import org.example.SessionManager;
import org.example.model.database.entity.User;
import org.example.model.database.service.UserService;

import java.sql.SQLException;
import java.util.Optional;

public class LoginModel {
    private UserService userService;
    private User user;

    public LoginModel() {
        this.userService = new UserService();
    }


    private boolean verifierOfUser(String email) {
        try {
            Optional<User> found = userService.getUserByEmail(email);
            if (found.isEmpty()) return false;
            this.user = found.get();
            return true;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean verifierOfPassword(String password) {
        if (password.equals(this.user.getPasswordHash()))
        {
            return true;
        }
        return false;
    }

    public void validateUser(String email, String password) throws Exception {
        if (!verifierOfUser(email)|| !verifierOfPassword(password)) {
            throw new Exception("The user or password is incorrect!");
        }
        SessionManager.getInstance().login(this.user);
    }

}
