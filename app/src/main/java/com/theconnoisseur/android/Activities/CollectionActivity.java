package com.theconnoisseur.android.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.theconnoisseur.android.Model.Comment;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.ExerciseScore;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import Util.ContentDownloadHelper;
import Util.ToastHelper;

public class CollectionActivity extends ActionBarActivity implements CursorCallback {
    public static final String TAG = CollectionActivity.class.getSimpleName();

    private int mLanguage_id = -1;
    private String mImage_url;
    private String mLanguageName;

    private TextView mLanguage;
    private TextView mScore;
    private ListView mListView;
    private Cursor mCursor;
    private Adapter mAdapter;

    // Set of mastered words
    private Set<Integer> mMastered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        Intent intent = getIntent();
        mLanguage_id = intent.getIntExtra(ExerciseContent.LANGUAGE_ID, mLanguage_id);
        mImage_url = intent.getStringExtra(ExerciseContent.IMAGE_URL);
        mLanguageName = intent.getStringExtra(ExerciseContent.LANGUAGE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Loads the cursor with words to fill out the ListView
        new CursorPreparationTask(this).execute();

        mListView = (ListView) findViewById(R.id.word_list);
        mScore = (TextView) findViewById(R.id.language_score);
        mLanguage = (TextView) findViewById(R.id.language);

        if (mImage_url != null) {
            ContentDownloadHelper.loadImage(this, (ImageView) findViewById(R.id.language_image), mImage_url);
        }
        mLanguage.setText(mLanguageName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collection, menu);
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
        startActivity(new Intent(CollectionActivity.this, CollectionSelectionActivity.class));
    }

    @Override
    public void CursorLoaded(Cursor c) {
        this.mCursor = c;

        getMasteredWords();

        ListView word_list = (ListView) findViewById(R.id.word_list);

        String[] from = new String[] {ExerciseContent.WORD, ExerciseContent.IMAGE_URL, ExerciseContent.WORD_DESCRIPTION, ExerciseContent.WORD_ID};
        int[] to = new int[] {R.id.word, R.id.word_image, R.id.word_description, R.id.collection_list_item};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.collection_list_item, c, from, to, BIND_IMPORTANT);

        //Binding for word image
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if(view.getId() == R.id.collection_list_item) {

                    // Sets view to invisible if user has not mastered it
//                    int word_id = cursor.getInt(columnIndex);
//                    if (!mMastered.contains(word_id)) {
//                        view.setVisibility(View.GONE);
//                        return true;
//                    }

                    // set listener for 'view_comment' textView
                    view.findViewById(R.id.view_comments).setOnClickListener(new viewCommentsListener(
                            cursor.getInt(cursor.getColumnIndex(ExerciseContent.WORD_ID)),
                            cursor.getString(cursor.getColumnIndex(ExerciseContent.WORD)),
                            cursor.getString(cursor.getColumnIndex(ExerciseContent.IMAGE_URL)),
                            mImage_url, mLanguageName));


                    // Sets recording onClick listener
                    view.findViewById(R.id.listen_icon).setOnClickListener(new recordingListener(cursor.getString(cursor.getColumnIndex(ExerciseContent.SOUND_RECORDING))));

                    return true;
                }

                if(view.getId() == R.id.word_image) {
                    String uri = cursor.getString(columnIndex);
                    if (uri == null) {
                        ((ImageView)view).setImageResource(R.drawable.face_connoisseur);
                    }
                    ContentDownloadHelper.loadImage(getApplicationContext(), (ImageView) view, uri);
                    return true;
                }

                return false;
            }
        });

        mAdapter = adapter;
        word_list.setAdapter(adapter);

        setListeners();
    }

    private void setListeners() {
        //TODO: page listeners?
    }

    // Calculates the user's mastered words - affects UI
    private void getMasteredWords() {
        if (mCursor == null) {return; }

        mMastered = new HashSet<Integer>();
        int total = mCursor.getCount();

        //Cycles through exercises and puts into mMastered words which user has mastered (0 attempts required)
        if (mCursor.moveToFirst()) {
            do {
                int word_id = mCursor.getInt(mCursor.getColumnIndex(ExerciseContent.WORD_ID));
                Cursor score = getContentResolver().query(InternalDbContract.queryForExerciseScore(word_id), null, null, null, null);
                if (score.moveToFirst()) {
                    if (score.getInt(score.getColumnIndex(ExerciseScore.ATTEMPTS_SCORE)) == 0) {
                        mMastered.add(word_id);
                    }
                }
            } while (mCursor.moveToNext());
        }
        mCursor.moveToFirst();

        mScore.setText("Mastered " + String.valueOf(mMastered.size()) + "/" + String.valueOf(total) + " words");
    }


    /**
     * Listener class that starts the appropriate CommentActivity for selected word
     */
    public class viewCommentsListener implements View.OnClickListener {
        private int mWordId;
        private String mWord;
        private String mIllustrationUri;
        private String mFlagUri;
        private String mFlagText;


        public viewCommentsListener(int word_id, String word, String illustration_uri, String flag_uri, String flag_text) {
            mWordId = word_id;
            mWord = word;
            mIllustrationUri = illustration_uri;
            mFlagUri = flag_uri;
            mFlagText = flag_text;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), CommentActivity.class);
            i.putExtra(ExerciseContent.WORD_ID, mWordId);
            i.putExtra(ExerciseContent.WORD, mWord);
            i.putExtra(ExerciseContent.IMAGE_URL, mIllustrationUri);

            Log.d(TAG, "View comments onClick");

            startActivity(i);
        }
    }


    // Listener class for playing a recording
    private class recordingListener implements View.OnClickListener {
        private String path;

        public recordingListener(String path) {
            this.path = path;
        }

        @Override
        public void onClick(View v) {
            if (path == null) { return; }
            path = path.replace(File.separator, "");

            try {
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(getApplicationContext().getFilesDir()+ "/" + path);
                mp.prepare();
                mp.start();

                Log.d(TAG, "mediaPlayer set with source: " + getApplicationContext().getFilesDir() + path);

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {mp.release(); Log.d(TAG, "MediaPlayer onCompletion!");
                    }
                });

            } catch (IOException e) {
                Log.d(TAG, "Unable to prepare sound recording");
                e.printStackTrace();
            }
        }
    }

    /**
     * Async task that prepares all the words for the collection view (from internal database)
     */
    private class CursorPreparationTask extends AsyncTask<Void, Void, Void> {

        CursorCallback mCallback;
        Cursor mCursor;

        public CursorPreparationTask(CursorCallback callback) {
            this.mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mLanguage_id != -1) {
                mCursor = getContentResolver().query(InternalDbContract.queryForWords(mLanguage_id), null, null, null, null);
            } else {
                Log.d(TAG, "CollectionActivity cursor preparation task: Unable to query database, invalid language_id");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "CursorPreparationTask onPostExecute called");

            mCallback.CursorLoaded(mCursor);
            super.onPostExecute(result);
        }
    }

}
