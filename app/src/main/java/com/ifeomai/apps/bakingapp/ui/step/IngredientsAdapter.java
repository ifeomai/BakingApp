package com.ifeomai.apps.bakingapp.ui.step;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Ingredient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    private List<Ingredient> ingredients;
    private Context context;

    IngredientsAdapter(@NonNull Context context, @NonNull List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.ingredient_item, viewGroup, false);

        return new IngredientsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder ingredientsViewHolder, int position) {
        ingredientsViewHolder.bind(ingredients.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_name_text_view)
        TextView ingredientsNameTextView;

        @BindView(R.id.ingredient_quantity_text_view)
        TextView ingredientsQuantityTextView;

        IngredientsViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(@NonNull Ingredient ingredient, int ingredientNumber) {
            ingredientsNameTextView.setText(context.getString(
                    R.string.ingredient_item_name,
                    ingredientNumber,
                    ingredient.getName()));
            ingredientsQuantityTextView.setText(context.getString(
                    R.string.ingredient_quantity,
                    String.valueOf(ingredient.getQuantity()),
                    ingredient.getMeasure()));
        }
    }
}
