package com.theconnoisseur.android.Activities;

import android.app.Fragment;
import android.content.ContentValues;
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
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;

public class ExerciseActivity extends FragmentActivity implements ExerciseFragment.OnFragmentInteractionListener, CursorCallback {

    private static final String TAG = ExerciseContent.class.getSimpleName();
    private static final String TAG_EXERCISE_FRAGMENT = "exercise_fragment";

    final int italian_id = 3; //ONLY FOR TESTING

    private Cursor mCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, ExerciseFragment.newInstance(), TAG_EXERCISE_FRAGMENT).commit();
        }
    }

    @Override
    protected void onStart() {
        // Inserts dummy data for test purposes
        insertExercisesForTest();

        // Loads the exercise cursor for specific set of exercises
        new ExerciseCursorPreparationTask(this).execute(italian_id);
        
        super.onStart();
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
        startActivity(new Intent(ExerciseActivity.this, LoginActivity.class));
    }

    public void nextExercise(boolean firstExercise) {
        //TODO: work on parent activity given that fragment requests new exercise?
        if (!firstExercise && mCursor != null) {
            Log.d(TAG, "mCursor moves to next");
        }

        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_EXERCISE_FRAGMENT);

        if (fragment instanceof ExerciseFragment) {
            ((ExerciseFragment)fragment).nextExercise(mCursor);
        }

    }
    
    private void insertExercisesForTest() {

        ContentValues values1 = new ContentValues();
        values1.put(ExerciseContent.WORD_ID, 1);
        values1.put(ExerciseContent.WORD, "Piazza");
        values1.put(ExerciseContent.PHONETIC, "phonetic1");
        values1.put(ExerciseContent.IMAGE_URL, "http://www.see-and-do-france.com/images/French_flag_design.jpg");
        values1.put(ExerciseContent.SOUND_RECORDING, "url1");
        values1.put(ExerciseContent.WORD_DESCRIPTION, "A piazza is most commonly found at the meeting of two or more streets. Most italian streets have sever- al piazzas with streets radiating from the centre");
        values1.put(ExerciseContent.LANGUAGE_ID, italian_id);
        values1.put(ExerciseContent.LANGUAGE, "ITALIAN");

        ContentValues values2 = new ContentValues();
        values2.put(ExerciseContent.WORD_ID, 2);
        values2.put(ExerciseContent.WORD, "Maestro");
        values2.put(ExerciseContent.PHONETIC, "phonetic2");
        values2.put(ExerciseContent.IMAGE_URL, "http://www.see-and-do-france.com/images/French_flag_design.jpg");
        values2.put(ExerciseContent.SOUND_RECORDING, "url2");
        values2.put(ExerciseContent.WORD_DESCRIPTION, "The master or teacher in an artistic field.");
        values2.put(ExerciseContent.LANGUAGE_ID, italian_id);
        values2.put(ExerciseContent.LANGUAGE, "ITALIANO");

        ContentValues values3 = new ContentValues();
        values3.put(ExerciseContent.WORD_ID, 3);
        values3.put(ExerciseContent.WORD, "Cornetto");
        values3.put(ExerciseContent.PHONETIC, "phonetic3");
        values3.put(ExerciseContent.IMAGE_URL, "http://www.see-and-do-france.com/images/French_flag_design.jpg");
        values3.put(ExerciseContent.SOUND_RECORDING, "url3");
        values3.put(ExerciseContent.WORD_DESCRIPTION, "In 1959, that spican, an italian ice cream manufacturer overcame the problem of soggy waffles by insulating the inside of the waffle cone from the ice cream with a coating of oil, sugar and chocolate");
        values3.put(ExerciseContent.LANGUAGE_ID, italian_id);
        values3.put(ExerciseContent.LANGUAGE, "ITALIANOO");

        getContentResolver().insert(InternalDbContract.insertExercisesUri(), values1);
        getContentResolver().insert(InternalDbContract.insertExercisesUri(), values2);
        getContentResolver().insert(InternalDbContract.insertExercisesUri(), values3);
    }

    @Override
    public void CursorLoaded(Cursor c) {
        Log.d(TAG, "CursorLoaded callback");

        this.mCursor = c;
        mCursor.moveToFirst();
        nextExercise(true);

    }


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

            mCallback.CursorLoaded(mCursor);
            super.onPostExecute(result);
        }
    }

}
