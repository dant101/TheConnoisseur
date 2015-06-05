package com.theconnoisseur.android.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.theconnoisseur.android.Activities.Interfaces.ExerciseContentDownloadCallback;

import Util.ContentSample;
import Util.ResourceDownloader;

/**
 * Queries and Downloads all the latest content from the online database / server
 */
public class ContentDownloadController {
    private static final String TAG = ContentDownloadController.class.getSimpleName();

    private static final String PREF_KEY_LAST_CHECK = "last_check";
    private static final String PREF_KEY_LANGUAGE_DOWNLOADED = "language_downloaded_";
    private static final int DOWNLOAD_QUANTUM = 86400000; // Millis in a day

    private static ContentDownloadController sInstance = null;

    private Context mContext;
    private int mLanguage_id = 0;

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
    public void getLanguages(Context context) {
        this.mContext = context;

        if (shouldFetchContent()) {
            Log.d(TAG, "ContentDownloadController: fetching language content...");
            new LanguagesContentDownloadTask().execute(context);

            //Temp testing
            //ContentSample.insertSampleItalian(context);

        } else {
            Log.d(TAG, "ContentDownloadController: should NOT fetch language content");
        }
    }

    /**
     * Calls the exercise download task on the specified language id if required.
     * @param context required for storing data
     * @param language_id id of language to download words for
     * @return whether download task was executed
     */
    public boolean getExercises(Context context, int language_id) {
        this.mContext = context;
        this.mLanguage_id = language_id;

        if (shouldFetchExercise(language_id)) {
            new ExerciseContentDownloadTask().execute(context);
            return true;
        } else {
            Log.d(TAG, "ContentDownloadController: should NOT fetch exercise context (id: " + String.valueOf(language_id) + ")");
            return false;
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
     * Returns whether exercises for a given language should be downloaded
     * TODO: what if we change the words on the database? decision to be made here.
     * @param language_id The id of the language whose words we are deciding whether to download
     * @return
     */
    private boolean shouldFetchExercise(int language_id) {
        return !PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(PREF_KEY_LANGUAGE_DOWNLOADED + String.valueOf(language_id), false);
    }

    /**
     * Downloads the latest languages, updates shared preferences appropriately (off calling thread)
     */
    private class LanguagesContentDownloadTask extends AsyncTask<Context, Void, Void> {

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
            Log.d(TAG, "onPostExecute of LanguagesContentDownloadTask called");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(PREF_KEY_LAST_CHECK, System.currentTimeMillis());
            editor.commit();

            Log.d(TAG, "Preference time set: " + String.valueOf(prefs.getLong(PREF_KEY_LAST_CHECK, 0)));
        }
    }

    /**
     * Downloads all the words with specified language_id (variable in parent class) off calling thread
     */
    private class ExerciseContentDownloadTask extends AsyncTask<Context, Void, Integer> {

        Context mContext = null;

        @Override
        protected Integer doInBackground(Context... contexts) {
            if(contexts[0] != null) {
                mContext = contexts[0];
                ResourceDownloader.downloadExercises(mContext, mLanguage_id);
            }
            return mLanguage_id;
        }

        @Override
        protected void onPostExecute(Integer language_id) {
            Log.d(TAG, "onPostExecute of ExerciseContentDownloadTask called");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_KEY_LANGUAGE_DOWNLOADED + String.valueOf(language_id), true);
            editor.commit();

            if (mContext instanceof ExerciseContentDownloadCallback) {
                Log.d(TAG, "ExerciseContentDownloadCallback called");
                ((ExerciseContentDownloadCallback) mContext).ExerciseDownloaded();
            }

            Log.d(TAG, "Preference boolean for language_id " + String.valueOf(language_id) + " set to true");
        }
    }

}
