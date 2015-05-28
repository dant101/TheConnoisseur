package com.theconnoisseur.Activities.Exercise;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.theconnoisseur.Activities.LoginActivity;
import com.theconnoisseur.R;

import java.util.concurrent.ExecutionException;

import Voice.VoiceRecogniser;
import Voice.VoiceScore;

public class ExerciseActivity extends FragmentActivity implements ExerciseFragment.OnFragmentInteractionListener {

    private static final String TAG_EXERCISE_FRAGMENT = "exercise_fragment";

    public static VoiceRecogniser vr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ExerciseFragment.newInstance(), TAG_EXERCISE_FRAGMENT).commit();
        }

        VoiceScore voiceScore = new VoiceScore();

        vr = new VoiceRecogniser(this, "МЕНЯ", "ru-RU", voiceScore);
        vr.clicked = false;

    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_exercise, menu);
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
        startActivity(new Intent(ExerciseActivity.this, LoginActivity.class));
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_exercise2, container, false);
            return rootView;
        }


    }

    public void voiceClick(View v) {

        vr.clicked = !vr.clicked;

        if (vr.clicked) {
            Log.d("voiceClick","Start Listening");
            vr.startListening();
        } else {
            Log.d("voiceClick", "Stop Listening");
            vr.stopListening();
            VoiceScore r = vr.getVoiceScore();
            Log.d("voiceClick", ("Score - "+r.getResult()));
        }

    }

    public void logScoreButton(View v) {
        Log.d("voiceClick", ("Score - "+vr.getVoiceScore().getResult()));
    }
}
