package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
          /*  connection = DriverManager.getConnection(
                    "jdbc:postgresql://db.doc.ic.ac.uk/g1427115_u?ssl=false", "g1427115_u",
                    "IOiuiPSi66");*/
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

    @Override
    public void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
