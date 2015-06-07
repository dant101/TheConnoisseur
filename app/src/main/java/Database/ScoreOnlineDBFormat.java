package Database;

import java.util.List;

/**
 * Created by Alexandre on 06/06/2015.
 */
public class ScoreOnlineDBFormat {
    private int score_id;
    private String username;
    private int word_id;
    private int best_score;

    public ScoreOnlineDBFormat(List<String> list) {
        this.score_id = Integer.parseInt(list.get(0));
        this.username = list.get(1);
        this.word_id = Integer.parseInt(list.get(2));
        this.best_score = Integer.parseInt(list.get(3));
    }

    public int getScore_id() {
        return score_id;
    }

    public int getWord_id() {
        return word_id;
    }

    public int getBest_score() {
        return best_score;
    }

    public String getUsername() {
        return username;
    }
}
