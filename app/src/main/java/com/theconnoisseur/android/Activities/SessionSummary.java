package com.theconnoisseur.android.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.theconnoisseur.R;

public class SessionSummary extends ActionBarActivity {

    private ImageView mLanguageImage;
    private TextView mLanguage;
    private TextView mSession;
    private TextView mScore;
    private TextView mTongueType;
    private TextView mTongueDescription;
    private ImageView mShareLink;
    private TextView mBestScore;
    private TextView mBestWord;
    private ImageView mBestWordLanguage;
    private TextView mWorstScore;
    private TextView mWorstWord;
    private ImageView mWorstWordLanguage;
    private Button mPlayNewSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_summary);

        mLanguageImage = (ImageView) findViewById(R.id.language_image);
        mLanguage = (TextView) findViewById(R.id.language);
        mSession = (TextView) findViewById(R.id.session);
        mScore = (TextView) findViewById(R.id.score);
        mTongueType = (TextView) findViewById(R.id.tongue_type);
        mTongueDescription = (TextView) findViewById(R.id.tongue_description);
        mShareLink = (ImageView) findViewById(R.id.share_link);
        mBestScore = (TextView) findViewById(R.id.best_score);
        mBestWord = (TextView) findViewById(R.id.best_word);
        mBestWordLanguage = (ImageView) findViewById(R.id.best_word_language);
        mWorstScore = (TextView) findViewById(R.id.worst_score);
        mWorstWord = (TextView) findViewById(R.id.worst_word);
        mWorstWordLanguage = (ImageView) findViewById(R.id.worst_word_language);
        mPlayNewSession = (Button) findViewById(R.id.play_new_session);


        Intent i = getIntent();
        //Get all relevant information from intent
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_session_summary, menu);
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
