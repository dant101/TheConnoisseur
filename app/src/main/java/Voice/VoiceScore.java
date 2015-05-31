package Voice;

/**
 * Created by dan on 24/05/15.
 */
public class VoiceScore {

    public static final float NOT_SET = -2;
    public static final float FAIL = -1;

    private float score;

    /*
     * Originally NOT_SET
     * Will stay NOT_SET until final results have been computed
     * Must call reset() before reusing the same voiceRecogniser
     * eg. If a user needs to retry a word as they have failed or want to improve
     * My idea for implementing this efficiently will be to have something like this:-
     *
     *  Executors.newSingleThreadExecutor().execute(new Runnable() {
     *       @Override
     *       public void run() {
     *           while (voiceScore.getResult() == NOT_SET) {
     *               Thread.sleep(500);
     *           }
     *           updateScore();
     *       }
     *   });
     *
     */
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

    public void reset() { score = NOT_SET; }
}
