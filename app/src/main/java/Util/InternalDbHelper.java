package Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import java.io.Externalizable;

//TODO: create test case to test setting up of database, test cursors

/**
 * Helper class that creates database to store newly downloaded exercises.
 * Provides methods returning custom cursors over the database of exercises
 */
public class InternalDbHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDatabase;

    private final static int DATABASE_VERSION = 1;

    public InternalDbHelper(Context context) {
        super(context, InternalDbContract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Names of all the columns in the EXERCISES database are all referenced from the ExerciseContent model class
    private static String WORD_ID = ExerciseContent.WORD_ID;
    private static String WORD = ExerciseContent.WORD;
    private static String PHONETIC = ExerciseContent.PHONETIC;
    private static String IMAGE_URL = ExerciseContent.IMAGE_URL;
    private static String SOUND_RECORDING = ExerciseContent.SOUND_RECORDING;
    private static String WORD_DESCRIPTION = ExerciseContent.WORD_DESCRIPTION;
    private static String LANGUAGE_ID = ExerciseContent.LANGUAGE_ID;
    private static String LANGUAGE = ExerciseContent.LANGUAGE;
    private static String LOCALE = ExerciseContent.LOCALE;

    private static String EXERCISES_TABLE_CREATE =
            "CREATE TABLE EXERCISES(_id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, phonetic TEXT, image_url TEXT, sound_recording TEXT, word_description TEXT, language_id INTEGER, language TEXT, locale TEXT)";


    //Names of all the columns in the LANGUAGES database - references from LanguageSelection model class
    private static String LANGUAGE_NAME = LanguageSelection.LANGUAGE_NAME;
    private static String LANGUAGE_HEX = LanguageSelection.LANGUAGE_HEX;
    private static String LANGUAGE_IMAGE_URL = LanguageSelection.LANGUAGE_IMAGE_URL;

    private static String LANGUAGES_TABLE_CREATE =
              "CREATE TABLE LANGUAGES(_id INTEGER PRIMARY KEY AUTOINCREMENT, language_name TEXT, language_hex TEXT, language_image_url TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EXERCISES_TABLE_CREATE);
        db.execSQL(LANGUAGES_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //NOT REQUIRED
    }

    //TODO: create methods to return custom cursors over the data for exercises
    //i.e., public Cursor getExerciseByLanguageId(Integer id){}

    public String getDatabasePath() {
            mDatabase = getWritableDatabase();
        return mDatabase.getPath();
    }

    public Cursor getLanguages() {
        mDatabase = getReadableDatabase();
        String[] projection = {LanguageSelection.LANGUAGE_ID, LanguageSelection.LANGUAGE_NAME, LanguageSelection.LANGUAGE_HEX, LanguageSelection.LANGUAGE_IMAGE_URL};
        return mDatabase.query(LanguageSelection.LANGUAGE_TABLE_NAME, projection, null, null, null, null, null, null);
    }

    public void insertLanguages(ContentValues values) {
        mDatabase = getWritableDatabase();
        mDatabase.insert(LanguageSelection.LANGUAGE_TABLE_NAME, null, values);
    }
}
