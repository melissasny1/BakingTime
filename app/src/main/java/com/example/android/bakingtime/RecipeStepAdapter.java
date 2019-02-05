package com.example.android.bakingtime;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create and populate view holders for the Recipe RecyclerView.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter
        <RecipeStepAdapter.RecipeStepViewHolder> {

    private final Context mContext;
    private final MasterListFragment.OnRecipeStepClickListener mRecipeStepClickListener;
    //Variable for the selected recipe's list of recipe steps passed into the adapter.
    private final List<RecipeStep> mRecipeSteps;
    //Variable for the position of the recipe step clicked by the user.
    private int mClickedRecipeStepPosition;
    //Variable which is true if the layout is two-pane.
    private final boolean mLayoutIsTwoPane;

    RecipeStepAdapter(Context context, MasterListFragment.OnRecipeStepClickListener
            onRecipeStepClickListener, List<RecipeStep> recipeSteps,
                      boolean layoutIsTwoPane, int clickedRecipeStepPosition ){
        mContext = context;
        mRecipeStepClickListener = onRecipeStepClickListener;
        mRecipeSteps = recipeSteps;
        mLayoutIsTwoPane = layoutIsTwoPane;
        mClickedRecipeStepPosition = clickedRecipeStepPosition;
    }

    @Override
    public RecipeStepViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                                     int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recipe_step_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeStepAdapter.RecipeStepViewHolder holder,
                                 int position) {
        //Retrieve the RecipeStep Object for the current position.
        RecipeStep currentRecipeStep = mRecipeSteps.get(position);
        String currentStepShortDesc = currentRecipeStep.getShortDescription();
        holder.mRecipeStepTextView.setText(currentStepShortDesc);

        holder.mRecipeStepLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickedRecipeStepPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
                //Invoke the onClickListener of the Adapter class.
                mRecipeStepClickListener.onRecipeStepClicked(mClickedRecipeStepPosition);
            }
        });

        //If the layout is two-pane, highlights only the clicked recipe step position
        //by changing the background and text colors.
        if(mLayoutIsTwoPane){
            if(mClickedRecipeStepPosition == holder.getAdapterPosition()){
                setColorsForClickedItem(holder);
            } else{
               setColorsForUnclickedItem(holder);
            }
        }
    }

    @Override
    public int getItemCount() {
        int numberOfItems = 0;
        if(mRecipeSteps != null && mRecipeSteps.size() > 0) {
            numberOfItems = mRecipeSteps.size();
        }
        return numberOfItems;
    }

    class RecipeStepViewHolder extends RecyclerView.ViewHolder {
        //Create a member variable for each view in the item view
        @BindView(R.id.recipe_step_textview) TextView mRecipeStepTextView;
        @BindView(R.id.recipe_step_linear_layout) LinearLayout mRecipeStepLinearLayout;
        @BindView(R.id.recipe_step_play_arrow) ImageView mRecipeStepImageView;

        RecipeStepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Helper method to set the text and background colors for the recipe step the user clicked.
     *
     * @param holder        The RecipeStepViewHolder for the clicked recipe step.
     */
    private void setColorsForClickedItem(RecipeStepViewHolder holder){
        holder.mRecipeStepLinearLayout.setBackgroundColor(Color
                .parseColor("#"+Integer.toHexString(ContextCompat
                        .getColor(mContext,
                                R.color.touchSelector))));
        holder.mRecipeStepTextView.setBackgroundColor(Color
                .parseColor("#"+Integer.toHexString(ContextCompat
                        .getColor(mContext,
                                R.color.touchSelector))));
        holder.mRecipeStepImageView.setBackgroundColor(Color
                .parseColor("#"+Integer.toHexString(ContextCompat
                        .getColor(mContext,
                                R.color.touchSelector))));
        holder.mRecipeStepTextView.setTextColor(Color.BLACK);
    }

    /**
     * Helper method to set the text and background colors for the unclicked recipe steps.
     *
     * @param holder        The RecipeStepViewHolder for the unclicked recipe steps.
     */
    private void setColorsForUnclickedItem(RecipeStepViewHolder holder){
        holder.mRecipeStepLinearLayout.setBackgroundColor(Color.WHITE);
        holder.mRecipeStepTextView.setBackgroundColor(Color
                .parseColor("#"+Integer.toHexString(ContextCompat
                        .getColor(mContext,
                                R.color.lightBackground))));
        holder.mRecipeStepImageView.setBackgroundColor(Color
                .parseColor("#"+Integer.toHexString(ContextCompat
                        .getColor(mContext,
                                R.color.lightBackground))));
        holder.mRecipeStepTextView.setTextColor(Color.BLACK);
    }
}
