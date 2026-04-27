package org.example;

public final class UserPreferences {

    private static UserPreferences instance;
    private String language;
    private String currency;
    private String currencySymbol;

    private UserPreferences() {}

    public static UserPreferences getInstance() {
        if (instance == null) {
            synchronized (UserPreferences.class) {
                if (instance == null) {
                    instance = new UserPreferences();
                }
            }
        }
        return instance;
    }

    public void init(String language, String currency) {
        this.language = language;
        this.currency = currency;
        this.currencySymbol = resolveCurrencySymbol(currency);
    }

    public void clear() {
        this.language = null;
        this.currency = null;
        this.currencySymbol = null;
    }

    private String resolveCurrencySymbol(String currency) {
        if (currency == null) return "€";
        return switch (currency) {
            case "USD" -> "$";
            case "CZK" -> "Kč";
            case "GBP" -> "£";
            default -> "€";
        };
    }

    public String getLanguage() { return language; }
    public String getCurrency() { return currency; }
    public String getCurrencySymbol() { return currencySymbol; }
}