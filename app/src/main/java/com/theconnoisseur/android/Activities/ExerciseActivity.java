package com.theconnoisseur.android.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
import com.theconnoisseur.android.Activities.Interfaces.ExerciseContentDownloadCallback;
import com.theconnoisseur.android.Controller.ContentDownloadController;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import Util.CursorHelper;

public class ExerciseActivity extends FragmentActivity implements ExerciseFragment.OnFragmentInteractionListener, CursorCallback, ExerciseContentDownloadCallback {

    private static final String TAG = ExerciseContent.class.getSimpleName();
    private static final String TAG_EXERCISE_FRAGMENT = "exercise_fragment";

    private int LANGUAGE_ID = 3; //ONLY FOR TESTING

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, ExerciseFragment.newInstance(), TAG_EXERCISE_FRAGMENT).commit();
        }

        Intent intent = getIntent();
        LANGUAGE_ID = intent.getIntExtra(ExerciseContent.LANGUAGE_ID, LANGUAGE_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Calls download task and leaves callback to setup cursor.
        // Otherwise, initiate cursor immediately (if data already downloaded)
        if (!ContentDownloadController.getInstance().getExercises(this, LANGUAGE_ID)) {
            // Loads the exercise cursor for specific set of exercises
            new ExerciseCursorPreparationTask(this).execute(LANGUAGE_ID);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goBack(View v) {
        startActivity(new Intent(ExerciseActivity.this, LanguageSelectionActivity.class));
    }

    public void nextExercise() {
        //TODO: work on parent activity given that fragment requests new exercise?

        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_EXERCISE_FRAGMENT);

        if (fragment instanceof ExerciseFragment) {
            ((ExerciseFragment)fragment).nextExercise(mCursor);
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
                mCursor = getContentResolver().query(InternalDbContract.queryForWords(languages_id), null, null, null, null);
            } else {
                Log.d(TAG, "No language_id identified for query!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "ExerciseCursorPreparationTask onPostExecute called");
            super.onPostExecute(result);

            mCallback.CursorLoaded(mCursor);
        }
    }

}
