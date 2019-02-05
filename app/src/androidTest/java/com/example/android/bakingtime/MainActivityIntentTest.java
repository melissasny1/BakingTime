package com.example.android.bakingtime;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Verify that clicking on a recipe name creates an intent that starts the MasterListActivity and
 * contains an Extra with the "Recipe" key.
 */

public class MainActivityIntentTest {

    private static final String INTENT_EXTRA_KEY = "Recipe";

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(
            MainActivity.class);


    @Before
    public void stubIntents() {
        Instrumentation.ActivityResult intentResult = new Instrumentation
                .ActivityResult(Activity.RESULT_OK, null);

        intending(anyIntent()).respondWith(intentResult);
    }

    @Test
    public void clickRecipeName_OpensMasterListActivityForClickedRecipe() {

        onView(withId(R.id.rv_recipes)).check(matches(isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        intended(allOf(
                hasComponent(MasterListActivity.class.getName()),
                hasExtraWithKey(INTENT_EXTRA_KEY)));
    }
}

