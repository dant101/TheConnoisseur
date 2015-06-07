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
        allArguments.add("best_score");
    }

    public void updateBestScore(String username, int word_id, int score) {
        String query = "";
        if(isUserScoreInDatabase(username, word_id)) {
            query = "UPDATE score " +
                    "SET best_score = " + score +
                    "WHERE word_id = " + word_id +
                    " AND username = ?";
            database.createScoreQuery(query, username, null, null);

        } else {
            query = "INSERT INTO score(username, word_id, best_score) " +
                    "VALUES (?, ?, ?)";
            database.createScoreQuery(query, username, word_id, score);
        }
    }

    private boolean isUserScoreInDatabase(String username, int word_id) {
        String query = "SELECT * " +
                "FROM score " +
                "WHERE word_id = " + word_id +
                " AND username = ?";
        List<List<String>> queryResult = database.scoreQuery(query, this.allArguments, username);
        return queryResult.size() != 0;
    }

    public int getBestScore(String username, int word_id) {
        int bestScore = 0;

        String query = "SELECT best_score " +
                "FROM score " +
                "WHERE word_id = " + word_id +
                " AND username = ?";

        List<String> arguments = new ArrayList<String>();
        arguments.add("best_score");

        List<List<String>> queryResult = database.scoreQuery(query, arguments, username);

        if(queryResult.size() == 1) {
            List<String> row = queryResult.get(0);
            if(row.size() == 1) {
                bestScore = Integer.parseInt(row.get(0));
            }
        }
        return bestScore;
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
