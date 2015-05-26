package Voice;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import com.theconnoisseur.Activities.Exercise.ExerciseActivity;

import static java.security.AccessController.getContext;

/**
 * Created by dan on 24/05/15.
 */
public class VoiceRecogniser {

    private SpeechRecognizer speechRecognizer;
    private String targetString;
    private Intent i;
    private VoiceListener listener;

    public boolean clicked;

    private final int max_results = 10;

    /*
     * Called with the string we are searching for, language and the application context
     * String must be lowercase
     * Lang must be in format en-US
     */
    public VoiceRecogniser(Context c, String targetString, String lang) {
        this.targetString = targetString;

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, max_results);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        listener = new VoiceListener(targetString);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(c);

        speechRecognizer.setRecognitionListener(listener);

    }

    /*
     * Recogniser will start to listen for results
     */
    public void startListening() {
        speechRecognizer.startListening(i);
    }

    /*
     * Stop listening and return the confidence score of our target string (0.0 - 1.0)
     * Return -1 if not found
     */
    public float stopListening() {
        speechRecognizer.stopListening();
        float r = listener.result();// <--- Crash
        return r;
    }


}
