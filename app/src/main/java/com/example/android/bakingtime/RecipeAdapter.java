package com.example.android.bakingtime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Create and populate view holders for the Recipe RecyclerView.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final RecipeClickListener mRecipeClickListener;
    private List<Recipe> mRecipes;
    private final Context mContext;

    RecipeAdapter (Context context, RecipeClickListener recipeClickListener){
        mContext = context;
        mRecipeClickListener = recipeClickListener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        //Retrieve the Recipe Object for the current position.
        Recipe currentRecipe = mRecipes.get(position);
        String currentRecipeName = currentRecipe.getRecipeName();
        String currentRecipeImageUrl = currentRecipe.getImageUrl();
        if(SharedHelper.hasInternetConnection(mContext) &&currentRecipeImageUrl != null
                && !currentRecipeImageUrl.isEmpty()){
            Picasso.get().load(currentRecipeImageUrl)
                    .error(R.drawable.baked_goods)
                    .into(holder.mRecipeImageView);
            holder.mRecipeImageView.setVisibility(View.VISIBLE);
        }
        holder.mRecipeNameTextView.setText(currentRecipeName);
    }

    @Override
    public int getItemCount() {
        int numberOfItems = 0;
        if(mRecipes != null && mRecipes.size() > 0){
            numberOfItems = mRecipes.size();
        }
        return numberOfItems;
    }

    /**
     * Set the recipe data on a RecipeAdapter if one has already been created.
     *
     * @param recipes The new recipe data to be displayed.
     */
    void setRecipeData(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    //Create an interface to define the listener.
    interface RecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //Create a member variable for each view in the item view.
        @BindView(R.id.recipe_name_textview) TextView mRecipeNameTextView;
        @BindView(R.id.recipe_image_view) ImageView mRecipeImageView;

        RecipeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            //Get the position of the recipe clicked.
            int clickedPosition = getAdapterPosition();
            Recipe clickedRecipe = mRecipes.get(clickedPosition);
            //Invoke the onClickListener of the Adapter class.
            mRecipeClickListener.onRecipeClick(clickedRecipe);
        }
    }
}
