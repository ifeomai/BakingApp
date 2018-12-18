package com.ifeomai.apps.bakingapp.ui.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

  /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}*/

  private final String TAG = "MainActivity";
    private MainActivityAdapter recipesAdapter;

    @BindView(R.id.recipes_list_recyclerview)
    RecyclerView recipesRecyclerView;

    @BindView(R.id.recipe_list_progress_bar)
    ProgressBar recipeListProgressBar;

    private MainActivityViewModel recipesListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        setupViewModel();
    }

    private void initUI() {
        ButterKnife.bind(this);

        recipesRecyclerView.setLayoutManager(
                new GridLayoutManager(this, getResources().getInteger(R.integer.recipe_list_columns)));

        recipesAdapter = new MainActivityAdapter(this);
        recipesRecyclerView.setAdapter(recipesAdapter);
    }

    private void setupViewModel() {
        recipesListViewModel = ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);

        showProgressBar();
        recipesListViewModel.getRecipesLiveData().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(@Nullable List<Recipe> recipes) {
                            hideProgressBar();
                            Log.d( TAG, "Received Recipes in Activity");
                            recipesAdapter.setRecipes(recipes);
                            recipesRecyclerView.setAdapter(recipesAdapter);
                    }
                }
        );
    }

    public MainActivityViewModel getRecipesListViewModel() {
        return recipesListViewModel;
    }

    private void showProgressBar() {
        recipeListProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        recipeListProgressBar.setVisibility(View.GONE);
    }
}
