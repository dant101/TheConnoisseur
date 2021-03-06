package com.theconnoisseur.android.Model;

/**
 * Reveals the public schema of a comment object coming from the online database
 */
public class Comment {

    public final static String comment_id = "_id";
    public final static String word_id = "word_id";
    public static final String username = "username";
    public static final String comment = "comment";
    public static final String time = "time";
    public static final String score = "score";
    public static final String parent_path = "parent_path";
    public static final String nesting = "nesting";

    public static final String[] columns = new String[] {comment_id, word_id, username, comment, time, score, parent_path, nesting};

    public static int getNesting(String parent_path) {
        if (parent_path == null || parent_path.isEmpty()) { return 0; }
        String[] paths = parent_path.split("\\.");
        return paths.length - 1;

    }
}
