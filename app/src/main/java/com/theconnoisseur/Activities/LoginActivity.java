package com.theconnoisseur.Activities;

import android.content.Intent;
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
import com.theconnoisseur.R;

import java.util.List;

import Database.*;
import Util.ImageDownload;


public class LoginActivity extends ActionBarActivity implements ImageDownload.ImageLoaderListener {
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
        String magic = "http://www.see-and-do-france.com/images/French_flag_design.jpg";
        ImageDownload mDownloader = new ImageDownload(magic, LoginActivity.this, mBitmap, this);
        mDownloader.execute();


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
