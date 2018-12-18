package com.ifeomai.apps.bakingapp.ui.recipe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> {
    private Context context;
    private List<Recipe> recipes;

    MainActivityAdapter(Context context) {
        this.context = context;
    }

    void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainActivityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recipe_single, viewGroup, false);
        return new MainActivityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityViewHolder mainActivityViewHolder, int position) {
        mainActivityViewHolder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    class MainActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_item_name_text_view)
        TextView recipeNameTextView;
        @BindView(R.id.recipe_item_servings_text_view)
        TextView servingsTextView;
        @BindView(R.id.recipe_item_image_view)
        ImageView recipeItemImageView;

        MainActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        void bind(@NonNull Recipe recipe) {
            ButterKnife.bind(this, itemView);

            recipeNameTextView.setText(recipe.getName());
            servingsTextView.setText(context.getString(R.string.recipe_item_servings, recipe.getServings()));

            if (!TextUtils.isEmpty(recipe.getImage())) {
                String imageUrl = recipe.getImage();
                Picasso.get()
                        .load(imageUrl)
                        .error(R.drawable.ic_pots)
                        .into(recipeItemImageView);
            }
        }

        @Override
        public void onClick(View view) {
            Recipe recipe = recipes.get(getAdapterPosition());

        }
    }
}

