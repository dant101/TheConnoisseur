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
        allArguments.add("nesting_level");
        allArguments.add("reply_to_id");
        allArguments.add("comment");
        allArguments.add("time");
        allArguments.add("score");
    }

    /*Returns all the comments corresponding to a specific exercise using its word_id*/
    public List<CommentOnlineDBFormat> getCommentsByWordId(int id) {
        String query = "SELECT * " +
                        "FROM comment" +
                        "WHERE word_id = " + id;
        List<List<String>> queryResult = database.selectQuery(query, this.allArguments);
        return format(queryResult, CommentOnlineDBFormat.class);
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


    /*Creates a new comment that is a reply to another comment
    and puts it in the database
    the "reply_to_id" argument is the comment_id of the comment we are replying to
    */
    public boolean createReplyToComment(int word_id, String username, int reply_to_id,
                                String comment) {
        boolean result;

        String query = "INSERT INTO comment(word_id, username, nesting_level, reply_to_id, " +
                "comment, time, score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        int nesting_level = getNestingLevelByCommentId(reply_to_id) + 1;
        Timestamp time = Util.Time.getCurrentTimestamp();
        int score = 0;

        result = database.createCommentQuery(query, word_id, username, nesting_level,
                reply_to_id, comment, time, score);

        return result;
    }


    /*Returns the nesting level of a specific comment using its unique comment_id*/
    private int getNestingLevelByCommentId(int comment_id) {
        int nesting_level = -1;

        String query = "SELECT nesting_level " +
                "FROM comment " +
                "WHERE comment_id = " + comment_id;

        List<String> arguments = new ArrayList<String>();
        arguments.add("nesting_level");

        List<List<String>> queryResult = database.selectQuery(query, arguments);

        if(queryResult.size() == 1) {
            List<String> row = queryResult.get(0);
            if(row.size() == 1) {
                nesting_level = Integer.parseInt(row.get(0));
            }
        }
        return nesting_level;
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

    /*Creates a new comment that is NOT a reply to another comment
    and puts it in the database
    */
    public boolean createComment(int word_id, String username, String comment) {
        boolean result;

        String query = "INSERT INTO comment(word_id, username, nesting_level, reply_to_id, " +
                "comment, time, score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        int nesting_level = 0; //this comment cannot be a reply
        int reply_to_id = -1; //this comment cannot be a reply
        Timestamp time = Util.Time.getCurrentTimestamp();
        int score = 0;

        result = database.createCommentQuery(query, word_id, username, nesting_level,
                reply_to_id, comment, time, score);

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
