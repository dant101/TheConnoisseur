package com.theconnoisseur.ProviderTests;


import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.LanguageSelection;
import com.theconnoisseur.android.Provider.InternalDbProvider;

public class InternalDbProviderTest extends ProviderTestCase2<InternalDbProvider> {

    private MockContentResolver mMockResolver;

    public InternalDbProviderTest() {
        super(InternalDbProvider.class, InternalDbContract.CONTENT_AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMockResolver = getMockContentResolver();

    }

    public void testQueriesLanguages() {
        mMockResolver.query(InternalDbContract.queryForLanguages(), null, null, null, null);
    }

    public void testInsertsQueriesAndUpdatesDB() {
        Cursor c = mMockResolver.query(InternalDbContract.queryForLanguages(), null, null, null, null);
        int items = c.getCount();

        mMockResolver.insert(InternalDbContract.insertLanguagesUri(), getTestValues(1));
        mMockResolver.insert(InternalDbContract.insertLanguagesUri(), getTestValues(2));

        c = mMockResolver.query(InternalDbContract.queryForLanguages(), null, null, null, null);

        int new_items = c.getCount();

        c.close();
        assertEquals(items + 2, new_items);

        c = mMockResolver.query(InternalDbContract.queryForLanguages(1), null, null, null, null);
        assertEquals(1, c.getCount());

    }

    public void testCanRetrieveCorrectItems() {
        mMockResolver.insert(InternalDbContract.insertLanguagesUri(), getTestValues(3));
        mMockResolver.insert(InternalDbContract.insertLanguagesUri(), getTestValues(4));

        Cursor c = mMockResolver.query(InternalDbContract.queryForLanguages(), null, null, null, null);
        assertEquals(2, c.getCount());

        c.moveToFirst();
        assertEquals("3", c.getString(c.getColumnIndex(LanguageSelection.LANGUAGE_ID)));
        assertEquals("FRENCH", c.getString(c.getColumnIndex(LanguageSelection.LANGUAGE_NAME)));
        assertEquals("002395", c.getString(c.getColumnIndex(LanguageSelection.LANGUAGE_HEX)));
        assertEquals("http://www.see-and-do-france.com/images/French_flag_design.jpg", c.getString(c.getColumnIndex(LanguageSelection.LANGUAGE_IMAGE_URL)));

        c.close();
    }

    public void testCanInsertAndRetrieveExercises() {
        mMockResolver.insert(InternalDbContract.insertExercisesUri(), getExerciseTestValues());

        Cursor c = mMockResolver.query(InternalDbContract.queryForWords(23), null, null, null, null);
        assertEquals(1, c.getCount());

        c.moveToFirst();
        assertEquals(1, c.getInt(c.getColumnIndex(ExerciseContent.WORD_ID)));
        assertEquals("test", c.getString(c.getColumnIndex(ExerciseContent.WORD)));
        assertEquals("phonetic", c.getString(c.getColumnIndex(ExerciseContent.PHONETIC)));
        assertEquals("url", c.getString(c.getColumnIndex(ExerciseContent.IMAGE_URL)));
        assertEquals("url2", c.getString(c.getColumnIndex(ExerciseContent.SOUND_RECORDING)));
        assertEquals("description", c.getString(c.getColumnIndex(ExerciseContent.WORD_DESCRIPTION)));
        assertEquals(23, c.getInt(c.getColumnIndex(ExerciseContent.LANGUAGE_ID)));
        assertEquals("POLISH", c.getString(c.getColumnIndex(ExerciseContent.LANGUAGE)));

        c.close();
    }

    private ContentValues getTestValues(int id) {
        ContentValues values = new ContentValues();
        values.put(LanguageSelection.LANGUAGE_ID, id);
        values.put(LanguageSelection.LANGUAGE_NAME, "FRENCH");
        values.put(LanguageSelection.LANGUAGE_HEX, "002395");
        values.put(LanguageSelection.LANGUAGE_IMAGE_URL, "http://www.see-and-do-france.com/images/French_flag_design.jpg");

        return values;
    }

    private ContentValues getExerciseTestValues() {
        ContentValues values = new ContentValues();
        values.put(ExerciseContent.WORD_ID, 1);
        values.put(ExerciseContent.WORD, "test");
        values.put(ExerciseContent.PHONETIC, "phonetic");
        values.put(ExerciseContent.IMAGE_URL, "url");
        values.put(ExerciseContent.SOUND_RECORDING, "url2");
        values.put(ExerciseContent.WORD_DESCRIPTION, "description");
        values.put(ExerciseContent.LANGUAGE_ID, 23);
        values.put(ExerciseContent.LANGUAGE, "POLISH");

        return values;
    }

}
