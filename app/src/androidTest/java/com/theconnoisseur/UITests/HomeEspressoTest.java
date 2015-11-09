package com.theconnoisseur.UITests;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.MainMenuActivity;

import org.junit.Before;

import static android.support.test.espresso.Espresso.onView;

/**
 * Created by Tomasz on 09/11/15.
 */

//TODO: resolve dependency issues!

public class HomeEspressoTest extends ActivityInstrumentationTestCase2 {

    private MainMenuActivity mActivity;

    public HomeEspressoTest() {
        super(MainMenuActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = (MainMenuActivity) getActivity();
    }

//    public void testLanguagesNavigation() {
//        //Press layout
//        onView(withId(R.id.language_selection))
//                .perform(ViewActions.click())
//    }
}
