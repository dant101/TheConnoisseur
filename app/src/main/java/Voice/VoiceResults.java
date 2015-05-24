package Voice;

import java.util.ArrayList;

/**
 * Created by dan on 24/05/15.
 */
public class VoiceResults {

    ArrayList<String> results;
    float[] scores;
    String target;

    VoiceResults(ArrayList<String> results, float[] scores, String target) {
        this.results = results;
        this.scores = scores;
        this.target = target;
    }

    /*
     * Search results and return corresponding confidence score (0.0 - 1.0)
     * Return -1 if target not found
     */
    public float result() {

        int i = 0;
        int max = results.size();

        for (String s : results) {
            if (target == s) {
                break;
            }
            i++;
        }

        if (i == max) {
            return -1;
        } else {
            return scores[i];
        }

    }
}
