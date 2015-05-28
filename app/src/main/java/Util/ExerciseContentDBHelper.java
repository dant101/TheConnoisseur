package Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.theconnoisseur.Activities.Model.ExerciseContent;
import com.theconnoisseur.Activities.Model.LanguageSelection;

//TODO: create test case to test setting up of database, test cursors

/**
 * Helper class that creates database to store newly downloaded exercises.
 * Provides methods returning custom cursors over the database of exercises
 */
public class ExerciseContentDBHelper extends SQLiteOpenHelper {

    private static ExerciseContentDBHelper sHelper;

    public static ExerciseContentDBHelper getInstance(Context context) {
        if (sHelper == null) {
            sHelper = new ExerciseContentDBHelper(context);
        }
        return sHelper;
    }

    private final static String DATABASE_NAME = "exercises";
    private final static int DATABASE_VERSION = 1;

    private ExerciseContentDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    private static String EXERCISES_TABLE_CREATE =
            "CREATE TABLE EXERCISES(\"_id\" INTEGER PRIMARY KEY, \"word_id\" INTEGER, \"word\" TEXT, \"phonetic\" TEXT, \"image_url\" TEXT, \"sound_recording\" TEXT, \"word_description\" TEXT, \"language_id\" INTEGER, \"language\" TEXT)";


    //Names of all the columns in the LANGUAGES database - references from LanguageSelection model class
    private static String LANGUAGE_NAME = LanguageSelection.LANGUAGE_NAME;
    private static String LANGUAGE_HEX = LanguageSelection.LANGUAGE_HEX;
    private static String LANGUAGE_IMAGE_URL = LanguageSelection.LANGUAGE_IMAGE_URL;
    private static String LANGUAGE_IMAGE = LanguageSelection.LANGUAGE_IMAGE;

    private static String LANGUAGES_TABLE_CREATE =
              "CREATE TABLE LANGUAGES(_id INTEGER PRIMARY KEY AUTOINCREMENT, language_name TEXT, language_hex TEXT, language_image_url TEXT, language_image BLOB NOT NULL)";



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
}
