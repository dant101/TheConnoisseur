package Database;

/**
 * Created by Alexandre on 30/05/2015.
 */
public class ConnoisseurDatabase {
    private final String username = "g1427115_u";
    private final String password = "IOiuiPSi66";
    private final String host = "db.doc.ic.ac.uk/g1427115_u";
    private Postgresql database = new Postgresql(host,username,password);
    private ExerciseOnlineDB exerciseTable = new ExerciseOnlineDB(database);
    private LanguageOnlineDB languageTable = new LanguageOnlineDB(database);
    private LoginOnlineDB loginTable = new LoginOnlineDB(database);

    private static ConnoisseurDatabase connoisseurDatabase = null;

    public static ConnoisseurDatabase getInstance() {
        if(connoisseurDatabase == null) {
            connoisseurDatabase = new ConnoisseurDatabase();
        }
        return connoisseurDatabase;
    }

    public ExerciseOnlineDB getExerciseTable() {
        return exerciseTable;
    }

    public LanguageOnlineDB getLanguageTable() {
        return languageTable;
    }

    public LoginOnlineDB getLoginTable() {
        return loginTable;
    }
}
