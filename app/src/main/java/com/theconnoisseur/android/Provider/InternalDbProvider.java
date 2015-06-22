package com.theconnoisseur.android.Provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.ExerciseScore;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import java.net.URI;

import Database.ConnoisseurDatabase;
import Util.CursorHelper;
import Util.InternalDbHelper;
import Util.ResourceDownloader;

/**
 * Provides a public interface to enable interaction with internal database. Any database queries must go through this class.
 * Use this class whenever you want to query the internal database, either via the provided methods
 * or with a URI that matches the contract format (see InternalDbContract for examples)
 */
public class InternalDbProvider extends ContentProvider {
    private static final String TAG = InternalDbProvider.class.getSimpleName();

    private static final String TABLE_LANGUAGES = LanguageSelection.LANGUAGE_TABLE_NAME;
    private static final String TABLE_EXERCISES = ExerciseContent.EXERICISE_TABLE_NAME;
    private static final String TABLE_SCORES = ExerciseScore.SCORE_TABLE_NAME;

    private static final int LANGUAGES = 0;
    private static final int LANGUAGES_ID = 1;
    private static final int EXERCISES_ALL = 2;
    private static final int EXERCISES = 3;
    private static final int SCORES = 4;
    private static final int SCORES_ID = 5;
    private static final int SCORES_DELETE = 6;
    private static final int FRIEND_SEARCH = 7;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static SQLiteDatabase mDatabase;
    private InternalDbHelper mHelper;
    private Context mContext;

    static {
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_LANGUAGES, LANGUAGES);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_LANGUAGES + "/*", LANGUAGES_ID);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_EXERCISES, EXERCISES_ALL);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_EXERCISES + "/*", EXERCISES);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_SCORES, SCORES);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_SCORES + "/" + ExerciseScore.DELETE + "/*", SCORES_DELETE);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_SCORES + "/*", SCORES_ID);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", FRIEND_SEARCH);
    }

    public InternalDbProvider(Context context) {
        this.mContext = context;
    }

    public InternalDbProvider() {
        this.mContext = getContext();
    }

    private SQLiteDatabase getDatabase(boolean writable) {

        if (mDatabase == null || !mDatabase.isOpen()) {
            if(writable) {
                mDatabase = mHelper.getWritableDatabase();
            } else {
                mDatabase = mHelper.getReadableDatabase();
            }
        }

        return mDatabase;
    }

    @Override
    public boolean onCreate() {
        mHelper = new InternalDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        Log.d(TAG, "Uri: " + uri.toString());

        switch (URI_MATCHER.match(uri)) {
            case LANGUAGES:
                Log.d(TAG, "querying LANGUAGES...");
                cursor = getDatabase(false).query(
                        TABLE_LANGUAGES, InternalDbContract.PROJECTION_LANGUAGES, selection, null, null, null, sortOrder);
                break;

            case LANGUAGES_ID:
                String query = InternalDbContract.getLanguageId(uri);
                String[] args = {query};
                Log.d(TAG, "querying LANGUAGES with LANGUAGE_ID: " + query);
                cursor = getDatabase(false).query(
                        TABLE_LANGUAGES, InternalDbContract.PROJECTION_LANGUAGES, LanguageSelection.LANGUAGE_ID + "=?", args, null, null, sortOrder);
                break;

            case EXERCISES:
                Log.d(TAG, "querying EXERCISES");
                query = InternalDbContract.getId(uri);
                String[] args2 = {query};
                if (query.equals("0")) {
                    Log.d(TAG, "querying exercise with zero");
                    cursor = getDatabase(false).query(TABLE_EXERCISES, InternalDbContract.PROJECTION_EXERCISES, null, null, null, null, "RANDOM()", "5");
                } else {
                    cursor = getDatabase(false).query(
                            TABLE_EXERCISES, InternalDbContract.PROJECTION_EXERCISES, ExerciseContent.LANGUAGE_ID + "=?", args2, null, null, sortOrder);
                }

                Log.d(TAG, "query: "+ query + "(sort order: " + sortOrder + ")");
                break;

            case EXERCISES_ALL:
                Log.d(TAG, "querying EXERCISES_ALL");
                cursor = getDatabase(false).query(
                    TABLE_EXERCISES, InternalDbContract.PROJECTION_EXERCISES, selection, null, null, null, sortOrder);
                break;

            case SCORES_ID:
                Log.d(TAG, "querying SCORES with WORD_ID");
                query = InternalDbContract.getWordID(uri);
                String[] args3 = {query};
                cursor = getDatabase(false).query(
                    TABLE_SCORES, InternalDbContract.PROJECTION_SCORES, ExerciseScore.WORD_ID + "=?", args3, null, null, null);
                CursorHelper.toString(cursor);
                break;

            case FRIEND_SEARCH:
                Log.d(TAG, "querying FRIENDS with user query");
                String query2 = uri.getLastPathSegment();
                cursor = ResourceDownloader.getSuggestions(query2);
                break;
        }

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "attempting insert operation...");
        switch (URI_MATCHER.match(uri)) {
            case LANGUAGES:
                Log.d(TAG, "inserting into Languages...");
                getDatabase(true).insert(TABLE_LANGUAGES, null, values);
                break;
            case EXERCISES_ALL:
                Log.d(TAG, "inserting into Exercises...");
                getDatabase(true).insert(TABLE_EXERCISES, null, values);
                break;
            case SCORES:
                Log.d(TAG, "inserting into Scores...");
                getDatabase(true).insert(TABLE_SCORES, null, values);
                break;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "attempting delete operation");
        Log.d(TAG, "incoming URI: " + uri.toString());
        switch(URI_MATCHER.match(uri)) {
            case SCORES_DELETE:
                String username = InternalDbContract.getId(uri);
                String[] args = {username};
                Log.d(TAG, "deleting score rows (with username: "+ username +")");
                return getDatabase(true).delete(TABLE_SCORES, ExerciseScore.USER_ID + " = ? ", args);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "attempting update operation");
        switch (URI_MATCHER.match(uri)) {
            case SCORES_ID:
                String word_id = InternalDbContract.getWordID(uri);
                String[] args = {word_id};
                Log.d(TAG, "Updating score row (with ID: " + word_id + ")");
                return getDatabase(true).update(TABLE_SCORES, values, ExerciseScore.WORD_ID + "=?", args);
        }
        return 0;
    }
}

