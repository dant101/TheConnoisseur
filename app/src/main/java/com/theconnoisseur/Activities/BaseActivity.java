package com.theconnoisseur.Activities;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import Voice.VoiceRecogniser;

/**
 * Base Activity class that all other activities will extend
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

