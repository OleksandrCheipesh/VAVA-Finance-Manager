package org.example;


import org.example.model.database.entity.User;
import java.util.Objects;


public final class SessionManager {

    private static SessionManager instance;
    private User currentUser;


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
        if (user == null) {
            throw new IllegalArgumentException("Cannot login with null user");
        }
        this.currentUser = user;
    }

    /**
     * Terminate the current session.
     */
    public synchronized void logout() {
        this.currentUser = null;
    }

    /**
     * Get the currently logged-in user.
     *
     * @return The current user
     * @throws IllegalStateException if no user is logged in
     */
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

    public synchronized SessionStatus getStatus() {
        return new SessionStatus(
                currentUser != null ? currentUser.getId() : -1,
                currentUser != null ? currentUser.getEmail() : null,
                currentUser != null ? currentUser.getCompanyId() : null
        );
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

    public static record SessionStatus(
            int userId,
            String email,
            Integer companyId
    ) {}
}