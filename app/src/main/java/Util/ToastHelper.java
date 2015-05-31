package Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Present a toast to the user
 *
 * e.g. ToastHelper.toast(this, "myMessage", Toast.LENGTH_SHORT);
 */
public class ToastHelper {

    public static void toast(Context context, String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
