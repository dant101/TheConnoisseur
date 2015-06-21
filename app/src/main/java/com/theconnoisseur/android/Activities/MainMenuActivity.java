package com.theconnoisseur.android.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Testing.VoiceRecogniserTestActivity;
import com.theconnoisseur.android.Model.GlobalPreferenceString;
import com.theconnoisseur.android.Model.InternalDbContract;

import Voice.VoiceRecogniser;

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

    public void goBack(View v) {
        //startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
    }

    public void friendSearchActivity(View v) {
        startActivity(new Intent(this, FriendSearch.class));
    }

    public void voiceRecogniserTest(View v) {
        startActivity(new Intent(MainMenuActivity.this, VoiceRecogniserTestActivity.class));
    }

    public void onSelection(View v) {
        Log.d(TAG, "onSelection Button click registered");
        switch(v.getId()) {

            case R.id.language_selection:
                startActivity(new Intent(MainMenuActivity.this, LanguageSelectionActivity.class));
                break;
            case R.id.collection_selection:
                startActivity(new Intent(MainMenuActivity.this, CollectionSelectionActivity.class));
                break;
            case R.id.logout:
                logOut();
                break;

            //Adventures button?
        }
    }

    private void logOut() {
        String username = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalPreferenceString.USERNAME_PREF, GlobalPreferenceString.GUEST);

        // Delete stored best scores/attempts
        getContentResolver().delete(InternalDbContract.deleteExerciseScore(username), null,null);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(GlobalPreferenceString.SIGNED_IN_PREF, false);
        editor.putString(GlobalPreferenceString.PASSWORD_PREF, "");
        editor.putString(GlobalPreferenceString.USERNAME_PREF, "");
        editor.commit();

        startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
    }
}
