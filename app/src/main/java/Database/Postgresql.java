package Database;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Util.JSONHelper;

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
                    "jdbc:postgresql://"+host+"?ssl=false", username,
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
    * Get the result back in a ResultSet
    * Convert it to JSON afterwards*/
    public JSONObject query(String query) {
        JSONObject result = null;

        try {
            Statement stmt = connection.createStatement();
            String sql;
            // For testing purposes
            sql = "SELECT name, surname FROM test";

            ResultSet rs = stmt.executeQuery(sql);
            //result = JSONHelper.getInstance().getJSON("What goes in here?");
            while (rs.next()) {
                //for testing purposes
                String first = rs.getString("name");
                String last = rs.getString("surname");

            }
            rs.close();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            return result;
        }
    }


}
