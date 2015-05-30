package com.theconnoisseur.android.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import Util.ResourceDownloader;

/**
 * Queries and Downloads all the latest content from the online database / server
 */
public class ContentDownloadController {
    private static final String TAG = ContentDownloadController.class.getSimpleName();

    private static final String PREF_KEY_LAST_CHECK = "last_check";
    private static final int DOWNLOAD_QUANTUM = 86400000; // Millis in a day

    private static ContentDownloadController sInstance = null;

    private Context mContext;

    public static synchronized ContentDownloadController getInstance() {
        if (sInstance == null) {
            sInstance = new ContentDownloadController();
        }
        return sInstance;
    }

    /**
     * Calls the content download task if required
     * @param context
     */
    public void execute(Context context) {
        this.mContext = context;

        if (shouldFetchContent()) {
            new ContentDownloadTask().execute(context);
        } else {
            Log.d(TAG, "ContentDownloadController: should NOT fetch Content");
        }
    }

    /**
     * Checks whether time since last content download exceeds time quantum set in this class
     * @return whether we should fetch content
     */
    private boolean shouldFetchContent() {
        long lastDownload = PreferenceManager.getDefaultSharedPreferences(mContext).getLong(PREF_KEY_LAST_CHECK, 0);
        return (System.currentTimeMillis() - lastDownload) > (DOWNLOAD_QUANTUM);
    }

    /**
     * Downloads the latest languages, updates shared preferences appropriately (off calling thread)
     */
    private class ContentDownloadTask extends AsyncTask<Context, Void, Void> {

        Context mContext = null;

        @Override
        protected Void doInBackground(Context... contexts) {
            if(contexts[0] != null) {
                mContext = contexts[0];
                ResourceDownloader.downloadLanguages(mContext);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.d(TAG, "onPostExecute of ContentDownloadTask called");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(PREF_KEY_LAST_CHECK, System.currentTimeMillis());
            editor.commit();

            Log.d(TAG, "Preference time set: " + String.valueOf(prefs.getLong(PREF_KEY_LAST_CHECK, 0)));
        }
    }

}
