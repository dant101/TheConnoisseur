package com.theconnoisseur.android.Activities.Testing;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.theconnoisseur.R;

import java.util.Locale;

import Voice.VoiceRecogniser;

public class VoiceRecogniserTestActivity extends Activity implements AdapterView.OnItemSelectedListener, VoiceRecogniser.VoiceCallback {
    public final static String TAG = VoiceRecogniserTestActivity.class.getSimpleName();

    private TextView mScoreText;
    private EditText mWordInput;
    private Spinner mLocaleSpinner;
    private ImageView mRecord;

    private VoiceRecogniser mVoiceRecogniser;

    private String mWord;
    private String mLocale;
    private String mDeviceLocale;

    private Boolean mClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recogniser_test);

        mScoreText = (TextView) findViewById(R.id.score);
        mWordInput = (EditText) findViewById(R.id.word_input);
        mLocaleSpinner = (Spinner) findViewById(R.id.locale_spinner);
        mRecord = (ImageView) findViewById(R.id.record_icon);
    }

    @Override
    protected void onStart() {
        super.onStart();

        prepareSpinner();
        setListeners();

        mDeviceLocale = Resources.getSystem().getConfiguration().locale.toString();
    }

    private void setListeners() {
        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord();
                mClicked = !mClicked;
                if (mClicked) {
                    mVoiceRecogniser.startListening();
                    mClicked = !mClicked;
                } else {
                    mVoiceRecogniser.stopListening();
                }
            }
        });
    }

    private void prepareSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locale_array, android.R.layout.simple_spinner_dropdown_item);
        mLocaleSpinner.setAdapter(adapter);
        mLocaleSpinner.setOnItemSelectedListener(this);
    }

    private void setLocale(String l) {
        Locale locale = new Locale(l);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void onRecord() {
        String mWord = mWordInput.getText().toString();
        setLocale(mLocale);
        mVoiceRecogniser = new VoiceRecogniser(this,  mWord, mLocale, this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mLocale = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mLocale = "en-GB";
    }

    @Override
    public void updateScore(float a) {
        mVoiceRecogniser.destroyVoiceRecogniser();
        mScoreText.setText(String.valueOf(Math.round(a * 100)) + "%");
    }

    @Override
    public void onStop() {
        setLocale(mDeviceLocale);
        super.onStop();
    }
}
