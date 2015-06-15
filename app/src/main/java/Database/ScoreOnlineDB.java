package Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandre on 06/06/2015.
 */
public class ScoreOnlineDB extends OnlineDB {

    public ScoreOnlineDB(Postgresql database) {
        super(database);
        allArguments.add("score_id");
        allArguments.add("username");
        allArguments.add("word_id");
        allArguments.add("percentage_score");
        allArguments.add("attempts_score");
    }

    /*Update/inserts a new score into the database
    if the variable attempts_score is set to null, then its value remains teh same as before
     */

    public void updateScoreAndAttempts(String username, int word_id, int percentage_score, Integer attempts_score) {
        String query = "";
        if(isUserScoreInDatabase(username, word_id)) {
            if(attempts_score != null) {
                query = "UPDATE score" +
                        " SET percentage_score = " + percentage_score +
                        ", attempts_score = " + attempts_score +
                        " WHERE word_id = " + word_id +
                        " AND username = ?";
                database.createScoreQuery(query, username, null, null, null);
            } else {
                query = "UPDATE score" +
                        " SET percentage_score = " + percentage_score +
                        " WHERE word_id = " + word_id +
                        " AND username = ?";
                database.createScoreQuery(query, username, null, null, null);
            }

        } else {
            query = "INSERT INTO score(username, word_id, percentage_score, attempts_score) " +
                    "VALUES (?, ?, ?, ?)";
            database.createScoreQuery(query, username, word_id, percentage_score, attempts_score);
        }
    }

    /*Checks that a user score is in the DB*/
    private boolean isUserScoreInDatabase(String username, int word_id) {
        String query = "SELECT * " +
                "FROM score " +
                "WHERE word_id = " + word_id +
                " AND username = ?";
        List<List<String>> queryResult = database.scoreQuery(query, this.allArguments, username);
        return queryResult.size() != 0;
    }

    /*Returns a unique ScoreOnlineDBFormat corresponding to a username and a word_id*/
    public ScoreOnlineDBFormat getScoreAndAttempts(String username, int word_id) {
        String query = "SELECT * " +
                "FROM score " +
                "WHERE word_id = " + word_id +
                " AND username = ?";

        List<List<String>> queryResult = database.scoreQuery(query, this.allArguments, username);

        if(queryResult.size() == 1) {
            return format(queryResult,ScoreOnlineDBFormat.class).get(0);
        }
        return null;
    }

    @Override
    <ScoreOnlineDBFormat> List<ScoreOnlineDBFormat> format(List<List<String>> queryResult, Class<ScoreOnlineDBFormat> cls) {
        List<ScoreOnlineDBFormat> result = new ArrayList<>();

        for(List<String> strings : queryResult) {
            try {
                ScoreOnlineDBFormat current = cls.getDeclaredConstructor(List.class).newInstance(strings);
                result.add(current);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
