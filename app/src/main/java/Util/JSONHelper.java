package Util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

    private static final String TAG = JSONHelper.class.getSimpleName();

    private static JSONHelper mHelper;

    public static synchronized JSONHelper getInstance() {
        return mHelper == null ? new JSONHelper() : mHelper;
    }

    public JSONObject getJSON(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            Log.d(TAG, "Unable to create JSON");
            //TODO: handle exception
            return new JSONObject();
        }

    }
}
