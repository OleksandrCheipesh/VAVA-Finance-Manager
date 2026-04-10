package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import java.io.File;
import java.sql.SQLException;

import org.example.model.models.CreateCompanyModel;

/**
 *  Create Company window - ViewModel
 */
public class CreateCompanyViewModel {

    // Data binding properties
    private final StringProperty companyName = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty industry = new SimpleStringProperty("");
    private final StringProperty city = new SimpleStringProperty("");
    private final StringProperty address = new SimpleStringProperty("");
    private final StringProperty website = new SimpleStringProperty("");
    private final StringProperty taxId = new SimpleStringProperty("");

    // Non-string types, Dropdown selections object properties
    private final ObjectProperty<String> country = new SimpleObjectProperty<>();
    private final ObjectProperty<String> currency = new SimpleObjectProperty<>();
    private final ObjectProperty<File> logoFile = new SimpleObjectProperty<>(null);

    // Dropdown options
    private final ObservableList<String> countries = FXCollections.observableArrayList(
            "Czech Republic", "Germany", "Slovakia", "United Kingdom", "United States"
    );
    private final ObservableList<String> currencies = FXCollections.observableArrayList(
            "CZK", "EUR", "GBP", "USD"
    );

   // Listeners for reactive UI
    public CreateCompanyViewModel() {
        // Currency suggestion based on selected country

        country.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                autoSelectCurrency(newValue);
            }
        });
    }

    // Map currency based on it's country
    private void autoSelectCurrency(String selectedCountry) {

        switch (selectedCountry) {
            case "Slovakia":
            case "Germany":
                currency.set("EUR");
                break;
            case "Czech Republic":
                currency.set("CZK");
                break;
            case "United States":
                currency.set("USD");
                break;
            case "United Kingdom":
                currency.set("GBP");
                break;
            default:
                currency.set("EUR");
        }
    }

    // Registration and validation
    public void registerCompany() throws SQLException {

        if (companyName.get().isEmpty() || country.get() == null || currency.get() == null) {
            System.out.println("Validation failed: Please fill all required fields.");

            return;
        }

        CreateCompanyModel createCompanyModel = new CreateCompanyModel(this.companyName.get(),
                this.country.get(),
                this.currency.get());
        createCompanyModel.createCompany();
        if (logoFile.get() != null) {
            System.out.println("Selected logo path: " + logoFile.get().getAbsolutePath());
        }


    }


    // Getters
    public StringProperty companyNameProperty() { return companyName; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty industryProperty() { return industry; }
    public ObjectProperty<String> countryProperty() { return country; }
    public ObjectProperty<String> currencyProperty() { return currency; }
    public StringProperty cityProperty() { return city; }
    public StringProperty addressProperty() { return address; }
    public StringProperty websiteProperty() { return website; }
    public StringProperty taxIdProperty() { return taxId; }
    public ObjectProperty<File> logoFileProperty() { return logoFile; }

    // Sorted ComboBox list of countries
    public ObservableList<String> getCountries() { return countries; }

    // Sorted ComboBox list of currencies
    public ObservableList<String> getCurrencies() { return currencies; }
}