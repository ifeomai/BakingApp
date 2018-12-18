package com.ifeomai.apps.bakingapp.data;

import com.ifeomai.apps.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeService {
    @GET("baking.json")
    Call<List<Recipe>> getRecipes();

}
