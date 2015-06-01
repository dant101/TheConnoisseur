package com.theconnoisseur.android.Activities;

import android.content.Intent;
import android.database.Cursor;
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
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;

import Util.ImageDownloadHelper;
import Util.ToastHelper;

public class CollectionActivity extends ActionBarActivity implements CursorCallback {
    public static final String TAG = CollectionActivity.class.getSimpleName();

    private int mLanguage_id = -1;
    private String mImage_url;
    private String mLanguageName;

    private TextView mLanguage;
    private ListView mListView;
    private Cursor mCursor;
    private Adapter mAdapter;

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
        mLanguage = (TextView) findViewById(R.id.language);

        if (mImage_url != null) {
            ImageDownloadHelper.loadImage(this, (ImageView) findViewById(R.id.language_image), mImage_url);
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

        ListView word_list = (ListView) findViewById(R.id.word_list);

        String[] from = new String[] {ExerciseContent.IMAGE_URL, ExerciseContent.WORD_DESCRIPTION};
        int[] to = new int[] {R.id.word_image, R.id.word_description};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.collection_list_item, c, from, to, BIND_IMPORTANT);

        //Binding for word image
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(view.getId() == R.id.word_image) {
                    ImageDownloadHelper.loadImage(getApplicationContext(), (ImageView) view, cursor.getString(columnIndex));
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Click on item? Actions?

                ToastHelper.toast(CollectionActivity.this, "Item selected: " + String.valueOf(id));
            }
        });
    }

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