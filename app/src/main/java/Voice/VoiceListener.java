package Voice;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

/**
 * Created by dan on 24/05/15.
 */
public class VoiceListener implements RecognitionListener {

    private String target;
    private VoiceResults vr;

    VoiceListener(String target) {
        this.target = target;
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
        vr = new VoiceResults(results, scores, target);
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    /*
     * Fetch results through VoiceResults
     */
    public float result() {
        return vr.result();
    }
}
