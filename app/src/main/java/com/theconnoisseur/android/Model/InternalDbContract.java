package com.theconnoisseur.android.Model;

import android.net.Uri;

public class InternalDbContract {

    public final static String DATABASE_NAME = "content";
    public final static String DATABASE_PATH = "/data/data/com.theconnoisseur/databases/content";

    public static final String CONTENT_AUTHORITY = "connoisseur";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String[] PROJECTION_LANGUAGES = {
            LanguageSelection.LANGUAGE_ID,
            LanguageSelection.LANGUAGE_NAME,
            LanguageSelection.LANGUAGE_HEX,
            LanguageSelection.LANGUAGE_IMAGE_URL};

    public static Uri queryForLanguages() {
        return CONTENT_URI.buildUpon().appendPath(LanguageSelection.LANGUAGE_TABLE_NAME).build();
    }

    public static Uri insertLanguagesUri() {
        return CONTENT_URI.buildUpon().appendPath(LanguageSelection.LANGUAGE_TABLE_NAME).build();
    }

    public static Uri queryForWords(String id) {
        //TODO: query for words on language id
        return CONTENT_URI.buildUpon().appendPath(ExerciseContent.EXERICISE_TABLE_NAME).build();
    }

}
