package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.model.RegistrationModel;

import java.sql.SQLException;

public class RegistrationViewModel {

    private final RegistrationModel service = new RegistrationModel();

    private final StringProperty message = new SimpleStringProperty("");

    public void register(String name, String surname, String email, String password) {
        try {
            service.register(name, surname, email, password);
            message.set("Registration successful!");
        } catch (IllegalArgumentException e) {
            message.set(e.getMessage());
        } catch (SQLException e) {
            message.set("Registration failed: " + e.getMessage());
        }
    }

    public StringProperty messageProperty() {
        return message;
    }
}