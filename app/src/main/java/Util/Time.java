package Util;

/**
 * Created by Alexandre on 03/06/2015.
 */
public class Time {

    public static java.sql.Timestamp getCurrentTimestamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());
    }
}
