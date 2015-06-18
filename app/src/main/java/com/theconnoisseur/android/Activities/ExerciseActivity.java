package com.theconnoisseur.android.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
import com.theconnoisseur.android.Activities.Interfaces.ExerciseContentDownloadCallback;
import com.theconnoisseur.android.Controller.ContentDownloadController;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;
import com.theconnoisseur.android.Model.SessionSummaryContent;

import Util.CursorHelper;

public class ExerciseActivity extends FragmentActivity implements ExerciseFragment.OnFragmentInteractionListener, CursorCallback, ExerciseContentDownloadCallback {

    private static final String TAG = ExerciseContent.class.getSimpleName();
    private static final String TAG_EXERCISE_FRAGMENT = "exercise_fragment";
    private static final String RESUMING = "resuming";

    private int LANGUAGE_ID = 3; //ONLY FOR TESTING
    public static final int EXERCISES_PER_SESSION = 5;

    private Cursor mCursor;
    private boolean mResuming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise);

        if (savedInstanceState == null) {
            Log.d(TAG, "ExerciseActivity: savedInstanceState is null");
            getFragmentManager().beginTransaction()
                    .add(R.id.container, ExerciseFragment.newInstance(), TAG_EXERCISE_FRAGMENT).commit();
        } else {
            mResuming = savedInstanceState.getBoolean(RESUMING);
        }

        Intent intent = getIntent();
        LANGUAGE_ID = intent.getIntExtra(ExerciseContent.LANGUAGE_ID, LANGUAGE_ID);

        //Calls download task and leaves callback to setup cursor.
        // Otherwise, initiate cursor immediately (if data already downloaded)
        if (!ContentDownloadController.getInstance().getExercises(this, LANGUAGE_ID)) {
            // Loads the exercise cursor for specific set of exercises
            new ExerciseCursorPreparationTask(this).execute(LANGUAGE_ID);
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "ExerciseActivity onStart()");
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "ExerciseActivity: onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(RESUMING, true);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "ExerciseActivity onStop()");
        mResuming = true;
        super.onStop();
    }

    public void goBack(View v) {
        startActivity(new Intent(ExerciseActivity.this, LanguageSelectionActivity.class));
    }

    public void viewComments(View v) {
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_EXERCISE_FRAGMENT);
        if (fragment instanceof ExerciseFragment) {
            ((ExerciseFragment)fragment).viewComments();
        }
    }

    public void playRecording(View v) {
    Fragment fragment = getFragmentManager().findFragmentByTag(TAG_EXERCISE_FRAGMENT);
        if (fragment instanceof ExerciseFragment) {
            ((ExerciseFragment)fragment).playRecording();
        }
    }

    public void nextExercise() {
        //TODO: work on parent activity given that fragment requests new exercise?
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_EXERCISE_FRAGMENT);
        if (fragment instanceof ExerciseFragment) {
            ((ExerciseFragment)fragment).nextExercise(mCursor, mResuming);
            Log.d(TAG, "nextExercise - resuming? " + String.valueOf(mResuming));
            mResuming = false;
        }
    }

    /**
     * Actions taken once content cursor is ready
     * @param c returned cursor from db query
     */
    @Override
    public void CursorLoaded(Cursor c) {
        Log.d(TAG, "CursorLoaded callback");
        //CursorHelper.toString(c); //For testing

        if(c.getCount() == 0) { return; }

        c.moveToFirst();
        this.mCursor = c;

        int id = c.getInt(c.getColumnIndex(ExerciseContent.LANGUAGE_ID));
        prepareLanguageSpecifics(id);
        nextExercise();

    }

    // Loads the correct language image and colours for a specific set of exercises
    private void prepareLanguageSpecifics(int language_id) {
        Cursor c = getContentResolver().query(InternalDbContract.queryForLanguages(language_id), null, null, null, null);

        //Precaution
        if(c == null || c.getCount() == 0) { return; }

        //Prepare for querying
        c.moveToFirst();

        String hex = c.getString(c.getColumnIndex(LanguageSelection.LANGUAGE_HEX));
        String path = c.getString(c.getColumnIndex(LanguageSelection.LANGUAGE_IMAGE_URL));

        //Find fragment and apply colour and image
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_EXERCISE_FRAGMENT);
        if (fragment instanceof ExerciseFragment) {
            ((ExerciseFragment)fragment).setLanguageSpecifics("#" + hex, path);
        }
    }

    /**
     * Initiates loading of content cursor once exercises have been downloaded
     */
    @Override
    public void ExerciseDownloaded() {
        new ExerciseCursorPreparationTask(this).execute(LANGUAGE_ID);
    }


    /**
     * Loads a cursor of exercises using given language_id. Callbacks on completion
     */
    private class ExerciseCursorPreparationTask extends AsyncTask<Integer, Void, Void> {

        CursorCallback mCallback;
        Cursor mCursor;

        public ExerciseCursorPreparationTask(CursorCallback callback) {
            this.mCallback = callback;
        }

        @Override
        protected Void doInBackground(Integer... ids) {
            if(ids[0] != null) {
                int languages_id = ids[0];
                mCursor = getContentResolver().query(InternalDbContract.queryForWords(languages_id), null, null, null, "RANDOM() LIMIT " + String.valueOf(EXERCISES_PER_SESSION));
            } else {
                Log.d(TAG, "No language_id identified for query!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "ExerciseCursorPreparationTask onPostExecute called");
            super.onPostExecute(result);

            //CursorHelper.toString(mCursor);

            mCallback.CursorLoaded(mCursor);
        }
    }

    public void demoSummary(View v) {
        Intent intent = new Intent(this, SessionSummary.class);

        intent.putExtra(SessionSummaryContent.BEST_SCORE, 0);
        intent.putExtra(SessionSummaryContent.BEST_WORD, "Veranda");
        intent.putExtra(SessionSummaryContent.WORST_SCORE, 2);
        intent.putExtra(SessionSummaryContent.WORST_WORD, "Maestro");
        intent.putExtra(SessionSummaryContent.LANGUAGE_FLAG_URI, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/flags/3-italian.png");
        intent.putExtra(SessionSummaryContent.WORDS_PASSED, 5);
        intent.putExtra(SessionSummaryContent.TOTAL_WORDS, 5);
        intent.putExtra(SessionSummaryContent.LANGUAGE, "Italian");
        intent.putExtra(SessionSummaryContent.LANGUAGE_ID, 3);
        intent.putExtra(SessionSummaryContent.LANGUAGE_HEX, "#009246");
        intent.putExtra(SessionSummaryContent.SESSION_NUMBER, 23);
        intent.putExtra(SessionSummaryContent.SESSION_ATTEMPTS, 1);

        startActivity(intent);
    }

}
