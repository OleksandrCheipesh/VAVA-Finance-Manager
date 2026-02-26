package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.model.RegistrationModel;

public class RegistrationViewModel {

    private final RegistrationModel service = new RegistrationModel();

    private final StringProperty message = new SimpleStringProperty("");

    public void register(String login, String email, String password) {
        try {
            service.register(login, email, password);
            message.set("Registration successful!");
        } catch (IllegalArgumentException e) {
            message.set(e.getMessage());
        }
    }

    public StringProperty messageProperty() {
        return message;
    }
}