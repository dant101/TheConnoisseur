package Util;

import java.sql.Timestamp;

public class Time {

    public static java.sql.Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
