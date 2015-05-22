package com.theconnoisseur.Activities.Model;

import android.util.Log;

import com.theconnoisseur.Activities.Exercise.ExerciseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class holding all the required elements to populate an exercise. Class is presented to ExerciseActivity for easy consumption
 */
public class ExerciseContent {

    private static final String TAG = ExerciseActivity.class.getSimpleName();

    public static final String WORD_ID = "word_id";
    public static final String WORD = "word";
    public static final String PHONETIC = "phonetic";
    public static final String IMAGE_URL = "image_url";
    public static final String SOUND_RECORDING = "sound_recording";
    public static final String WORD_DESCRIPTION = "word_description";
    public static final String LANGUAGE_ID = "language_id";
    public static final String LANGUAGE = "language";

    private int word_id;
    private String word;
    private String phonetic;
    private String image_url;
    private String sound_recording;
    private String word_description;
    private int language_id;
    private String language;

    /**
     * Generic getters
     * @return
     */
    public String getWord() {
        return word;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getSound_recording_url() {
        return sound_recording;
    }

    public int getLanguage_id() {
        return language_id;
    }

    public String getLanguage() {
        return language;
    }

    public String getDescription() {
        return word_description;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public int getWord_id() {
        return word_id;
    }


    /**
     * Parses an 'ExerciseActivity' JSON object and populates the fields.
     * @param json a correctly parsed JSON object (use JSONHelper)
     */
    public ExerciseContent(JSONObject json) {
        try {
            word_id = json.getInt(WORD_ID);
            word = json.getString(WORD);
            phonetic = json.getString(PHONETIC);
            image_url = json.getString(IMAGE_URL);
            sound_recording = json.getString(SOUND_RECORDING);
            word_description = json.getString(WORD_DESCRIPTION);
            language_id = json.getInt(LANGUAGE_ID);
            language = json.getString(LANGUAGE);

        } catch (JSONException c) {
            Log.d(TAG, "Malformed JSON object");
            c.printStackTrace();
        }

    }

}
