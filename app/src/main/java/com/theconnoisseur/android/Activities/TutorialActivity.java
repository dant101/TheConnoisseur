package com.theconnoisseur.android.Activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Model.GlobalPreferenceString;

public class TutorialActivity extends FragmentActivity {
    private static final int TUTORIAL_SCREENS = 4;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new TutorialAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit();
        e.putBoolean(GlobalPreferenceString.SEEN_TUTORIAL, true);
        e.commit();
    }

    public void close(View v) {
        finish();
    }

    private class TutorialAdapter extends FragmentStatePagerAdapter {
        @Override
        public int getCount() {
            return TUTORIAL_SCREENS;
        }

        @Override
        public TutorialFragment getItem(int position) {
            TutorialFragment tutorialFragment = new TutorialFragment();
            tutorialFragment.setPosition(position);
            return tutorialFragment;
        }

        public TutorialAdapter(FragmentManager fm) {
            super(fm);
        }
    }
}
