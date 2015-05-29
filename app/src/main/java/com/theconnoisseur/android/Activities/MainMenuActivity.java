package com.theconnoisseur.android.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.theconnoisseur.R;

public class MainMenuActivity extends ActionBarActivity {
    public static final String TAG = MainMenuActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    @Override
    protected void onStart() {
        final Typeface font_bold = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Bold.ttf");
        final Typeface font_normal = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Medium.ttf");

        TextView language_selection_title = (TextView) findViewById(R.id.language_selection_title);
        TextView language_selection_desc = (TextView) findViewById(R.id.language_selection_desc);
        TextView connoisseur_collection_title = (TextView) findViewById(R.id.connoisseur_collection_title);
        TextView connoisseur_collection_desc = (TextView) findViewById(R.id.connoisseur_collection_desc);

        language_selection_title.setTypeface(font_bold);
        //connoisseur_collection_title.setTypeface(font_bold);
        language_selection_desc.setTypeface(font_normal);
        //connoisseur_collection_desc.setTypeface(font_normal);

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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
        startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
    }

    public void onSelection(View v) {
        Log.d(TAG, "onSelection Button click registered");
        switch(v.getId()) {

            case R.id.language_selection_button:
                startActivity(new Intent(MainMenuActivity.this, LanguageSelectionActivity.class));
                break;
            case R.id.connoisseur_collection_button:

                Toast.makeText(this, "Button selection not currently supported", Toast.LENGTH_SHORT).show();
        }
    }
}
