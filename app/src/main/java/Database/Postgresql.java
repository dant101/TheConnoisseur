package Database;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Alexandre on 22/05/2015.
 */
public class Postgresql implements Database {

    private Connection connection;
    private String host;
    private String username;
    private String password;

    public Postgresql(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /*Connect to the Database*/
    @Override
    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found, include in your library path!");
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + host + "?&ssl=true" + "&sslfactory=org.postgresql.ssl.NonValidatingFactory",
                    username,
                    password);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("Connection was successful!");
        } else {
            System.out.println("Failed to make connection!");
        }
    }

    /*Disconnect from the database*/
    @Override
    public void disconnect() {
        try {
            connection.close();
            System.out.println("Disconnected from Database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isServerReachable() {
        //maximum time in ms to fetch data
        int time = 2;
        boolean result = false;
        try {
            result = connection.isValid(time);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return result;
        }
    }

    /*Execute an SQL selectQuery
    * Get the result back as a list of rows where each row is a list of objects (int, string, date...)
    * sqlQuery is an SQL selectQuery example: "SELECT name, surname FROM test"
    * arguments is what we want back example: "name, surname, city"*/
    public List<List<String>> selectQuery(String sqlQuery, List<String> arguments) {
        this.connect();
        List<List<String>> result = new ArrayList<>();

        try {
            if(isServerReachable()) {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sqlQuery);
                //Loop over all the rows
                while (rs.next()) {
                    //For each row get the different values needed
                    List<String> row = new ArrayList<>();
                    for (String arg : arguments) {
                        row.add(rs.getString(arg));
                    }
                    result.add(row);
                }
                rs.close();
                stmt.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            this.disconnect();
            return result;
        }
    }

    /*Does an insert into the database
    Format example:
    - "INSERT INTO exercise VALUES (100, 'Zara', 'Ali', 18)"
    - "UPDATE database SET username = alex WHERE id = 123";
     */
    public void insertQuery(String sqlQuery) {
        this.connect();
        try {
            if(isServerReachable()) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(sqlQuery);
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
    }


    /*Does an insert into the database without connecting first
    Format example:
    - "INSERT INTO exercise VALUES (100, 'Zara', 'Ali', 18)"
    - "UPDATE database SET username = alex WHERE id = 123";
     */
    public void insertQueryNoConnection(String sqlQuery) {
        try {
            if(isServerReachable()) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(sqlQuery);
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*We need a special query to prevent user from doing SQLInjection*/
    public List<List<String>> loginAndFriendQuery(String sqlQuery, List<String> arguments, String value) {
        this.connect();
        List<List<String>> result = new ArrayList<>();

        try {
            if(isServerReachable()) {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, value);
                ResultSet rs = stmt.executeQuery();
                //Loop over all the rows
                while (rs.next()) {
                    //For each row get the different values needed
                    List<String> row = new ArrayList<>();
                    for (String arg : arguments) {
                        row.add(rs.getString(arg));
                    }
                    result.add(row);
                }
                rs.close();
                stmt.close();

            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            this.disconnect();
            return result;
        }
    }

    /*We need a special createLoginQuery to prevent user from doing SQLInjection*/
    public boolean createLoginQuery(String sqlQuery,
                                 String username, String password, int salt) {
        boolean result = false;
        this.connect();
        try {
            if(isServerReachable()) {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setInt(3, salt);
                stmt.executeUpdate();
                stmt.close();
                result = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            this.disconnect();
            return result;
        }
    }

    /*We need a special createCommentQuery to prevent user from doing SQLInjection*/
    public boolean createCommentQuery(String sqlQuery,
                                      int word_id, String username,
                                      String comment, Timestamp time,
                                      int score, String parent_path) {
        boolean result = false;
        this.connect();
        try {
            if(isServerReachable()) {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setInt(1, word_id);
                stmt.setString(2, username);
                stmt.setString(3, comment);
                stmt.setInt(4, score);
                stmt.setString(5, parent_path);
                stmt.setTimestamp(6, time);
                stmt.executeUpdate();
                stmt.close();
                result = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            this.disconnect();
            return result;
        }
    }

    /*We need a special createScoreQuery to prevent user from doing SQLInjection*/
    public boolean createScoreQuery(String sqlQuery,
                                      String username, Integer word_id, Integer percentage_score, Integer attempts_score) {
        boolean result = false;
        this.connect();
        try {
            if(isServerReachable()) {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, username);
                if (word_id != null && percentage_score != null && attempts_score != null) {
                    stmt.setInt(2, word_id);
                    stmt.setInt(3, percentage_score);
                    stmt.setInt(4, attempts_score);
                }
                stmt.executeUpdate();
                stmt.close();
                result = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            this.disconnect();
            return result;
        }
    }

    /*We need a special scoreQuery to prevent user from doing SQLInjection*/
    public List<List<String>> scoreQuery(String sqlQuery, List<String> arguments, String value) {
        this.connect();
        List<List<String>> result = new ArrayList<>();

        try {
            if(isServerReachable()) {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, value);
                ResultSet rs = stmt.executeQuery();
                //Loop over all the rows
                while (rs.next()) {
                    //For each row get the different values needed
                    List<String> row = new ArrayList<>();
                    for (String arg : arguments) {
                        row.add(rs.getString(arg));
                    }
                    result.add(row);
                }
                rs.close();
                stmt.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            this.disconnect();
            return result;
        }
    }

    public boolean createFriendQuery(String sqlQuery,
                                    String username, String friend_username, Boolean confirmed) {
        boolean result = false;
        this.connect();
        try {
            if(isServerReachable()) {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, username);
                stmt.setString(2, friend_username);
                if (confirmed != null) {
                    stmt.setBoolean(3, confirmed);
                }
                stmt.executeUpdate();
                stmt.close();
                result = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            this.disconnect();
            return result;
        }
    }

    /*We need a special scoreQuery to prevent user from doing SQLInjection*/
    public List<List<String>> selectFriendsQuery(String sqlQuery, List<String> arguments,
                                                 String username, String friend_username) {
        this.connect();
        List<List<String>> result = new ArrayList<>();

        try {
            if(isServerReachable()) {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, username);
                if (friend_username != null) {
                    stmt.setString(2, friend_username);
                }
                ResultSet rs = stmt.executeQuery();
                //Loop over all the rows
                while (rs.next()) {
                    //For each row get the different values needed
                    List<String> row = new ArrayList<>();
                    for (String arg : arguments) {
                        row.add(rs.getString(arg));
                    }
                    result.add(row);
                }
                rs.close();
                stmt.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            this.disconnect();
            return result;
        }
    }
}
