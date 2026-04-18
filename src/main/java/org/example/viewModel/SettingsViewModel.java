package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.SessionManager;
import org.example.model.database.entity.User;
import org.example.model.PasswordUtil;
import org.example.model.database.service.UserService;
import org.example.model.database.entity.Position;
import org.example.model.models.SettingsModel;

import java.sql.SQLException;

public class SettingsViewModel {

    private final StringProperty companyName = new SimpleStringProperty("");
    private final StringProperty country = new SimpleStringProperty("");
    private final StringProperty currency = new SimpleStringProperty("");
    private final StringProperty industry = new SimpleStringProperty("");

    private final StringProperty language = new SimpleStringProperty("English");
    private final StringProperty dateFormat = new SimpleStringProperty("DD/MM/YYYY");

    private final ObservableList<User> users = FXCollections.observableArrayList();

    private final SettingsModel settingsModel = new SettingsModel();

    public SettingsViewModel() {
        try {
            loadInitialData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInitialData() {
        companyName.set("Mint Management");
        country.set("Slovakia");
        currency.set("EUR (€)");
        industry.set("Financial Services");

        try {
            int companyId = SessionManager.getInstance().getCurrentUser().getCompanyId();
            users.addAll(settingsModel.getUsersByCompanyId(companyId));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void saveCompanyProfile() {
        System.out.println("Saving Company: " + companyName.get());
    }

    public void applyPreferences() {
        System.out.println("Applying preferences...");
    }

    public void addUser(User user) {
        try {
            if (user.getCompanyId() == null) {
                user.setCompanyId(SessionManager.getInstance().getCurrentCompanyId());
            }
            settingsModel.addUser(user);
            users.add(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        this.settingsModel.deleteUser(userId);
        users.removeIf(u -> u.getId() == userId);
    }

    public void editUser(User user) throws SQLException {
        this.settingsModel.editUser(user);
        users.replaceAll(u -> u.getId() == user.getId() ? user : u);
    }

    public void changePassword(String currentPw, String newPw, String confirmPw) throws Exception {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) throw new Exception("No user logged in.");

        if (!PasswordUtil.verify(currentPw, currentUser.getPasswordHash())) {
            throw new Exception("Incorrect current password.");
        }

        if (!newPw.equals(confirmPw)) {
            throw new Exception("Password confirmation doesn't match.");
        }

        currentUser.setPasswordHash(PasswordUtil.hash(newPw));
        UserService userService = new UserService();
        userService.updateUser(currentUser);
    }

    public StringProperty companyNameProperty() { return companyName; }
    public StringProperty countryProperty() { return country; }
    public StringProperty currencyProperty() { return currency; }
    public StringProperty industryProperty() { return industry; }
    public StringProperty languageProperty() { return language; }
    public StringProperty dateFormatProperty() { return dateFormat; }

    public ObservableList<User> getUsers() { return users; }
}