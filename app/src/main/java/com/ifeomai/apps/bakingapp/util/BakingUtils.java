package com.ifeomai.apps.bakingapp.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ifeomai.apps.bakingapp.data.model.Ingredient;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class BakingUtils {

    /**
     * Convert ingredient string to the list of ingredients
     *
     * Reference: @see "https://stackoverflow.com/questions/44580702/android-room-persistent-library
     * -how-to-insert-class-that-has-a-list-object-fie"
     * "https://medium.com/@toddcookevt/android-room-storing-lists-of-objects-766cca57e3f9"
     */
    public static List<Ingredient> toIngredientList(String ingredientString) {
        if (ingredientString == null) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Ingredient>>() {}.getType();
        return gson.fromJson(ingredientString, listType);
    }

    /**
     * Convert the list of ingredients to the String.
     */
    public static String toIngredientString(List<Ingredient> ingredientList) {
        if (ingredientList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Ingredient>>() {}.getType();
        return gson.toJson(ingredientList, listType);
    }

}
