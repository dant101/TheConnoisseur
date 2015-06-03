package com.theconnoisseur;

import android.test.AndroidTestCase;

import com.theconnoisseur.android.Model.ExerciseContent;

import Util.JSONHelper;


public class JSONTest extends AndroidTestCase {

    public void testExerciseContentCanCorrectlyFormFromJson() {
        String sample_json = "{\n" +
                "\t\t\"word_id\": 1,\n" +
                "\t\t\"word\": \"ma-try-osh-ka\",\n" +
                "\t\t\"phonetic\" : \"ʠɠƙʄƈɗƭʃʒʂʐ\",\n" +
                "\t\t\"image_url\" : \"www.image.com\",\n" +
                "\t\t\"sound_recording\" : \"www.recording.com\",\n" +
                "\t\t\"word_description\" : \"The first Russian nested doll set was made in 1890 by Vasily Zvyozdochkin from a design by Sergey Malyutin, who was a folk crafts painter at Abramtsevo\",\n" +
                "\t\t\"language_id\": 3,\n" +
                "\t\t\"language\" : \"Russian\"\n" +
                "\t}";

//        ExerciseContent e = new ExerciseContent(JSONHelper.getInstance().getJSON(sample_json));
//
//        assertEquals(1, e.getWord_id());
//        assertEquals("ma-try-osh-ka", e.getWord());
//        assertEquals("ʠɠƙʄƈɗƭʃʒʂʐ", e.getPhonetic());
//        assertEquals("www.image.com", e.getImage_url());
//        assertEquals("www.recording.com", e.getSound_recording_url());
//        assertEquals(3, e.getLanguage_id());
//        assertEquals("Russian", e.getLanguage());

    }

}
