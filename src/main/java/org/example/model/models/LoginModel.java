package org.example.model.models;

import org.example.SessionManager;
import org.example.model.PasswordUtil;
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
        var logger = org.example.logging.AppLog.getLogger(LoginModel.class);
        try {
            logger.debug("Looking up user by email={}", email);
            Optional<User> found = userService.getUserByEmail(email);
            if (found.isEmpty()) {
                logger.debug("User not found for email={}", email);
                return false;
            }
            this.user = found.get();
            logger.debug("User lookup success: id={} email={}", this.user.getId(), this.user.getEmail());
            return true;
        } catch (SQLException e) {
            logger.error("User lookup failed for email={}", email, e);
            throw new RuntimeException(e);
        }

    }

    private boolean verifierOfPassword(String password) {
        var logger = org.example.logging.AppLog.getLogger(LoginModel.class);
        if (this.user == null) {
            logger.debug("Password verification skipped: no user present for verification");
            return false;
        }
        boolean ok = PasswordUtil.verify(password, this.user.getPasswordHash());
        if (ok) logger.debug("Password verification passed for user id={} email={}", this.user.getId(), this.user.getEmail());
        else logger.debug("Password verification failed for user id={} email={}", this.user.getId(), this.user.getEmail());
        return ok;
    }

    public void validateUser(String email, String password) throws Exception {
        var logger = org.example.logging.AppLog.getLogger(LoginModel.class);
        logger.info("Login attempt for email={}", email);
        boolean userExists = verifierOfUser(email);
        boolean pwdOk = userExists && verifierOfPassword(password);
        if (!userExists) {
            logger.warn("Login failed: user not found for email={}", email);
            throw new Exception("The user or password is incorrect!");
        }
        if (!pwdOk) {
            logger.warn("Login failed: invalid credentials for email={}", email);
            throw new Exception("The user or password is incorrect!");
        }
        SessionManager.getInstance().login(this.user);
        logger.info("Login successful: id={} email={}", this.user.getId(), this.user.getEmail());
    }

}
