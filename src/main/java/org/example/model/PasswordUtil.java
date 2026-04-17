package org.example.model;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hash(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    public static boolean verify(String plainText, String hashed) {
        if (plainText == null || hashed == null) return false;
        try {
            return BCrypt.checkpw(plainText, hashed);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

