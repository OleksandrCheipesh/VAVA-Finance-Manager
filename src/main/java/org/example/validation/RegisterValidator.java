package org.example.validation;

public class RegisterValidator {


    public static void validate(String login, String email, String password) {

        if (login == null || login.isBlank())
            throw new IllegalArgumentException("Login is required");

        if (!checkThePass(password))
            throw new IllegalArgumentException("Password must: be at least 8 characters," +
                    " have upper and lowercase letter, or " +
                    "have a specific symbol");

        if (!checkTheEmail(email))
            throw new IllegalArgumentException("Invalid email");
    }

    // password checker
    private static boolean checkThePass(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Check for uppercase letter
        boolean hasUppercase = password.matches(".*[A-Z].*");

        // Check for  lowercase letter
        boolean hasLowercase = password.matches(".*[a-z].*");

        // Check for special character
        boolean hasSpecialChar = password.matches(".*[^A-Za-z0-9].*");

        return hasUppercase && hasLowercase && hasSpecialChar;
    }

    // checker if the email has @ and .
    private static boolean checkTheEmail(String email) {

        boolean isEmailOk = true;

        if (email == null || email.isEmpty()) {
            isEmailOk = false;
        }
        int atIndex = email.indexOf('@');
        if (atIndex == -1) return false;

        int dotIndex = email.indexOf('.');

        boolean atRightPosition = true;
        boolean dotRightPosition = true;
        // @ is not at the begining or duplicating
        if (atIndex <= 0 || atIndex != email.lastIndexOf('@')) {
            atRightPosition = false;
        }
        // no dot after @, or dot at end
        if (dotIndex <= atIndex + 1 || dotIndex >= email.length() - 1) {
            dotRightPosition = false;
        }

        // check for consecutive dots
        if (email.contains("..")) {
            dotRightPosition = false;
        }

        // check if local part and domain are not empty
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        if (localPart.isEmpty() || domain.isEmpty()) {
            isEmailOk = false;
        }

        return atRightPosition && dotRightPosition && isEmailOk;
    }
}
