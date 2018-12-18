package com.ifeomai.apps.bakingapp.ui.recipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;

import com.ifeomai.apps.bakingapp.data.model.Recipe;
import com.ifeomai.apps.bakingapp.data.remote.RecipeRepo;
import com.ifeomai.apps.bakingapp.data.remote.SimpleIdlingResource;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements RecipeRepo.DelayCallback {
    private MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
    @Nullable
    private SimpleIdlingResource simpleIdlingResource;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        getIdlingResource();
        fetchRecipes();
    }

    @VisibleForTesting
    public IdlingResource getIdlingResource() {
        if (null == simpleIdlingResource) {
            simpleIdlingResource = new SimpleIdlingResource();
        }

        return simpleIdlingResource;
    }

    public LiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    private void fetchRecipes() {
        RecipeRepo recipeRepo = new RecipeRepo();
        recipeRepo.loadRecipeList(this, simpleIdlingResource);
    }

    @Override
    public void onDone(List<Recipe> recipes) {
        recipesLiveData.setValue(recipes);
    }
}

