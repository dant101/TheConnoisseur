package Voice;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

/**
 * Created by dan on 24/05/15.
 */
public class VoiceRecogniser {

    private SpeechRecognizer speechRecognizer;
    private String targetString;
    private Intent i;
    private VoiceListener listener;

    private final int max_results = 10;

    /*
     * Called with the string we are searching for, language and the application context
     */
    VoiceRecogniser(Context c, String targetString, String lang) {
        this.targetString = targetString;

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(c);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, max_results);

        listener = new VoiceListener(targetString);
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
        return listener.result();
    }

}
