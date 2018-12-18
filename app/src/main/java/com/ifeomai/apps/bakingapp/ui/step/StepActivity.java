package com.ifeomai.apps.bakingapp.ui.step;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Step;
import com.ifeomai.apps.bakingapp.ui.detail.StepsList;

import java.util.List;

import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity {
    public static final String RECIPE_NAME_EXTRA = "recipe_name_extra";
    public static final String STEP_EXTRA = "step_extra";

    public static Intent newIntent(Context context, int stepPosition, String recipeName) {
        Intent intent = new Intent(context, StepActivity.class);
        intent.putExtra(StepActivity.STEP_EXTRA, stepPosition);
        intent.putExtra(StepActivity.RECIPE_NAME_EXTRA, recipeName);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(RECIPE_NAME_EXTRA) && intent.hasExtra(STEP_EXTRA)) {
            String recipeName = intent.getStringExtra(RECIPE_NAME_EXTRA);
            setTitle(recipeName);

            int stepPosition = intent.getIntExtra(STEP_EXTRA, 0);
            List<Step> steps = StepsList.getSteps();

            if (savedInstanceState == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment stepFragment = fragmentManager
                        .findFragmentById(R.id.activity_step_fragment_container);

                if (stepFragment == null) {
                    stepFragment = StepDetailFragment
                            .getInstance(steps.get(stepPosition), stepPosition);
                    fragmentManager.beginTransaction()
                            .add(R.id.activity_step_fragment_container, stepFragment)
                            .commit();
                }
            }
        }
    }


}
