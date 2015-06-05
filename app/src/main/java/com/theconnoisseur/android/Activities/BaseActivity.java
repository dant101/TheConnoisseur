package com.theconnoisseur.android.Activities;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.theconnoisseur.android.Controller.ContentDownloadController;

import Util.ContentSample;

/**
 * Base Activity class that all other activities will extend
 */
public class BaseActivity extends Activity {
    public static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "BaseActivity onStart");
        ContentDownloadController.getInstance().getLanguages(this);

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

