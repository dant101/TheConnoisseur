package Util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Alexandre on 28/05/2015.
 */
public class PasswordEncryption {
    private static final Random RANDOM = new SecureRandom();

    /*Returns a random salt number*/
    public static int getNextSalt() {
        return RANDOM.nextInt();
    }

    /* Computes the hashing of a password and its salt and returns it
     * We are using SHA-256 hashing
     */
    public static String hash(String password, int salt) {
        String text = salt + password;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] tmp = digest.digest(text.getBytes("UTF-8"));
            String hash = new String(tmp);
            return hash;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Checks that given pasword and salt match the hashed value
     */
    public static boolean isExpectedPassword(String password, int salt, String expectedHash) {
        String passwordHash = hash(password, salt);
        if(passwordHash != null) {
            if (passwordHash.equals(expectedHash)) {
                return true;
            }
        }
        return false;
    }

}
