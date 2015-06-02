package Util;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import java.util.List;

import Database.ConnoisseurDatabase;
import Database.ExerciseOnlineDBFormat;
import Database.LanguageOnlineDBFormat;

/**
 * Executes high level actions with domain specific effects. I.e. download & store all languages/ all words.
 * Completes actions on the calling thread, must be called in a async-task.
 */
public class ResourceDownloader {
    public static final String TAG = ResourceDownloader.class.getSimpleName();

    /**
     * Downloads all the languages available from the online database and associated images
     */
    public static void downloadLanguages(Context context) {

        List<LanguageOnlineDBFormat> rows = ConnoisseurDatabase.getInstance().getLanguageTable().getAllLanguages();

        for (LanguageOnlineDBFormat row : rows) {
            ContentValues values = new ContentValues();

            values.put(LanguageSelection.LANGUAGE_ID, row.getLanguage_id());
            values.put(LanguageSelection.LANGUAGE_NAME, row.getLanguage_name().toUpperCase());
            values.put(LanguageSelection.LANGUAGE_HEX, row.getLanguage_hex());
            values.put(LanguageSelection.LANGUAGE_IMAGE_URL, row.getLanguage_image_url());

            context.getContentResolver().insert(InternalDbContract.insertLanguagesUri(), values);

            ContentDownloadHelper.getBitmapFromUrl(row.getLanguage_image_url(), context, true);
        }
    }

    /**
     * Downloads all the exercises for a given language (identified with id) and stores all relevant data and images
     * @param context context required for saving image to app storage
     * @param language_id id that identifies required language
     */
    public static void downloadExercises(Context context, int language_id) {

        List<ExerciseOnlineDBFormat> rows = ConnoisseurDatabase.getInstance().getExerciseTable().getLanguageByID(language_id);

        for (ExerciseOnlineDBFormat row : rows) {
            Log.d(TAG, "row: " + row.getWord());
            ContentValues values = new ContentValues();

            values.put(ExerciseContent.WORD_ID, row.getWord_id());
            values.put(ExerciseContent.WORD, row.getWord());
            values.put(ExerciseContent.PHONETIC, row.getPhonetic());
            values.put(ExerciseContent.IMAGE_URL, row.getImage_url());
            values.put(ExerciseContent.SOUND_RECORDING, row.getSound_recording());
            values.put(ExerciseContent.WORD_DESCRIPTION, row.getWord_description());
            values.put(ExerciseContent.LANGUAGE_ID, row.getLanguage_id());
            values.put(ExerciseContent.LANGUAGE, row.getLanguage());

            context.getContentResolver().insert(InternalDbContract.insertExercisesUri(), values);

            ContentDownloadHelper.getBitmapFromUrl(row.getImage_url(), context, true);
            ContentDownloadHelper.saveSoundFile(context, row.getSound_recording());
        }
    }
}
