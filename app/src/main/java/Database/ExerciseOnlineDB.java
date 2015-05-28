package Database;

import java.util.ArrayList;
import java.util.List;
import Resources.LANGUAGE_ID;

/**
 * Created by Alexandre on 22/05/2015.
 */
public class ExerciseOnlineDB extends OnlineDB {

    public ExerciseOnlineDB(Postgresql database) {
        super(database);
        allArguments.add("word_id");
        allArguments.add("word");
        allArguments.add("phonetic");
        allArguments.add("image_url");
        allArguments.add("sound_recording");
        allArguments.add("word_description");
        allArguments.add("language_id");
        allArguments.add("language");
    }

    public List<ExerciseOnlineDBFormat> getFrench() {
        String query = "SELECT * " +
                        "FROM exercise " +
                        "WHERE language_id = " + LANGUAGE_ID.FRENCH.getNumVal();
        List<List<String>> queryResult = database.query(query, this.allArguments);
        return format(queryResult);
    }


    private List<ExerciseOnlineDBFormat> format(List<List<String>> queryResult) {
        List<ExerciseOnlineDBFormat> result = new ArrayList<>();

        for(List<String> strings : queryResult) {
            ExerciseOnlineDBFormat current = new ExerciseOnlineDBFormat(strings);
            result.add(current);
        }
        return result;
    }


}
