package Database;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                    "jdbc:postgresql://"+host+"?&ssl=true" +
                    "&sslfactory=org.postgresql.ssl.NonValidatingFactory",
                    username,
                    password);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
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

    /*Execute an SQL query
    * Get the result back as a list of rows where each row is a list of objects (int, string, date...)
    * sqlQuery is an SQL query example: "SELECT name, surname FROM test"
    * arguments is what we want back example: "name, surname, city"*/
    public List<List<String>> query(String sqlQuery, List<String> arguments) {
        List<List<String>> result = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            //Loop over all the rows
            while (rs.next()) {
                //For each row get the different values needed
                List<String> row = new ArrayList<>();
                for(String arg : arguments) {
                    row.add(rs.getString(arg));
                }
                result.add(row);
            }
            rs.close();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    /*We need a special loginQuery to prevent user from doing SQLInjection*/
    public List<List<String>> loginQuery(String sqlQuery, List<String> arguments, String value) {
        List<List<String>> result = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setString(1,value);
            ResultSet rs = stmt.executeQuery();
            //Loop over all the rows
            while (rs.next()) {
                //For each row get the different values needed
                List<String> row = new ArrayList<>();
                for(String arg : arguments) {
                    row.add(rs.getString(arg));
                }
                result.add(row);
            }
            rs.close();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    /*We need a special createLoginQuery to prevent user from doing SQLInjection*/
    public boolean createLoginQuery(String sqlQuery,
                                 String username, String password,
                                 String email, int salt) {

        try {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setInt(4, salt);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
