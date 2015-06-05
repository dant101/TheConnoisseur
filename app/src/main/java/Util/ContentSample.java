package Util;

import android.content.ContentValues;
import android.content.Context;

import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;


//Useful class for trying exercises if you haven't got an internet connection or the server is down.
//Can be called from any activity - preferably MainMenuActivity. This way, data will appear when you go to LanguageSelectionActivity
public class ContentSample {

    public static void insertSampleItalian(Context context) {

        ContentValues values = new ContentValues();

        values.put(LanguageSelection.LANGUAGE_ID, 3);
        values.put(LanguageSelection.LANGUAGE_NAME, "Italian");
        values.put(LanguageSelection.LANGUAGE_HEX, "009246");
        values.put(LanguageSelection.LANGUAGE_IMAGE_URL, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/flags/3-italian.png");

        context.getContentResolver().insert(InternalDbContract.insertLanguagesUri(), values);

        ContentDownloadHelper.getBitmapFromUrl("http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/flags/3-italian.png", context, true);

        ContentValues c1 = new ContentValues();

        c1.put(ExerciseContent.WORD_ID, 4);
        c1.put(ExerciseContent.WORD, "stradivari violoncello");
        c1.put(ExerciseContent.PHONETIC, "stradivari vjolontʃello");
        c1.put(ExerciseContent.IMAGE_URL, "");
        c1.put(ExerciseContent.SOUND_RECORDING, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/sounds/exercises/italian/stradivari_violoncello.mp3");
        c1.put(ExerciseContent.WORD_DESCRIPTION, "After the string instrument crafter Antoinio Stradivary, the most significant and greatest artisan in the field.");
        c1.put(ExerciseContent.LANGUAGE_ID, 3);
        c1.put(ExerciseContent.LANGUAGE, "Italian");
        c1.put(ExerciseContent.LOCALE, "it");

        context.getContentResolver().insert(InternalDbContract.insertExercisesUri(), c1);

        ContentDownloadHelper.getBitmapFromUrl("", context, true);
        ContentDownloadHelper.saveSoundFile(context, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/sounds/exercises/italian/stradivari_violoncello.mp3");


        ContentValues c2 = new ContentValues();

        c2.put(ExerciseContent.WORD_ID, 5);
        c2.put(ExerciseContent.WORD, "cornetto");
        c2.put(ExerciseContent.PHONETIC, "kornetto");
        c2.put(ExerciseContent.IMAGE_URL, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/exercises/italian/cornetto.png");
        c2.put(ExerciseContent.SOUND_RECORDING, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/sounds/exercises/italian/cornetto.mp3");
        c2.put(ExerciseContent.WORD_DESCRIPTION, "In 1959, that spican, an italian ice cream manufacturer overcame the problem of soggy waffles by insulating the inside of the waffle cone from the ice cream with a coating of oil, sugar and chocolate.");
        c2.put(ExerciseContent.LANGUAGE_ID, 3);
        c2.put(ExerciseContent.LANGUAGE, "Italian");
        c2.put(ExerciseContent.LOCALE, "it");

        context.getContentResolver().insert(InternalDbContract.insertExercisesUri(), c2);

        ContentDownloadHelper.getBitmapFromUrl("http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/exercises/italian/cornetto.png", context, true);
        ContentDownloadHelper.saveSoundFile(context, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/sounds/exercises/italian/cornetto.mp3");


        ContentValues c3 = new ContentValues();

        c3.put(ExerciseContent.WORD_ID, 6);
        c3.put(ExerciseContent.WORD, "Maestro");
        c3.put(ExerciseContent.PHONETIC, "maˈɛstro");
        c3.put(ExerciseContent.IMAGE_URL, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/sounds/exercises/italian/maestro.mp3");
        c3.put(ExerciseContent.WORD_DESCRIPTION, "The master or teacher in an artistic field.");
        c3.put(ExerciseContent.LANGUAGE_ID, 3);
        c3.put(ExerciseContent.LANGUAGE, "Italian");
        c3.put(ExerciseContent.LOCALE, "it");

        context.getContentResolver().insert(InternalDbContract.insertExercisesUri(), c3);

        ContentDownloadHelper.getBitmapFromUrl("", context, true);
        ContentDownloadHelper.saveSoundFile(context, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/sounds/exercises/italian/maestro.mp3");


        ContentValues c4 = new ContentValues();

        c4.put(ExerciseContent.WORD_ID, 7);
        c4.put(ExerciseContent.WORD, "Piazza");
        c4.put(ExerciseContent.PHONETIC, "pjattsa");
        c4.put(ExerciseContent.IMAGE_URL, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/exercises/italian/piazza.png");
        c4.put(ExerciseContent.SOUND_RECORDING, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/sounds/exercises/italian/piazza.mp3");
        c4.put(ExerciseContent.WORD_DESCRIPTION, "A piazza is most commonly found at the meeting of two or more streets. Most italian streets have several piazzas with streets radiating from the centre.");
        c4.put(ExerciseContent.LANGUAGE_ID, 3);
        c4.put(ExerciseContent.LANGUAGE, "Italian");
        c4.put(ExerciseContent.LOCALE, "it");

        context.getContentResolver().insert(InternalDbContract.insertExercisesUri(), c4);

        ContentDownloadHelper.getBitmapFromUrl("http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/exercises/italian/piazza.png", context, true);
        ContentDownloadHelper.saveSoundFile(context, "http://www.doc.ic.ac.uk/project/2014/271/g1427115/sounds/exercises/italian/piazza.mp3");
    }
}
