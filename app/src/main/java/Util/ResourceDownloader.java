package Util;

import android.content.ContentValues;
import android.content.Context;

import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

import java.util.List;

import Database.ConnoisseurDatabase;
import Database.LanguageOnlineDBFormat;

/**
 * Executes high level actions with domain specific effects. I.e. download & store all languages/ all words.
 * Completes actions on the calling thread, must be called in a async-task.
 */
public class ResourceDownloader {

    /**
     * Downloads all the languages available from the online database and associated images
     */
    public static void downloadLanguages(Context context) {

        List<LanguageOnlineDBFormat> rows = ConnoisseurDatabase.getInstance().getLanguageTable().getAllLanguages();

        for (LanguageOnlineDBFormat row : rows) {
            ContentValues values = new ContentValues();

            values.put(LanguageSelection.LANGUAGE_ID, row.getLanguage_id());
            values.put(LanguageSelection.LANGUAGE_NAME, row.getLanguage_name());
            values.put(LanguageSelection.LANGUAGE_HEX, row.getLanguage_hex());
            values.put(LanguageSelection.LANGUAGE_IMAGE_URL, row.getLanguage_image_url());

            context.getContentResolver().insert(InternalDbContract.insertLanguagesUri(), values);

            ImageDownloadHelper.getBitmapFromUrl(row.getLanguage_image_url(), context, true);
        }
    }
}
