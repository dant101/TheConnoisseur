package Database;

/**
 * Created by Alexandre on 20/05/2015.
 */
public interface Database {
            /*USAGE EXAMPLE for Database*/
/*

        Postgresql db = new Postgresql("db.doc.ic.ac.uk/g1427115_u","g1427115_u","IOiuiPSi66");
        db.connect();


        LanguageOnlineDB langDB = new LanguageOnlineDB(db);
        List<LanguageOnlineDBFormat> langList = langDB.getAllLanguages();

        for(LanguageOnlineDBFormat l : langList) {
            System.out.println("ID: " + l.getLanguage_id());
            System.out.println("NAME: " + l.getLanguage_name());
            System.out.println("HEX: " + l.getLanguage_hex());
            System.out.println("IMAGE_URL: " + l.getLanguage_image_url());
            System.out.println();
        }
        db.disconnect();

        */


    void connect();
    void disconnect();
}
