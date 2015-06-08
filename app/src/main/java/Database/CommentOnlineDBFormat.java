package Database;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

public class CommentOnlineDBFormat {
    private int comment_id;
    private int word_id;
    private String username;
    private String comment;
    private Timestamp time;
    private int score;
    private String parent_path;

    public CommentOnlineDBFormat(List<String> list) {
        this.comment_id = Integer.parseInt(list.get(0));
        this.word_id = Integer.parseInt(list.get(1));
        this.username = list.get(2);
        this.comment = list.get(3);
        this.score = Integer.parseInt(list.get(4));
        this.parent_path = list.get(5);
        this.time = java.sql.Timestamp.valueOf(list.get(6));
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

    public String getComment() {
        return comment;
    }

    public int getScore() {
        return score;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getParent_path() {
        return parent_path;
    }
}
