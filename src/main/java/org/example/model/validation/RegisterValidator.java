package org.example.model.validation;

public class RegisterValidator {

    public static void validate(String name, String surname, String email, String password) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is required");

        if (surname == null || surname.isBlank())
            throw new IllegalArgumentException("Surname is required");

        if (!isValidEmail(email))
            throw new IllegalArgumentException("Invalid email format");

        if (!isValidPassword(password))
            throw new IllegalArgumentException("Password must be at least 8 characters, have upper and lowercase letters and a special symbol");
    }

    public static void validatePassword(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("New password is required.");
        if (password.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        if (!password.matches(".*[A-Z].*"))
            throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        if (!password.matches(".*[a-z].*"))
            throw new IllegalArgumentException("Password must contain at least one lowercase letter.");
        if (!password.matches(".*[^A-Za-z0-9].*"))
            throw new IllegalArgumentException("Password must contain at least one special character.");
    }

    private static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        return password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[^A-Za-z0-9].*");
    }

    private static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex != email.lastIndexOf('@')) return false;
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);
        return !local.isEmpty() && domain.contains(".") && !domain.startsWith(".")
                && !domain.endsWith(".") && !email.contains("..");
    }
}
// схуя ви ублюдки приватизували мою розроботку ?