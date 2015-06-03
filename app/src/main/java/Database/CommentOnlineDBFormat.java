package Database;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Alexandre on 03/06/2015.
 */
public class CommentOnlineDBFormat {
    private int comment_id;
    private int word_id;
    private String username;
    private int nesting_level;
    private int reply_to_id;
    private String comment;
    private Timestamp time;
    private int score;

    public CommentOnlineDBFormat(List<String> list) {
        this.comment_id = Integer.parseInt(list.get(0));
        this.word_id = Integer.parseInt(list.get(1));
        this.username = list.get(2);
        this.nesting_level = Integer.parseInt(list.get(3));
        this.reply_to_id = Integer.parseInt(list.get(4));
        this.comment = list.get(5);
        this.time = Timestamp.valueOf(list.get(6));
        this.score = Integer.parseInt(list.get(7));
    }

    public int getComment_id() {
        return comment_id;
    }

    public int getWord_id() {
        return word_id;
    }

    public String getUsername() {
        return username;
    }

    public int getReply_to_id() {
        return reply_to_id;
    }

    public int getNesting_level() {
        return nesting_level;
    }

    public String getComment() {
        return comment;
    }

    public int getScore() {
        return score;
    }

    public Timestamp getTime() {
        return time;
    }
}
