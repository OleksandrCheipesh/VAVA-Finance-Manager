package org.example.model.models;

import org.example.model.database.entity.Position;
import org.example.model.database.entity.User;
import org.example.model.database.service.UserService;
import org.example.SessionManager;

import java.sql.SQLException;
import java.util.List;


public class SettingsModel {

    private UserService userService = new UserService();
    private User currentUser = SessionManager.getInstance().getCurrentUser();

    public User addUser(User user) throws SQLException {
        if (currentUser.getPosition() == Position.Director ) {
            this.userService.addUser(user);
        }
        return user;
    }

    public void deleteUser(int userId) throws SQLException {
        if (currentUser.getPosition() == Position.Director ) {
            this.userService.deleteUser(userId);
        }
    }

    public void editUser(User user) throws SQLException {
        if (currentUser.getPosition() == Position.Director) {
            this.userService.updateUser(user);
        }
    }

    public List<User> getUsersByCompanyId(int companyId) throws SQLException {
        return this.userService.getUsersByCompanyId(companyId);
    }
}
