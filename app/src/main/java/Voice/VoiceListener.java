package Voice;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import static com.theconnoisseur.android.Activities.ExerciseFragment.scoreUpdate;

/**
 * Created by dan on 24/05/15.
 */
public class VoiceListener implements RecognitionListener {

    private String target;
    private VoiceScore score;
    private Locale locale;

    /*
     * Called from VoiceRecogniser
     * Methods will be called when certain actions happen according to the speech recogniser
     */
    VoiceListener(String target, VoiceScore score, String lang) {

        this.target = target;
        this.score = score;

        locale = new Locale(lang);

    }



    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

    }

    /*
     * Called when all results have been received
     */
    @Override
    public void onResults(Bundle bundle) {

        ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        float[] scores = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

        logResults(results, scores);
        getScore(results, scores);

        scoreUpdate(score.getResult());
    }

    // Returns corresponding score, if there is one
    private void getScore(ArrayList<String> results, float[] scores) {
        int i = 0;
        int max = results.size();

        for (String s : results) {
            if (target.toLowerCase(locale).equals(s.toLowerCase(locale))) {
                break;
            }
            i++;
        }

        if (i == max) {
            score.setResult(VoiceScore.FAIL);
        } else {
            score.setResult(scores[i]);
        }
    }

    // For Testing
    private void logResults(ArrayList<String> results, float[] scores) {

        int j = 0;
        for (String s : results) {
            Log.d("Voices recognised", s);
            Log.d("Voices score", ""+scores[j]);
            j++;
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
