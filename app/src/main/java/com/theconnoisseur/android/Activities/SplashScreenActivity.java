package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.theconnoisseur.R;

public class SplashScreenActivity extends Activity {

    private static final int SPLASH_TIME = 1500;

    private boolean runningPostDelayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runningPostDelayed = false;
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            }
        }, SPLASH_TIME);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!runningPostDelayed) {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            }
        }
        return true;
    }

}
