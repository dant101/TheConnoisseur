package Util;

import android.content.ContentValues;
import android.content.Context;

import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;

/**
 * Executes high level actions with domain specific effects. I.e. download & store all languages/ all words.
 * Completes actions on the calling thread, must be called in a async-task.
 */
public class ResourceDownloader {

    /**
     * Downloads all the languages available from the online database and associated images
     */
    public static void downloadLanguages(Context context) {
        //TODO: use postgres db API to get map/list

        // <TestingValues>
        String url1 = "http://www.see-and-do-france.com/images/French_flag_design.jpg";
        String url2 = "http://fc01.deviantart.net/fs71/i/2012/200/2/a/italy_flag_wallpaper_by_peluch-d57u6f4.png";
        String url3 = "http://2.cdn.nhle.com/nhl/images/upload/2011/12/russia-flag.jpg";

        ContentValues values1 = new ContentValues();
        values1.put(LanguageSelection.LANGUAGE_ID, 5);
        values1.put(LanguageSelection.LANGUAGE_NAME, "FRENCH");
        values1.put(LanguageSelection.LANGUAGE_HEX, "002395");
        values1.put(LanguageSelection.LANGUAGE_IMAGE_URL, url1);

        ContentValues values2 = new ContentValues();
        values2.put(LanguageSelection.LANGUAGE_ID, 6);
        values2.put(LanguageSelection.LANGUAGE_NAME, "ITALIAN");
        values2.put(LanguageSelection.LANGUAGE_HEX, "009246");
        values2.put(LanguageSelection.LANGUAGE_IMAGE_URL, url2);

        ContentValues values3 = new ContentValues();
        values3.put(LanguageSelection.LANGUAGE_ID, 7);
        values3.put(LanguageSelection.LANGUAGE_NAME, "RUSSIAN");
        values3.put(LanguageSelection.LANGUAGE_HEX, "D52B1E");
        values3.put(LanguageSelection.LANGUAGE_IMAGE_URL, url3);
        // </TestingValues>

        context.getContentResolver().insert(InternalDbContract.insertLanguagesUri(), values1);
        context.getContentResolver().insert(InternalDbContract.insertLanguagesUri(), values2);
        context.getContentResolver().insert(InternalDbContract.insertLanguagesUri(), values3);

        ImageDownloadHelper.getBitmapFromUrl(url1, context, true);
        ImageDownloadHelper.getBitmapFromUrl(url2, context, true);
        ImageDownloadHelper.getBitmapFromUrl(url3, context, true);
    }
}
