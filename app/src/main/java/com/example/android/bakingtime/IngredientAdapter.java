package com.example.android.bakingtime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create and populate view holders for the Recipe RecyclerView.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>{

    private final List<String> mIngredients;

    IngredientAdapter(List<String> ingredients){
        mIngredients = ingredients;
    }

    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.ingredient_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientAdapter.IngredientViewHolder holder, int position) {
        //Retrieve the RecipeStep Object for the current position.
        String currentIngredient = mIngredients.get(position);
        holder.mIngredientNameTextView.setText(currentIngredient);
    }

    @Override
    public int getItemCount() {
        int numberOfItems = 0;
        if(mIngredients != null && mIngredients.size() > 0) {
            numberOfItems = mIngredients.size();
        }
        return numberOfItems;
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder{
        //Create a member variable for each view in the item view.
        @BindView(R.id.ingredient_name_textview)
        TextView mIngredientNameTextView;

        IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
