package com.theconnoisseur.android.Activities;


import android.app.Activity;
import android.os.Bundle;

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

