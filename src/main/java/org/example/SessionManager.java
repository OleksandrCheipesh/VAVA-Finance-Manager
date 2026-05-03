package org.example;

import org.example.model.database.entity.Company;
import org.example.model.database.entity.Position;
import org.example.model.database.entity.User;
import org.example.logging.AppLog;
import org.example.model.database.service.CompanyService;

import java.sql.SQLException;

public final class SessionManager {

    private static SessionManager instance;
    private User currentUser;
    private Company currentCompany;
    private PendingRegistration pendingRegistration;
    private String language = "English";
    private String currency = "";
    private String currencySymbol = "$";

    private SessionManager() {
        this.currentUser = null;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public synchronized void login(User user) {
        var logger = AppLog.getLogger(SessionManager.class);
        if (user == null) {
            logger.warn("Cannot login with null user");
            throw new IllegalArgumentException("Cannot login with null user");
        }
        this.currentUser = user;
        this.language = user.getLanguage() != null ? user.getLanguage() : "English";

        Integer companyId = user.getCompanyId();
        if (companyId == null) {
            currentCompany = null;
        } else {
            try {
                currentCompany = new CompanyService().getCompanyById(companyId).orElse(null);
            } catch (SQLException e) {
                currentCompany = null;
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        if (currentCompany != null && currentCompany.getCurrency() != null) {
            this.currency = currentCompany.getCurrency();
            this.currencySymbol = extractSymbol(currentCompany.getCurrency());
        }

        logger.info("User login: id={} email={}", user.getId(), user.getEmail());
        AppLog.pushSession(getStatus());
    }

    public synchronized void login(User user, Company currentCompany) {
        var logger = AppLog.getLogger(SessionManager.class);
        if (user == null) {
            logger.warn("Cannot login with null user");
            throw new IllegalArgumentException("Cannot login with null user");
        }
        this.currentUser = user;
        this.currentCompany = currentCompany;
        this.language = user.getLanguage() != null ? user.getLanguage() : "English";

        if (currentCompany != null && currentCompany.getCurrency() != null) {
            this.currency = currentCompany.getCurrency();
            this.currencySymbol = extractSymbol(currentCompany.getCurrency());
        }

        logger.info("User login: id={} email={}", user.getId(), user.getEmail());
        AppLog.pushSession(getStatus());
    }

    private String extractSymbol(String currencyString) {
        int open = currencyString.indexOf('(');
        int close = currencyString.indexOf(')');
        if (open >= 0 && close > open) {
            return currencyString.substring(open + 1, close);
        }
        // fallback for old records without symbol
        return switch (currencyString.trim().toUpperCase()) {
            case "EUR" -> "€";
            case "USD" -> "$";
            case "GBP" -> "£";
            case "CZK" -> "Kč";
            default -> currencyString;
        };
    }

    public synchronized void logout() {
        var logger = AppLog.getLogger(SessionManager.class);
        if (this.currentUser != null) {
            logger.info("User logout: id={} email={}", this.currentUser.getId(), this.currentUser.getEmail());
        }
        this.currentUser = null;
        this.currentCompany = null;
        this.pendingRegistration = null;
        this.language = "English";
        this.currency = "";
        this.currencySymbol = "$";
        AppLog.clearSession();
    }

    public synchronized User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user in session");
        }
        return currentUser;
    }

    public synchronized int getCurrentCompanyId() {
        User user = getCurrentUser();
        Integer companyId = user.getCompanyId();
        if (companyId == null) {
            throw new IllegalStateException("Current user is not associated with any company");
        }
        return companyId;
    }

    public synchronized void setCurrentCompany(Company currentCompany) {
        this.currentCompany = currentCompany;
    }

    public synchronized void setPendingRegistration(String name, String surname, String email, String passwordHash) {
        this.pendingRegistration = new PendingRegistration(name, surname, email, passwordHash);
    }

    public synchronized PendingRegistration requirePendingRegistration() {
        if (pendingRegistration == null) {
            throw new IllegalStateException("No pending registration data available");
        }
        return pendingRegistration;
    }

    public synchronized void clearPendingRegistration() {
        this.pendingRegistration = null;
    }

    public synchronized SessionStatus getStatus() {
        return new SessionStatus(
                currentUser != null ? currentUser.getId() : -1,
                currentUser != null ? currentUser.getEmail() : null,
                currentUser != null ? currentUser.getCompanyId() : null
        );
    }

    public synchronized Position getPosition() {
        return getCurrentUser().getPosition();
    }

    public synchronized void verifyCompanyAccess(int companyId) {
        int currentCompanyId = getCurrentCompanyId();
        if (currentCompanyId != companyId) {
            throw new SecurityException(String.format(
                    "User does not have access to company %d. User belongs to company %d",
                    companyId, currentCompanyId
            ));
        }
    }

    public record SessionStatus(int userId, String email, Integer companyId) {}

    public record PendingRegistration(String name, String surname, String email, String passwordHash) {}

    public Company getCurrentCompany() {
        if (currentCompany == null) {
            throw new IllegalStateException("No authenticated company in session");
        }
        return currentCompany;
    }

    public synchronized String getLanguage() { return language; }
    public synchronized void setLanguage(String language) { this.language = language; }
    public synchronized String getCurrency() { return currency; }
    public synchronized void setCurrency(String currency) { this.currency = currency; }
    public synchronized String getCurrencySymbol() { return currencySymbol; }
    public synchronized void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }
}