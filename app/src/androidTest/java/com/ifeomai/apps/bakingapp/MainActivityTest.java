package com.ifeomai.apps.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ifeomai.apps.bakingapp.ui.recipe.MainActivity;

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

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        idlingResource = mActivityTestRule.getActivity().getRecipesListViewModel().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    public void showRecipeFirstItemTest() {
        onView(withId(R.id.recipes_list_recyclerview))
                .perform(RecyclerViewActions.scrollToPosition(0))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickFirstRecipe_OpenRecipeDetails() {
        onView(withId(R.id.recipes_list_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.ingredients_label_text_view))
                .check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}

