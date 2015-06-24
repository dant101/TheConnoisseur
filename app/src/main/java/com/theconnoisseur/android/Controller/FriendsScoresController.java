package com.theconnoisseur.android.Controller;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.FriendContent;
import com.theconnoisseur.android.Model.FriendsScoresContent;
import com.theconnoisseur.android.Model.InternalDbContract;

import java.util.List;

import Database.ConnoisseurDatabase;
import Database.FriendsOnlineDBFormat;
import Database.ScoreOnlineDB;
import Database.ScoreOnlineDBFormat;
import Util.ResourceCache;

public class FriendsScoresController {
    public static final String TAG = FriendsScoresController.class.getSimpleName();

    private static FriendsScoresController sInstance = null;

    public static synchronized  FriendsScoresController getsInstance() {
        if (sInstance == null) {
            sInstance = new FriendsScoresController();
        }
        return sInstance;
    }

    private FriendsScoresController() {
    }

    public Cursor getFriendsScores(Context context, String username, String best_word, String worst_word) {
        Cursor best_word_cursor = context.getContentResolver().query(InternalDbContract.queryForWordId(best_word), null, null, null, null);
        Cursor worst_word_cursor = context.getContentResolver().query(InternalDbContract.queryForWordId(worst_word), null, null, null, null);

        if (best_word_cursor == null || worst_word_cursor == null) {
            Log.d(TAG, "Initial querying for word_id cursors returned null!");
            return null;
        }

        if(best_word_cursor.moveToFirst() && worst_word_cursor.moveToFirst()) {
            int best_word_id = best_word_cursor.getInt(best_word_cursor.getColumnIndex(ExerciseContent.WORD_ID));
            int best_language_id = best_word_cursor.getInt(best_word_cursor.getColumnIndex(ExerciseContent.LANGUAGE_ID));
            int worst_word_id = best_word_cursor.getInt(worst_word_cursor.getColumnIndex(ExerciseContent.WORD_ID));

            Cursor friends = FriendsController.getsInstance().getYourFriends(username);
            if (!friends.moveToFirst()) { return null; }

            MatrixCursor scores = new MatrixCursor(FriendsScoresContent.FRIENDS_SCORES_PROJECTION);

            do {
                String friend = friends.getString(friends.getColumnIndex(FriendContent.friend));
                Log.d(TAG, "Friend: " + friend + ". Best word id: " + String.valueOf(best_word_id) + ". Worst word id: " + String.valueOf(worst_word_id));

                ScoreOnlineDBFormat friend_best = ConnoisseurDatabase.getInstance().getScoreTable().getScoreAndAttempts(friend, best_word_id);
                ScoreOnlineDBFormat friend_worst = ConnoisseurDatabase.getInstance().getScoreTable().getScoreAndAttempts(friend, worst_word_id);

                if(friend_best == null || friend_worst == null) { Log.d(TAG, "Leaving early, not enough data for these words"); continue; }

                int friend_best_attempts = friend_best.getAttempts_score();
                int friend_worst_attempts = friend_worst.getAttempts_score();

                Log.d(TAG, "best attempts: " + String.valueOf(friend_best_attempts) + ". worst attempts: " + String.valueOf(friend_worst_attempts));

                scores.addRow(new Object[] {
                        best_language_id, 0, friend, best_word, worst_word, friend_best_attempts, friend_worst_attempts
                });

            } while (friends.moveToNext());

            return scores;

        } else {
            Log.d(TAG, "Error with exercises cursor (to find word ids)");
            return null;
        }
    }
}
