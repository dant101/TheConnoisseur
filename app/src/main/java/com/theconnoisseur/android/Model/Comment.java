package com.theconnoisseur.android.Model;

/**
 * Reveals the public schema of a comment object coming from the online database
 */
public class Comment {

    public final static String comment_id = "comment_id";
    public final static String word_id = "word_id";
    public static final String username = "username";
    public static final String nesting_level = "nesting_level";
    public static final String reply_to_id = "reply_to_id";
    public static final String comment = "comment";
    public static final String time = "time";
    public static final String score = "score";

    public static final String[] columns = new String[] {comment_id, word_id, username, nesting_level, reply_to_id, comment, time, score};
}
