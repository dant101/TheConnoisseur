package Database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Alexandre on 20/05/2015.
 */
public interface Database {

    void connect();
    void disconnect();
}
