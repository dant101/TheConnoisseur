package Util;

public class Time {

    public static java.sql.Timestamp getCurrentTimestamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());
    }
}
