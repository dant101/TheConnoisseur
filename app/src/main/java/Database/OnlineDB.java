package Database;

import java.util.ArrayList;
import java.util.List;

public abstract class OnlineDB {
    Postgresql database;
    List<String> allArguments = new ArrayList<>();

    public OnlineDB(Postgresql database) {
        this.database = database;
    }

    abstract <T> List<T> format(List<List<String>> queryResult, Class<T> cls);
}
