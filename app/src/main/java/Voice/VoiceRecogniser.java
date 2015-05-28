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
    private VoiceScore voiceScore;

    public boolean clicked;

    private final int MAX_RESULTS = 10;

    /*
     * Called with the application context, string we are searching for, language
     * and voiceResult, which will be updated with the corresponding score when it is computed
     * String must be lowercase
     * Lang must be locale, eg. en-US or ru-RU
     */
    public VoiceRecogniser(Context c, String targetString, String lang, VoiceScore voiceScore) {
        this.targetString = targetString;
        this.voiceScore = voiceScore;

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        listener = new VoiceListener(targetString, voiceScore, lang);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(c);

        speechRecognizer.setRecognitionListener(listener);

    }

    /*
     * Recogniser will start listening for results
     */
    public void startListening() {
        speechRecognizer.startListening(i);
    }

    /*
     * Recogniser will stop listening for results
     */
    public void stopListening() {
        speechRecognizer.stopListening();
    }

    public VoiceScore getVoiceScore() {
        return voiceScore;
    }

}

