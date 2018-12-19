package com.ifeomai.apps.bakingapp;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ifeomai.apps.bakingapp.data.model.Ingredient;
import com.ifeomai.apps.bakingapp.data.model.Recipe;
import com.ifeomai.apps.bakingapp.data.model.Step;
import com.ifeomai.apps.bakingapp.ui.detail.DetailActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    private Recipe testRecipe;

    @Rule
    public ActivityTestRule<DetailActivity> mActivityTestRule =
            new ActivityTestRule<DetailActivity>(DetailActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();

                    testRecipe = getTestRecipe();
                    intent.putExtra(DetailActivity.EXTRA_RECIPE, testRecipe);

                    return intent;
                }
            };

    private Recipe getTestRecipe() {
        ArrayList<Step> steps = new ArrayList<>();
        steps.add(new Step(
                0,
                "test short desc",
                "test long desc",
                "test vid url",
                "test thumbnail url"
        ));
        steps.add(new Step(
                1,
                "test short desc",
                "test long desc",
                "test vid url",
                "test thumbnail url"
        ));

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(
                "Test ingredient",
                1,
                "KG"
        ));
        ingredients.add(new Ingredient(
                "Test ingredient 2",
                2,
                "G"
        ));

        return new Recipe(
                0,
                "Test Recipe",
                ingredients,
                steps,
                10,
                null
        );
    }

    @Test
    public void CheckIfIngredientsAndStepsShown() {
        onView(withId(R.id.ingredients_label_text_view))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ingredients_recycler_view))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ingredients_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(0))
                .check(matches(isDisplayed()));

        onView(withId(R.id.recipe_detail_steps_label_text_view))
                .check(matches(isDisplayed()));
        onView(withId(R.id.steps_recycler_view))
                .check(matches(isDisplayed()));
        onView(withId(R.id.steps_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(0))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickOnStep_OpenStepDetail() {
        onView(withId(R.id.steps_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.step_instruction_text_view))
                .check(matches(withText(testRecipe.getSteps().get(0).getDescription())));
    }
}

