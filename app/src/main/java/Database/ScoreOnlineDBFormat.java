package Database;

import java.util.List;

/**
 * Created by Alexandre on 06/06/2015.
 */
public class ScoreOnlineDBFormat {
    private int score_id;
    private String username;
    private int word_id;
    private int percentage_score;
    private int attempts_score;

    public ScoreOnlineDBFormat(List<String> list) {
        this.score_id = Integer.parseInt(list.get(0));
        this.username = list.get(1);
        this.word_id = Integer.parseInt(list.get(2));
        this.percentage_score = Integer.parseInt(list.get(3));
        this.attempts_score = Integer.parseInt(list.get(4));
    }

    public int getScore_id() {
        return score_id;
    }

    public int getWord_id() {
        return word_id;
    }

    public int getPercentage_score() {
        return percentage_score;
    }

    public String getUsername() {
        return username;
    }

    public int getAttempts_score() {return attempts_score; }
}
