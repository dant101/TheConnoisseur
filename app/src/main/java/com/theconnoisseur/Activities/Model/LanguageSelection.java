package com.theconnoisseur.Activities.Model;

import android.content.ContentValues;

public class LanguageSelection {

    public static final String LANGUAGE_NAME = "language_name";
    public static final String LANGUAGE_ID = "language_id";
    public static final String LANGUAGE_HEX = "language_hex";
    public static final String LANGUAGE_IMAGE_URL = "language_image_url";
    public static final String LANGUAGE_IMAGE = "language_image";

    public ContentValues getTestValues1() {
        ContentValues values = new ContentValues();
        values.put(LanguageSelection.LANGUAGE_ID, 1);
        values.put(LanguageSelection.LANGUAGE_NAME, "French");
        values.put(LanguageSelection.LANGUAGE_HEX, "002395");

        return null;
    }

}
