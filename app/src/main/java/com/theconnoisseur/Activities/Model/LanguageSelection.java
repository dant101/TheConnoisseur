package com.theconnoisseur.Activities.Model;

import android.content.ContentValues;

public class LanguageSelection {

    public static final String LANGUAGE_TABLE_NAME = "LANGUAGES";
    public static final String LANGUAGE_NAME = "language_name";
    public static final String LANGUAGE_ID = "_id";
    public static final String LANGUAGE_HEX = "language_hex";
    public static final String LANGUAGE_IMAGE_URL = "language_image_url";
    public static final String LANGUAGE_IMAGE = "language_image";

    public static ContentValues getTestValues1() {
        ContentValues values = new ContentValues();
        values.put(LanguageSelection.LANGUAGE_ID, 1);
        values.put(LanguageSelection.LANGUAGE_NAME, "FRENCH");
        values.put(LanguageSelection.LANGUAGE_HEX, "002395");
        values.put(LanguageSelection.LANGUAGE_IMAGE_URL, "http://www.see-and-do-france.com/images/French_flag_design.jpg");

        return values;
    }

    public static ContentValues getTestValues2() {
        ContentValues values = new ContentValues();
        values.put(LanguageSelection.LANGUAGE_ID, 2);
        values.put(LanguageSelection.LANGUAGE_NAME, "ITALIAN");
        values.put(LanguageSelection.LANGUAGE_HEX, "009246");
        values.put(LanguageSelection.LANGUAGE_IMAGE_URL, "http://www.theflagshop.co.uk/ekmps/shops/speed/images/italian-italy-flag-130-p.jpg");

        return values;
    }

}
