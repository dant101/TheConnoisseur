package com.theconnoisseur.android.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.ExerciseScore;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.SessionSummaryContent;

import java.net.URISyntaxException;

import Util.ContentDownloadHelper;

public class SessionSummary extends ActionBarActivity {
    public static final String TAG = SessionSummary.class.getSimpleName();

    private int mAverageScore;
    private int mWorstScore; //worst attempts performance
    private int mWorstWordId;
    private int mBestWordId;
    private int mBestScore; //best attempts performance
    private int mSessionNumber;
    private int mSessionAttempts;
    private String mWorstWord;
    private String mBestWord;
    private String mFlagUri;
    private int mWordsPassed;
    private String mLanguageString;
    private int mTotalWords;
    private int mLanguageId;
    private String mLanguageHex;

    private ImageView mLanguageImage;
    private TextView mLanguage;
    private TextView mSession;
    private TextView mScore;
    private TextView mTongueType;
    private TextView mTongueDescription;
    private ImageView mShareLink;
    private TextView mPersonalHighlightText;
    private TextView mBestScoreTextView;
    private TextView mBestWorstTextView;
    private TextView mBestWordTextView;
    private ImageView mBestWordLanguage;
    private TextView mWorstScoreTextView;
    private TextView mWorstWordTextView;
    private ImageView mWorstWordLanguage;
    private ImageView mConnoisseurImage;
    private Button mPlayNewSession;

    private ImageView mHeartBest1;
    private ImageView mHeartBest2;
    private ImageView mHeartBest3;
    private ImageView mHeartWorst1;
    private ImageView mHeartWorst2;
    private ImageView mHeartWorst3;

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
        mBestWordTextView = (TextView) findViewById(R.id.best_word);
        mBestWorstTextView = (TextView) findViewById(R.id.best_worst_text);
        mBestWordLanguage = (ImageView) findViewById(R.id.best_word_language);
        mWorstWordTextView = (TextView) findViewById(R.id.worst_word);
        mWorstWordLanguage = (ImageView) findViewById(R.id.worst_word_language);
        mConnoisseurImage = (ImageView) findViewById(R.id.connoisseur_image);
        mPlayNewSession = (Button) findViewById(R.id.play_new_session);
        mHeartBest1 = (ImageView) findViewById(R.id.heart_best_1);
        mHeartBest2 = (ImageView) findViewById(R.id.heart_best_2);
        mHeartBest3 = (ImageView) findViewById(R.id.heart_best_3);
        mHeartWorst1 = (ImageView) findViewById(R.id.heart_worst_1);
        mHeartWorst2 = (ImageView) findViewById(R.id.heart_worst_2);
        mHeartWorst3 = (ImageView) findViewById(R.id.heart_worst_3);

        //Get all relevant information from intent
        Intent i = getIntent();

        mAverageScore = i.getIntExtra(SessionSummaryContent.AVERAGE_SCORE, 0);
        mWorstScore = i.getIntExtra(SessionSummaryContent.WORST_SCORE, 0);
        mBestScore = i.getIntExtra(SessionSummaryContent.BEST_SCORE, 0);
        mWorstWord = i.getStringExtra(SessionSummaryContent.WORST_WORD);
        mWorstWordId = i.getIntExtra(SessionSummaryContent.WORST_WORD_ID, 0);
        mBestWordId = i.getIntExtra(SessionSummaryContent.BEST_WORD_ID, 0);
        mBestWord = i.getStringExtra(SessionSummaryContent.BEST_WORD);
        mFlagUri = i.getStringExtra(SessionSummaryContent.LANGUAGE_FLAG_URI);
        mWordsPassed = i.getIntExtra(SessionSummaryContent.WORDS_PASSED, 0);
        mTotalWords = i.getIntExtra(SessionSummaryContent.TOTAL_WORDS, 0);
        mLanguageString = i.getStringExtra(SessionSummaryContent.LANGUAGE);
        mLanguageId = i.getIntExtra(SessionSummaryContent.LANGUAGE_ID, -1);
        mLanguageHex = i.getStringExtra(SessionSummaryContent.LANGUAGE_HEX);
        mSessionNumber = i.getIntExtra(SessionSummaryContent.SESSION_NUMBER, 1);
        mSessionAttempts = i.getIntExtra(SessionSummaryContent.SESSION_ATTEMPTS, 15);

        setBestWorstWords();
    }

    @Override
    public void onStart() {
        super.onStart();

        ContentDownloadHelper.loadImage(this, mLanguageImage, mFlagUri);
        ContentDownloadHelper.loadImage(this, mBestWordLanguage, mFlagUri);
        ContentDownloadHelper.loadImage(this, mWorstWordLanguage, mFlagUri);

        mLanguage.setText(mLanguageString);
        mScore.setText("(" + String.valueOf(mWordsPassed) + "/" + String.valueOf(mTotalWords) + " passed)");
        mBestWordTextView.setText(mBestWord);
        mWorstWordTextView.setText(mWorstWord);
        mSession.setText("Session " + String.valueOf(mSessionNumber));

        setTongueLevel();
        setTextColour();
    }

    private void setTongueLevel() {
        float average = (float) mSessionAttempts / ExerciseActivity.EXERCISES_PER_SESSION;
        if (average >= ExerciseContent.AVERAGE_TOURIST) {
            mConnoisseurImage.setImageResource(R.drawable.face_barbarian);
            mTongueType.setText(R.string.performance_1);
            mTongueDescription.setText(R.string.barbarian_description);
        } else if (average < ExerciseContent.AVERAGE_TOURIST && average > ExerciseContent.AVERAGE_CONNOISSEUR) {
            mConnoisseurImage.setImageResource(R.drawable.face_tourist);
            mTongueType.setText(R.string.performance_2);
            mTongueDescription.setText(R.string.tourist_description);
        } else if (mAverageScore <= ExerciseContent.AVERAGE_CONNOISSEUR) {
            mConnoisseurImage.setImageResource(R.drawable.face_connoisseur);
            mTongueType.setText(R.string.performance_3);
            mTongueDescription.setText(R.string.connoisseur_description);
        }
    }

    private void setTextColour() {
        try {
            mLanguage.setTextColor(Color.parseColor(mLanguageHex));
            mSession.setTextColor(Color.parseColor(mLanguageHex));
            mScore.setTextColor(Color.parseColor(mLanguageHex));
            mBestWorstTextView.setTextColor(Color.parseColor(mLanguageHex));
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Illegal Hex was provided for language - check database value!");
            e.printStackTrace();
        }
        //Set custom font
        Typeface plantin = Typeface.createFromAsset(getAssets(), "fonts/Plantin_Light.ttf");
        mTongueType.setTypeface(plantin);

    }

    private void setBestWorstWords() {

        mHeartBest1.setImageResource(R.drawable.heart_black_large); mHeartBest2.setImageResource(R.drawable.heart_black_large);
        mHeartBest3.setImageResource(R.drawable.heart_black_large); mHeartWorst1.setImageResource(R.drawable.heart_green_black);
        mHeartWorst2.setImageResource(R.drawable.heart_green_black); mHeartWorst3.setImageResource(R.drawable.heart_green_black);

        setHearts(mBestScore, mHeartBest1, mHeartBest2, mHeartBest3);
        setHearts(mWorstScore, mHeartWorst1, mHeartWorst2, mHeartWorst3);
    }

    private void setHearts(int remaining, ImageView heart1, ImageView heart2, ImageView heart3) {
        switch (remaining) {
            case 0:
            case 1:
                heart1.setImageResource(R.drawable.heart_green_small);
            case 2:
                heart2.setImageResource(R.drawable.heart_green_small);
            case 3:
                heart3.setImageResource(R.drawable.heart_green_small);
        }
    }

    public void newSession(View v) {
        Intent intent;
        if (mLanguageId != -1) {
            intent = new Intent(this, ExerciseActivity.class);
            intent.putExtra(ExerciseContent.LANGUAGE_ID, mLanguageId);
        } else {
            intent = new Intent(this, MainMenuActivity.class);
        }

        startActivity(intent);
        finish();
    }

    public void goBack(View v) {
        startActivity(new Intent(this, LanguageSelectionActivity.class));
        finish();
    }



}
