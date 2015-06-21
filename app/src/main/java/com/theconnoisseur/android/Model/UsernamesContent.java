package com.theconnoisseur.android.Model;

import android.app.SearchManager;

public class UsernamesContent {
    public static final String TABLE_NAME = "USERNAMES";

    public static final String ID = "_id";
    public static final String USERNAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String INTENT_DATA = SearchManager.SUGGEST_COLUMN_INTENT_DATA;

    public static final String[] COLUMNS = new String[] {ID, USERNAME, INTENT_DATA};
}
