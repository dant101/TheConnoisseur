package Database;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandre on 03/06/2015.
 */
public class CommentOnlineDB extends OnlineDB {

    public CommentOnlineDB(Postgresql database) {
        super(database);
        allArguments.add("comment_id");
        allArguments.add("word_id");
        allArguments.add("username");
        allArguments.add("comment");
        allArguments.add("time");
        allArguments.add("score");
        allArguments.add("parent_path");
    }

    /*Returns all the comments corresponding to a specific exercise using its word_id*/
    public List<CommentOnlineDBFormat> getCommentsByWordId(int id) {
        String query = "SELECT * " +
                        "FROM comment " +
                        "WHERE word_id = " + id;
        List<List<String>> queryResult = database.selectQuery(query, this.allArguments);
        return format(queryResult, CommentOnlineDBFormat.class);
    }

    /*Returns the comment corresponding to a specific comment_id,
     returns null if the comment_id does not exists*/
    public CommentOnlineDBFormat getCommentByCommentId(int id) {
        CommentOnlineDBFormat result = null;

        String query = "SELECT * " +
                "FROM comment" +
                "WHERE comment_id = " + id;
        List<List<String>> queryResult = database.selectQuery(query, this.allArguments);
        List<CommentOnlineDBFormat> list = format(queryResult, CommentOnlineDBFormat.class);

        if(list.size() == 1) {
            result = list.get(0);
        }

        return result;
    }

    /*Adds one to the current score of a comment using its comment_id*/
    public void addOneToScore(int comment_id) {
        int score = getScoreByCommentId(comment_id) + 1;

        String query = "UPDATE comment "+
                "SET score = " + score +
                "WHERE comment_id = " + comment_id;
        database.insertQuery(query);
    }

    /*Subtracts one from the current score of a comment using its comment_id*/
    public void subtractOneFromScore(int comment_id) {
        int score = getScoreByCommentId(comment_id) - 1;

        String query = "UPDATE comment "+
                "SET score = " + score +
                "WHERE comment_id = " + comment_id;
        database.insertQuery(query);
    }

    /*Returns the score of a comment using its unique comment_id*/
    private int getScoreByCommentId(int comment_id) {
        int score = 0;

        String query = "SELECT score " +
                "FROM comment " +
                "WHERE comment_id = " + comment_id;

        List<String> arguments = new ArrayList<String>();
        arguments.add("score");

        List<List<String>> queryResult = database.selectQuery(query, arguments);

        if(queryResult.size() == 1) {
            List<String> row = queryResult.get(0);
            if(row.size() == 1) {
                score = Integer.parseInt(row.get(0));
            }
        }
        return score;
    }

    /*Creates a new comment with no parent and puts it in the database */
    public boolean createComment(int word_id, String username, String comment) {
        return createComment(word_id, username, comment, null);
    }

    /*Creates a new comment with a parent and puts it tin the database*/
    public boolean createComment(int word_id, String username, String comment, String parent_path) {
        boolean result;

        String query = "INSERT INTO comment(word_id, username, comment, time, score, parent_path) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Timestamp time = Util.Time.getCurrentTimestamp();
        int score = 0;

        result = database.createCommentQuery(query, word_id, username,
                comment, time, score, parent_path);

        return result;
    }



    @Override
    <CommentOnlineDBFormat> List<CommentOnlineDBFormat> format(
            List<List<String>> queryResult,
            Class<CommentOnlineDBFormat> cls) {

        List<CommentOnlineDBFormat> result = new ArrayList<>();

        for(List<String> strings : queryResult) {
            try {
                CommentOnlineDBFormat current = cls.getDeclaredConstructor(List.class).newInstance(strings);
                result.add(current);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
