package com.theconnoisseur.Activities.Model;

import android.widget.ImageView;

import java.util.HashMap;

public class LanguageSelectionListItem {

    public final static String LANGUAGE_TEXT = "language_text";

    public String language_text;
    public int img_code; //TODO: how do we dynamically get the right flag? helper?

    public LanguageSelectionListItem() {
        this("Language", 1);
    }

    public LanguageSelectionListItem(String language, int img_code) {
        this.language_text = language;
        this.img_code = img_code;
    }

    public HashMap<String, String> getMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(LANGUAGE_TEXT, language_text);

        return map;
    }


    @Override
    public String toString() {
        return language_text;
    }
}
