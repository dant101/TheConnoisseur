package Database;

/**
 * Created by Alexandre on 20/05/2015.
 */
public interface Database {
            /*USAGE EXAMPLE for Database*/
/*
        Postgresql db = new Postgresql("db.doc.ic.ac.uk/g1427115_u","g1427115_u","IOiuiPSi66");
        db.connect();

        ExerciseOnlineDB r = new ExerciseOnlineDB(db);
        List<ExerciseOnlineDBFormat> l = r.getFrench();

        for(ExerciseOnlineDBFormat f : l) {
            System.out.print(f.getWord());
            System.out.print(f.getLanguage());
            System.out.print(f.getImage_url());
            System.out.println();
        }


        LoginOnlineDB loginDB = new LoginOnlineDB(db);
        System.out.println("CREATION:" + loginDB.create("alex", "lol", "alex@cool.com"));
        System.out.println("LOGIN:" + loginDB.login("alex", "loll"));

        db.disconnect();

        */


    void connect();
    void disconnect();
}
