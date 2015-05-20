package Database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Alexandre on 20/05/2015.
 */
public class Database {
    public Database() {

    }

    public void connect() {
        System.out.println("-------- PostgreSQL "
                + "JDBC Connection Testing ------------");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://db.doc.ic.ac.uk/g1427115_u?ssl=false", "g1427115_u",
                    "IOiuiPSi66");
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

        try {
            connection.close();
        } catch (Exception e) {

        }
    }

}
