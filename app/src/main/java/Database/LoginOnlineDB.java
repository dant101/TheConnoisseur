package Database;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import Util.PasswordEncryption;

public class LoginOnlineDB extends OnlineDB {
    public static final String TAG = LoginOnlineDB.class.getSimpleName();

    public LoginOnlineDB(Postgresql database) {
        super(database);
        allArguments.add("id");
        allArguments.add("username");
        allArguments.add("password");
        allArguments.add("salt");
        allArguments.add("email");
    }

    /* Tries to login returning true if success*/
    public boolean login(String username, String password) {
        boolean result = false;

        try {
            //using a different selectQuery format here to prevent SQLInjections
            String query = "SELECT * " +
                    "FROM login " +
                    "WHERE username = ?";
            List<List<String>> queryResult = database.loginAndFriendQuery(query, this.allArguments, username);
            if(queryResult.size() == 1) {
                LoginOnlineDBFormat formatLogin = format(queryResult,LoginOnlineDBFormat.class).get(0);
                result = PasswordEncryption.isExpectedPassword(password, formatLogin.getSalt(), formatLogin.getPassword());
            }

        } catch (Exception e) {
            Log.d(TAG, "Unable to correctly verify login - assume false");
            result = false;
            e.printStackTrace();
        }

        return result;
    }

    /*Creates a new account
     * Returns true if creation was successful
     */
    public boolean create(String username, String password, String email) {
        boolean result = false;
        String query = "INSERT INTO login(username, password, salt) " +
                "VALUES (?, ?, ?)";

        //check that username is not taken
        if(isUserNameUnique(username)) {
            int salt = PasswordEncryption.getNextSalt();
            String hash = PasswordEncryption.hash(password, salt);

            result = database.createLoginQuery(query, username, hash, salt);
        }

     return result;
    }

    /*
    Returns true if email not present in database
     */
    public boolean isEmailUnique(String email) {
        String queryUsername = "SELECT * " +
                "FROM login " +
                "WHERE email = ?";
        List<List<String>> queryResultEmail = database.loginAndFriendQuery(queryUsername, this.allArguments, email);

        return queryResultEmail.size() == 0;
    }

    /*
    Returns true if username not present is database
     */
    public boolean isUserNameUnique(String username) {
        String queryUsername = "SELECT * " +
                "FROM login " +
                "WHERE username = ?";
        List<List<String>> queryResultUsername = database.loginAndFriendQuery(queryUsername, this.allArguments, username);

        return queryResultUsername.size() == 0;
    }

    public boolean isEmailValid(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        } finally {
            return result;
        }
    }

    /*Returns a list of all users who's username is or starts with the given string*/
    public List<LoginOnlineDBFormat> getUser(String username) {
        //using a different selectQuery format here to prevent SQLInjections
        String query = "SELECT * " +
                    "FROM login " +
                    "WHERE username LIKE ? " +
                    "ORDER BY username";
        username = username + "%";
        List<List<String>> queryResult = database.loginAndFriendQuery(query, this.allArguments, username);
        return format(queryResult,LoginOnlineDBFormat.class);
    }

    /*Returns a list of all users*/
    public List<LoginOnlineDBFormat> getAllUsers() {
        //using a different selectQuery format here to prevent SQLInjections
        String query = "SELECT * " +
                "FROM login";
        List<List<String>> queryResult = database.selectQuery(query, allArguments);
        return format(queryResult,LoginOnlineDBFormat.class);
    }

    @Override
    <LoginOnlineDBFormat> List<LoginOnlineDBFormat> format(List<List<String>> queryResult,
                                                           Class<LoginOnlineDBFormat> cls) {
        List<LoginOnlineDBFormat> result = new ArrayList<>();

        for(List<String> strings : queryResult) {
            try {
                LoginOnlineDBFormat current = cls.getDeclaredConstructor(List.class).newInstance(strings);
                result.add(current);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
