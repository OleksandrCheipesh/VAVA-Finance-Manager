package org.example.model.validation;

public class RegisterValidator {

    public static void validate(String login, String email, String password) {
        if (login == null || login.isBlank())
            throw new IllegalArgumentException("Login is required");

        if (!isValidPassword(password))
            throw new IllegalArgumentException("Password must be at least 8 characters, " +
                    "have upper and lowercase letters and a special symbol");

        if (!isValidEmail(email))
            throw new IllegalArgumentException("Invalid email");
    }

    // password checker
    private static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;

        // Check for uppercase, lowercase and special character
        return password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[^A-Za-z0-9].*");
    }

    // checker if the email has @ and .
    private static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;

        // @ is not at the beginning or duplicating
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex != email.lastIndexOf('@')) return false;

        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        // check if local part and domain are not empty, no dot at start/end, no consecutive dots
        return !local.isEmpty()
                && domain.contains(".")
                && !domain.startsWith(".")
                && !domain.endsWith(".")
                && !email.contains("..");
    }
}