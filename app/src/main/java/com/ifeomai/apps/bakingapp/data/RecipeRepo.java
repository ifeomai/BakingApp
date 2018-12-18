package com.ifeomai.apps.bakingapp.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ifeomai.apps.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRepo {
    private final String TAG = "Recipe Repo";

     public void loadRecipeList() {
         RecipeService recipeService = RecipeApi.getInstance();

         Call<List<Recipe>> recipesCall = recipeService.getRecipes();
         recipesCall.enqueue(new Callback<List<Recipe>>() {
             @Override
             public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                 List<Recipe> recipes = response.body();
                 Log.d("Recipes: %s", recipes.toString());
             }

             @Override
             public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                 Log.e(TAG, "Problem fetching recipes");
             }
         });
     }
}
