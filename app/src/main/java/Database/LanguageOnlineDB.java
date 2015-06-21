package Database;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alexandre on 28/05/2015.
 */
public class LanguageOnlineDB extends OnlineDB {

    public LanguageOnlineDB(Postgresql database) {
        super(database);
        allArguments.add("language_id");
        allArguments.add("language_name");
        allArguments.add("language_hex");
        allArguments.add("language_image_url");
        allArguments.add("placeholder_tourist");
        allArguments.add("placeholder_barbarian");
        allArguments.add("placeholder_connoisseur");
    }

    public List<LanguageOnlineDBFormat> getAllLanguages() {
        String query = "SELECT * " +
                "FROM languages";
        List<List<String>> queryResult = database.selectQuery(query, this.allArguments);
        return format(queryResult, LanguageOnlineDBFormat.class);
    }

    @Override
    <LanguageOnlineDBFormat> List<LanguageOnlineDBFormat> format(
            List<List<String>> queryResult, Class<LanguageOnlineDBFormat> cls) {

        List<LanguageOnlineDBFormat> result = new ArrayList<>();

        for(List<String> strings : queryResult) {
            try {
                LanguageOnlineDBFormat current = cls.getDeclaredConstructor(List.class).newInstance(strings);
                result.add(current);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
