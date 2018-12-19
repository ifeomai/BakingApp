package com.ifeomai.apps.bakingapp.ui.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Ingredient;
import com.ifeomai.apps.bakingapp.data.model.Recipe;
import com.ifeomai.apps.bakingapp.ui.recipe.MainActivity;
import com.ifeomai.apps.bakingapp.ui.recipe.MainActivityViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
public class BakingAppConfigActivity extends AppCompatActivity implements RecipeSelectionAdapter.RecipeSelectionListener {
    private RecipeSelectionAdapter recipeSelectionAdapter;
    private final String TAG = "ConfigActivity";

    @BindView(R.id.recipe_selection_progress_bar)
    ProgressBar recipeSelectionProgressBar;

    @BindView(R.id.recipes_selection_recyclerview)
    RecyclerView recipeSelectionRecyclerView;
    private int widgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baking_app_config);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            widgetId = extras.getInt(EXTRA_APPWIDGET_ID);
        }

        initUI();
        setupViewModel();
    }

    private void initUI() {
        ButterKnife.bind(this);

        recipeSelectionAdapter = new RecipeSelectionAdapter(this, this);

        recipeSelectionRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recipeSelectionRecyclerView.setAdapter(recipeSelectionAdapter);
    }

    private void setupViewModel() {
        MainActivityViewModel recipesListViewModel = ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);

        showProgressBar();
        recipesListViewModel.getRecipesLiveData().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(@Nullable List<Recipe> recipes) {
                        hideProgressBar();
                        Log.d( TAG, "Received Recipes in Activity");
                        recipeSelectionAdapter.setRecipes(recipes);
                        recipeSelectionRecyclerView.setAdapter(recipeSelectionAdapter);
                    }
                }
        );
    }

    private void showProgressBar() {
        recipeSelectionProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        recipeSelectionProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRecipeSelected(List<Ingredient> ingredientList) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.baking_widget_provider);
        StringBuilder ingredientsStringBuilder = new StringBuilder();
        for (Ingredient ingredient : ingredientList) {
            ingredientsStringBuilder.append(
                    String.format("- %s (%s %s)\n",
                            ingredient.getName().substring(0, 1).toUpperCase() +
                                    ingredient.getName().substring(1),
                            ingredient.getQuantity(),
                            ingredient.getMeasure())
            );
        }
        views.setTextViewText(R.id.appwidget_text, ingredientsStringBuilder.toString());

        AppWidgetManager.getInstance(this).updateAppWidget(widgetId, views);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, intent);

        finish();
    }
}

