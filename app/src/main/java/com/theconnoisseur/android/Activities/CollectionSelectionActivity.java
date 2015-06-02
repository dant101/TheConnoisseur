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

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import Util.ContentDownloadHelper;

public class CollectionSelectionActivity extends ActionBarActivity implements CursorCallback{
    public static final String TAG = CollectionSelectionActivity.class.getSimpleName();

    private Cursor mCursor;
    private Adapter mAdapter;
    private ListView mListView;

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
        startActivity(new Intent(CollectionSelectionActivity.this, MainMenuActivity.class));
    }

    @Override
    public void CursorLoaded(Cursor c) {
        this.mCursor = c;

        ListView collections = (ListView) findViewById(R.id.collections_list);

        //TODO: alter database to include scores and dates
        String[] from = new String[] {LanguageSelection.LANGUAGE_NAME, LanguageSelection.LANGUAGE_IMAGE_URL, LanguageSelection.LANGUAGE_ID};
        int[] to = new int[] {R.id.language_text, R.id.language_image, R.id.item_order};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this , R.layout.collections_selection_list_item, c, from, to, BIND_IMPORTANT);

        //Binding for language image
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.language_image) {
                    ContentDownloadHelper.loadImage(getApplicationContext(), (ImageView) view, cursor.getString(columnIndex));
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
