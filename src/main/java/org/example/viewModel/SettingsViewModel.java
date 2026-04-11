package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SettingsViewModel {

    private final StringProperty companyName = new SimpleStringProperty("");
    private final StringProperty country = new SimpleStringProperty("");
    private final StringProperty currency = new SimpleStringProperty("");
    private final StringProperty industry = new SimpleStringProperty("");

    private final StringProperty language = new SimpleStringProperty("English");
    private final StringProperty dateFormat = new SimpleStringProperty("DD/MM/YYYY");

    private final ObservableList<UserUI> users = FXCollections.observableArrayList();

    public SettingsViewModel() {
        loadInitialData();
    }

    private void loadInitialData() {
        companyName.set("Mint Management");
        country.set("Slovakia");
        currency.set("EUR (€)");
        industry.set("Financial Services");

        // Dummy data
        users.addAll(
                new UserUI("Marcus", "Thorne", "m.thorne@mintmgmt.com", "Director"),
                new UserUI("Elena", "Varga", "e.varga@mintmgmt.com", "Manager"),
                new UserUI("Jakub", "Kolar", "j.kolar@mintmgmt.com", "Analyst")
        );
    }

    public void saveCompanyProfile() {
        System.out.println("Saving Company: " + companyName.get());
    }

    public void applyPreferences() {
        System.out.println("Applying preferences...");
    }

    public void addUser(UserUI user) {
        users.add(user);
    }

    public void deleteUser(UserUI user) {
        users.remove(user);
    }

    public void changePassword(String currentPw, String newPw, String confirmPw) {
        System.out.println("Password logic here...");
    }

    public StringProperty companyNameProperty() { return companyName; }
    public StringProperty countryProperty() { return country; }
    public StringProperty currencyProperty() { return currency; }
    public StringProperty industryProperty() { return industry; }
    public StringProperty languageProperty() { return language; }
    public StringProperty dateFormatProperty() { return dateFormat; }

    public ObservableList<UserUI> getUsers() { return users; }
}