package Database;

import java.util.List;

/**
 * Created by Alexandre on 22/05/2015.
 */
public class ExerciseOnlineDBFormat {
    private int word_id;
    private String word;
    private String phonetic;
    private String image_url;
    private String sound_recording;
    private String word_description;
    private int language_id;
    private String language;

    public ExerciseOnlineDBFormat(List<String> list) {
        this.word_id = Integer.parseInt(list.get(0));
        this.word = list.get(1);
        this.phonetic = list.get(2);
        this.image_url = list.get(3);
        this.sound_recording = list.get(4);
        this.word_description = list.get(5);
        this.language_id = Integer.parseInt(list.get(6));
        this.language = list.get(7);
    }

    public int getWord_id() {
        return word_id;
    }

    public String getWord() { return word; }

    public String getPhonetic() {
        return phonetic;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getSound_recording() {
        return sound_recording;
    }

    public String getWord_description() {
        return word_description;
    }

    public int getLanguage_id() {
        return language_id;
    }

    public String getLanguage() {
        return language;
    }
}
