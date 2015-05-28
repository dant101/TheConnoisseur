package Voice;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by dan on 24/05/15.
 */
public class VoiceScore {

    public static final float NOT_SET = -2;
    public static final float FAIL = -1;

    private float score;

    public VoiceScore() {
        score = NOT_SET;
    }

    public void setResult(float r) {
        if (r >= 0 && r <= 1) {
            score = r;
        } else {
            score = FAIL;
        }
    }

    public float getResult() {
        return score;
    }
}
