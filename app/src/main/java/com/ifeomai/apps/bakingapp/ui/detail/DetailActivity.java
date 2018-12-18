package com.ifeomai.apps.bakingapp.ui.detail;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Recipe;
import com.ifeomai.apps.bakingapp.ui.step.StepDetailFragment;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE = "extra_recipe";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initUI();
    }

    private void initUI() {
        Intent intent = getIntent();
        if (intent.hasExtra(DetailActivity.EXTRA_RECIPE)) {
            Recipe recipe = intent.getParcelableExtra(DetailActivity.EXTRA_RECIPE);

            if (recipe != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                Fragment recipeDetailFragment = fragmentManager
                        .findFragmentById(R.id.details_container);

                // Create recipe fragment if it doesn't already exist
                if (recipeDetailFragment == null) {
                    fragmentManager.beginTransaction()
                            .add(R.id.details_container,
                                    DetailFragment.getInstance(recipe))
                            .commit();

                    setTitle(recipe.getName());
                }

                if (getResources().getBoolean(R.bool.is_tablet)) {
                    //DONE: add the fragment for tablet
                    Fragment stepFragment = fragmentManager.findFragmentById(R.id.details_container);

                    // Create step fragment if it doesn't already exist
                    if (stepFragment == null) {
                        fragmentManager.beginTransaction()
                                .add(R.id.details_container, StepDetailFragment.getInstance(recipe.getSteps().get(0), 0))
                                .commit();

                        setTitle(recipe.getName());
                    }
                }
            }
        }
    }
}
