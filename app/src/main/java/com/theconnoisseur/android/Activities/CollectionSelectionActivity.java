package com.theconnoisseur.android.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.ExerciseScore;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Util.ContentDownloadHelper;
import Util.CursorHelper;
import Util.ResourceDownloader;

public class CollectionSelectionActivity extends ActionBarActivity implements CursorCallback{
    public static final String TAG = CollectionSelectionActivity.class.getSimpleName();

    private Cursor mCursor;
    private Adapter mAdapter;
    private ListView mListView;
    private Map<Integer, Float> mAverageScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_selection);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Loads the cursor with languages information to fill ListView
        new CursorPreparationTask(this).execute();

        mListView = (ListView) findViewById(R.id.collections_list);
    }

    public void goBack(View v) {
        startActivity(new Intent(CollectionSelectionActivity.this, MainMenuActivity.class));
    }

    @Override
    public void CursorLoaded(Cursor c) {
        this.mCursor = c;

        getAverageScores();

        ListView collections = (ListView) findViewById(R.id.collections_list);

        //TODO: alter database to include scores and dates
        String[] from = new String[] {LanguageSelection.LANGUAGE_NAME, LanguageSelection.LANGUAGE_IMAGE_URL, LanguageSelection.LANGUAGE_ID, LanguageSelection.LANGUAGE_ID};
        int[] to = new int[] {R.id.language_text, R.id.language_image, R.id.item_order, R.id.collection_list_item};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this , R.layout.collections_selection_list_item, c, from, to, BIND_IMPORTANT);

        //Binding for language image
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.language_image) {
                    ContentDownloadHelper.loadImage(getApplicationContext(), (ImageView) view, cursor.getString(columnIndex));
                    return true;
                }

                if (view.getId() == R.id.collection_list_item) {
                    int language_id = cursor.getInt(columnIndex);
                    Log.d(TAG, "Language_id: " + String.valueOf(language_id));
                    if (mAverageScores.containsKey(language_id)) {
                        float average_best_score = mAverageScores.get(language_id);
                        Log.d(TAG, "average_best_score" + String.valueOf(average_best_score));

                        ((TextView)view.findViewById(R.id.item_score)).setText("Average attempts: " + String.format("%.1f", average_best_score));
                        view.findViewById(R.id.item_score).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.item_score).setVisibility(View.GONE);
                    }

                    double sigma = 0.01;
                    if(mAverageScores.containsKey(language_id) && mAverageScores.get(language_id) < ExerciseContent.AVERAGE_CONNOISSEUR + sigma + 1) {
                        Log.d(TAG, "score: " + String.valueOf(mAverageScores.get(language_id)));
                        view.findViewById(R.id.star).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.star).setVisibility(View.INVISIBLE);
                    }
                    return true;
                }

                return false;
            }
        });

        mAdapter = adapter;
        collections.setAdapter(adapter);

        setListeners();
    }

    private void setListeners() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int language_id = (int) mAdapter.getItemId(position);

                Log.d(TAG, "CollectionActivity: selected item with id " + String.valueOf(language_id));

                //Starts the collections activity for selected language
                Intent intent = new Intent(CollectionSelectionActivity.this, CollectionActivity.class);
                intent.putExtra(ExerciseContent.LANGUAGE_ID, language_id);

                //Assumes the items in the listview are consistent with their language_id (to put image uri on intent)
                //TODO: probably will not be the case later on...
                //TODO: get score here too
                mCursor.moveToPosition(language_id - 1); //DIRTY DIRTY
                String path = mCursor.getString(mCursor.getColumnIndex(LanguageSelection.LANGUAGE_IMAGE_URL));
                String language = mCursor.getString(mCursor.getColumnIndex(LanguageSelection.LANGUAGE_NAME));

                intent.putExtra(ExerciseContent.IMAGE_URL, path);
                intent.putExtra(ExerciseContent.LANGUAGE, language);

                startActivity(intent);
            }
        });

    }

    private void getAverageScores() {
        mAverageScores = new HashMap<Integer, Float>();
        if (!mCursor.moveToFirst()) { return; }
        do {
            int language_id = mCursor.getInt(mCursor.getColumnIndex(LanguageSelection.LANGUAGE_ID));
            Cursor exercises = getContentResolver().query(InternalDbContract.queryForWords(language_id), null, null, null, null);
            int number_exercises = 0;
            int cummulative_attempt_score = 0;

            if (!exercises.moveToFirst()) { continue; }
            do {
                int word_id = exercises.getInt(exercises.getColumnIndex(ExerciseContent.WORD_ID));
                Cursor word_score = getContentResolver().query(InternalDbContract.queryForExerciseScore(word_id), null, null, null, null);

                if (!word_score.moveToFirst()) { continue; }

                // Database currently tracks 'attempts' to mean incorrect attempts before successful pronunciation - hence the +1
                int attempt_score = word_score.getInt(word_score.getColumnIndex(ExerciseScore.ATTEMPTS_SCORE)) + 1;
                number_exercises += 1;
                cummulative_attempt_score += attempt_score;

                Log.d(TAG, "#exercises: " + String.valueOf(number_exercises) + ". Cummulative score: " + String.valueOf(cummulative_attempt_score));

                word_score.close();
            } while (exercises.moveToNext());
            exercises.close();

            float average_score = (float)cummulative_attempt_score / number_exercises;
            if (average_score == 0) { continue; }
            mAverageScores.put(language_id, average_score);
            Log.d(TAG, "putting into mAverageScores: language_id = " + String.valueOf(language_id) + ", average_score = " + String.valueOf(average_score));

        } while (mCursor.moveToNext());
    }

    private class CursorPreparationTask extends AsyncTask<Void, Void, Void> {

        CursorCallback mCallback;
        Cursor mCursor;

        public CursorPreparationTask(CursorCallback callback) {
            this.mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mCursor = getContentResolver().query(InternalDbContract.queryForLanguages(), null, null, null, null);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "CollectionSelectionActivity CursorPreparationTask onPostExecute called");

            mCallback.CursorLoaded(mCursor);

            CursorHelper.toString(mCursor);

            super.onPostExecute(result);
        }
    }
}
