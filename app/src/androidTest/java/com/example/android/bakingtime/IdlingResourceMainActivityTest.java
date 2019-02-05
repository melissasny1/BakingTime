package com.example.android.bakingtime;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 *This test demos a user clicking on a recipe and verifies that it opens the
 * master list view.
 */

@RunWith(AndroidJUnit4.class)
public class IdlingResourceMainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    //Register IdlingResource before running tests.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void displayRecipeRecyclerView(){
        //Confirm that the recyclerview is displayed and the loading
        //indicator and error messages are not.
        onView(withId(R.id.rv_recipes)).check(matches(isDisplayed()));
        //onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        //onView(withId(R.id.textLoadingError)).check(matches(not(isDisplayed())));
    }

    @Test
    public void clickRecipeName_DisplaysMasterFragment() {
        //Click on the first recipe recyclerview item.
        onView((withId(R.id.rv_recipes))).perform(RecyclerViewActions
                .actionOnItemAtPosition(0,click()));

        //Verify that the master list fragment is displayed.
        onView((withId(R.id.master_list_fragment))).check(matches(isDisplayed()));
    }

    //Unregister resources when not needed
    @After
    public void unregisterIdlingResource() {
        if(mIdlingResource != null){
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}

