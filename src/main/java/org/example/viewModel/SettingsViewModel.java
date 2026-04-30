package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.SessionManager;
import org.example.view.templates.I18n;
import org.example.model.database.entity.Company;
import org.example.model.database.entity.User;
import org.example.model.PasswordUtil;
import org.example.model.database.service.CompanyService;
import org.example.model.database.service.UserService;
import org.example.model.models.SettingsModel;
import org.example.model.validation.CompanyValExept;
import org.example.model.validation.CompanyValidator;
import org.example.model.validation.RegisterValidator;

import java.sql.SQLException;

public class SettingsViewModel {

    private final StringProperty companyName = new SimpleStringProperty("");
    private final StringProperty country = new SimpleStringProperty("");
    private final StringProperty currency = new SimpleStringProperty("");
    private final StringProperty industry = new SimpleStringProperty("");

    private final StringProperty language = new SimpleStringProperty(SessionManager.getInstance().getLanguage());
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
        try {
            Company company = SessionManager.getInstance().getCurrentCompany();
            companyName.set(company.getName());
            country.set(company.getCountry());
            currency.set(company.getCurrency());
            industry.set(company.getIndustry());
        } catch (Exception e) {
            companyName.set("Unknown");
            country.set("Unknown");
            currency.set("Unknown");
            industry.set("Unknown");
        }

        try {
            int companyId = SessionManager.getInstance().getCurrentUser().getCompanyId();
            users.addAll(settingsModel.getUsersByCompanyId(companyId));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

        public void saveCompanyProfile(String name,String industry, String country, String currency) throws CompanyValExept {
            Company company = new Company(name,industry,country,currency);
            company.setId(SessionManager.getInstance().getCurrentCompany().getId());
            CompanyValidator.validate(company);
            CompanyService db = new CompanyService();
            try {
                db.updateCompany(company);
                SessionManager.getInstance().setCurrentCompany(company);
            } catch (SQLException e) {
//                TODO: loging
            }

    }

    public void applyPreferences() {
        I18n.setLanguage(language.get());
        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            new UserService().updateLanguage(userId, language.get());
            SessionManager.getInstance().getCurrentUser().setLanguage(language.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if (currentPw == null || currentPw.isBlank())
            throw new Exception("Current password is required.");

        if (!PasswordUtil.verify(currentPw, currentUser.getPasswordHash()))
            throw new Exception("Incorrect current password.");

        RegisterValidator.validatePassword(newPw);

        if (!newPw.equals(confirmPw))
            throw new Exception("Passwords do not match.");

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