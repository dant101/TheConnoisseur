package com.theconnoisseur.android.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import Util.InternalDbHelper;

/**
 * Provides a public interface to enable interaction with internal database. Any database queries must go through this class.
 * Use this class whenever you want to query the internal database, either via the provided methods
 * or with a URI that matches the contract format (see InternalDbContract for examples)
 */
public class InternalDbProvider extends ContentProvider {
    private static final String TAG = InternalDbProvider.class.getSimpleName();

    private static final String TABLE_LANGUAGES = LanguageSelection.LANGUAGE_TABLE_NAME;
    private static final String TABLE_EXERCISES = ExerciseContent.EXERICISE_TABLE_NAME;

    private static final int LANGUAGES = 0;
    private static final int EXERCISES_ALL = 1;
    private static final int EXERCISES = 2;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static SQLiteDatabase mDatabase;
    private InternalDbHelper mHelper;
    private Context mContext;

    static {
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_LANGUAGES, LANGUAGES);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_EXERCISES, EXERCISES_ALL);
        URI_MATCHER.addURI(InternalDbContract.CONTENT_AUTHORITY, TABLE_EXERCISES + "/*", EXERCISES);
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

            case EXERCISES:
                Log.d(TAG, "querying EXERCISES");
                String query = InternalDbContract.getId(uri);
                String[] args = {query};

                cursor = getDatabase(false).query(
                        TABLE_EXERCISES, InternalDbContract.PROJECTION_EXERCISES, ExerciseContent.LANGUAGE_ID + "=?", args, null, null, sortOrder);
                break;

            case EXERCISES_ALL:
                cursor = getDatabase(false).query(
                TABLE_EXERCISES, InternalDbContract.PROJECTION_EXERCISES, selection, null, null, null, sortOrder);
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
                Log.d(TAG, "inserting into Exerises...");
                getDatabase(true).insert(TABLE_EXERCISES, null, values);
                break;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Series of pre-made cursor requests, returns specific cursor
     */

    public Cursor getLanguagesCursor() {
        return query(InternalDbContract.queryForLanguages(), null, null, null, null);
    }
}

