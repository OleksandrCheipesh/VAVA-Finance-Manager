package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.SessionManager;
import org.example.UserPreferences;
import org.example.model.models.LoginModel; // Using your existing model

public class LoginViewModel {

    private final LoginModel loginModel = new LoginModel();
    private final StringProperty message = new SimpleStringProperty("");

    public void login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            message.set("Email and Password are required.");
            return;
        }

        try {
            // Note: Update your LoginM validateUser method to accept email instead of username if needed
            loginModel.validateUser(email, password);
            UserPreferences.getInstance().init(
                    "English",
                    SessionManager.getInstance().getCurrentCompany().getCurrency()
            );
            message.set("Login successful!");
        } catch (Exception e) {
            message.set(e.getMessage());
        }
    }

    public StringProperty messageProperty() {
        return message;
    }
}