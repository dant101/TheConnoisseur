package com.theconnoisseur.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.theconnoisseur.Activities.Exercise.ExerciseActivity;
import com.theconnoisseur.Activities.Model.LanguageSelection;
import com.theconnoisseur.R;

import Util.CursorHelper;
import Util.ImageDownloadHelper;
import Util.InternalDBHelper;


public class LoginActivity extends ActionBarActivity implements ImageDownloadHelper.ImageLoaderListener {
    public static final String TAG = LoginActivity.class.getSimpleName();

    private static Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void click (View v) {
        startActivity(new Intent(LoginActivity.this, ExerciseActivity.class));
    }

    public void main_menu_click(View v) {
        startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
    }

    public void downloadTest(View v) {
        //String magic = "http://www.see-and-do-france.com/images/French_flag_design.jpg";
        String magic = "http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/flags/4-russian.png";
        ImageDownloadHelper mDownloader = new ImageDownloadHelper(magic, LoginActivity.this, mBitmap, this);
        mDownloader.execute(true);

    }

    public void dbTest(View v) {
        InternalDBHelper mDbHelper = InternalDBHelper.getInstance(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long rowId1 = db.insert(LanguageSelection.LANGUAGE_TABLE_NAME, null, LanguageSelection.getTestValues1());
        long rowId2 = db.insert(LanguageSelection.LANGUAGE_TABLE_NAME, null, LanguageSelection.getTestValues2());

        String[] projection = {LanguageSelection.LANGUAGE_ID, LanguageSelection.LANGUAGE_NAME, LanguageSelection.LANGUAGE_HEX, LanguageSelection.LANGUAGE_IMAGE_URL};

        Cursor c = db.query(LanguageSelection.LANGUAGE_TABLE_NAME, projection, null, null, null, null, null, null);
        c.moveToFirst();

        CursorHelper.toString(c);
    }

    public void onImageDownloaded(Bitmap bmp) {
        Log.d(TAG, "LoginActivity: onImageDownloaded call back");
        ImageView image = (ImageView) findViewById(R.id.test_image);
        image.setImageBitmap(bmp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
