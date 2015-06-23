package com.theconnoisseur.android.Model;

public class FriendsScoresContent {

    public static final String LANGUAGE_ID = "language_id";
    public static final String ID = "_id";
    public static final String FRIEND = "friend"; // First column must be named as such
    public static final String BEST_WORD = "best_word";
    public static final String WORST_WORD = "worst_word";
    public static final String BEST_WORD_ATTEMPTS = "best_word_attempts";
    public static final String WORST_WORD_ATTEMPTS = "worst_word_attempts";

    public static final String[] FRIENDS_SCORES_PROJECTION = new String[]{LANGUAGE_ID, ID, FRIEND, BEST_WORD, WORST_WORD, BEST_WORD_ATTEMPTS, WORST_WORD_ATTEMPTS};
}
