package com.ifeomai.apps.bakingapp.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeApi {
    private static RecipeService recipesService;
    private static final Object LOCK = new Object();
    public static final String PROJECT_BAKING_RECIPES_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public synchronized static RecipeService getInstance() {
        if (recipesService == null) {
            synchronized (LOCK) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(PROJECT_BAKING_RECIPES_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                recipesService = retrofit.create(RecipeService.class);
            }
        }
        return recipesService;
    }
}