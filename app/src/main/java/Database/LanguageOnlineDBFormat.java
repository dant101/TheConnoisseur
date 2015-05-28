package Database;

import java.util.List;

/**
 * Created by Alexandre on 28/05/2015.
 */
public class LanguageOnlineDBFormat {
    private int language_id;
    private String language_name;
    private String language_hex;
    private String language_image_url;

    public LanguageOnlineDBFormat(List<String> list) {
        this.language_id = Integer.parseInt(list.get(0));
        this.language_name = list.get(1);
        this.language_hex = list.get(2);
        this.language_image_url = list.get(3);
    }

    public int getLanguage_id() {
        return language_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public String getLanguage_hex() {
        return language_hex;
    }

    public String getLanguage_image_url() {
        return language_image_url;
    }
}
