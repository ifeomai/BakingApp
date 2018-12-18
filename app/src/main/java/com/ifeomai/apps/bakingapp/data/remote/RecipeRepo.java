package com.ifeomai.apps.bakingapp.data.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ifeomai.apps.bakingapp.data.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRepo {
    private final String TAG = "Recipe Repo";

    public void loadRecipeList(final DelayCallback delayCallback, final SimpleIdlingResource idlingResource) {
        RecipeService recipesService = RecipeApi.getInstance();

        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        Call<List<Recipe>> recipesCall = recipesService.getRecipes();
        recipesCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                List<Recipe> recipes = response.body();
                Log.d(TAG,String.format("Recipes: %s", recipes.toString()));
                if (delayCallback != null) {
                    delayCallback.onDone(recipes);
                    if (idlingResource != null) {
                        idlingResource.setIdleState(true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.e(TAG, "Problem fetching recipes");
            }
        });
    }
    public interface DelayCallback {
        void onDone(List<Recipe> recipes);
    }
}
