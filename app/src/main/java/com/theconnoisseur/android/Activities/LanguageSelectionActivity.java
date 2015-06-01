package com.theconnoisseur.android.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;
import com.theconnoisseur.android.Model.LanguageSelectionListItem;
import com.theconnoisseur.R;
import com.theconnoisseur.android.Provider.InternalDbProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Util.CursorHelper;
import Util.ImageDownloadHelper;
import Util.ResourceDownloader;

public class LanguageSelectionActivity extends ActionBarActivity implements CursorCallback {
    private static final String TAG = LanguageSelectionActivity.class.getSimpleName();

    ListView mListView;
    SimpleCursorAdapter mAdapter;
    Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Loads the cursor with languages information
        new CursorPreparationTask(this).execute();

        mListView = (ListView) findViewById(R.id.languages_list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_language_selection, menu);
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
        startActivity(new Intent(LanguageSelectionActivity.this, MainMenuActivity.class));
    }

    @Override
    public void CursorLoaded(Cursor c) {

        //TODO: Cache loaded images!

        //CursorHelper.toString(c); for testing.
        mCursor = c;

        ListView languages = (ListView) findViewById(R.id.languages_list);

        String[] from = new String[] {LanguageSelection.LANGUAGE_NAME, LanguageSelection.LANGUAGE_IMAGE_URL};
        int[] to = new int[] {R.id.language_text, R.id.language_image};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.language_list_item, c, from, to, BIND_IMPORTANT);

        // Handwritten binder that enables adapter to bind image_path string in cursor with ImageView (from real image stored on app)
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.language_image) {
                    ImageDownloadHelper.loadImage(getApplicationContext(), (ImageView)view, cursor.getString(columnIndex));
                    return true;
                }
                return false;
            }
        });

        mAdapter = adapter;
        languages.setAdapter(adapter);

        setListeners();
    }

    private void setListeners() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Gets unique language_id from selected language item
                int language_id = (int) mAdapter.getItemId(position);

                //Starts the exercise activity with words from the correct language (id as extra)
                Intent intent = new Intent(LanguageSelectionActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseContent.LANGUAGE_ID, language_id);
                startActivity(intent);
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
            mCursor = getContentResolver().query(InternalDbContract.queryForLanguages(), null, null, null, null);

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
