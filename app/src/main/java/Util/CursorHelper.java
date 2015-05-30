package Util;

import android.database.Cursor;
import android.util.Log;

/**
 * Helper class that prints the contents of a given cursor to the screen
 */
public class CursorHelper {
    public static final String TAG = CursorHelper.class.getSimpleName();

    public static void toString(Cursor c) {
        int entries = c.getCount();
        int columns = c.getColumnCount();

        c.moveToFirst();

        Log.d(TAG, "Printing Value of Cursor... \n");

        for (int i = 0; i < entries; i++) {
            for (int j = 0; j < columns; j++) {
                Log.d(TAG, "Entry: " + String.valueOf(i) + ". Column: " + c.getColumnName(j) + ", value: " + c.getString(j) + "\n");
            }
            c.moveToNext();
        }
    }
}
