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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
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
import Util.ResourceDownloader;

public class LanguageSelectionActivity extends ActionBarActivity implements CursorCallback {
    private static final String TAG = LanguageSelectionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        // Loads the cursor with languages information
        new CursorPreparationTask(this).execute();
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
        CursorHelper.toString(c);

        ListView languages = (ListView) findViewById(R.id.languages_list);

        String[] from = new String[] {LanguageSelection.LANGUAGE_NAME, LanguageSelection.LANGUAGE_IMAGE_URL};
        int[] to = new int[] {R.id.language_text, R.id.language_image};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.language_list_item, c, from, to, BIND_IMPORTANT);

        // Handwritten binder that enables adapter to bind image_path string in cursor with ImageView (from real image stored on app)
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.language_image) {

                    String path = cursor.getString(columnIndex).replace(File.separator, "");
                    try {
                        FileInputStream f = openFileInput(path);
                        Bitmap b = BitmapFactory.decodeStream(f);
                        f.close();
                        ((ImageView)view).setImageBitmap(b);
                    } catch (FileNotFoundException e) {
                        Log.d(TAG, "FileNotFoundException when decoding saved image");
                        e.printStackTrace();
                    } catch (IOException f) {
                        Log.d(TAG, "Unable to close fileinputstream when decoding image");
                    }

                    return true;
                }
                return false;
            }
        });

        languages.setAdapter(adapter);
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
