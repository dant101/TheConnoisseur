package Database;

import java.util.ArrayList;
import java.util.List;

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
        allArguments.add("locale");
    }

    public List<ExerciseOnlineDBFormat> getLanguageByID(int id) {
        String query = "SELECT * " +
                        "FROM exercise " +
                        "WHERE language_id = " + id;
        List<List<String>> queryResult = database.selectQuery(query, this.allArguments);
        return format(queryResult, ExerciseOnlineDBFormat.class);
    }

    @Override
    <ExerciseOnlineDBFormat> List<ExerciseOnlineDBFormat> format(
            List<List<String>> queryResult,
            Class<ExerciseOnlineDBFormat> cls) {

        List<ExerciseOnlineDBFormat> result = new ArrayList<>();

        for(List<String> strings : queryResult) {
            try {
                ExerciseOnlineDBFormat current = cls.getDeclaredConstructor(List.class).newInstance(strings);
                result.add(current);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }


}
