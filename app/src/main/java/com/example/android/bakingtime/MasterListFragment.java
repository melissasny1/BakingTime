package com.example.android.bakingtime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// For the recipe clicked by the user, this fragment displays both a list of ingredients
// and a list of recipe steps.  Both lists are RecyclerViews.

public class MasterListFragment extends Fragment {

    @BindView(R.id.rv_ingredients) RecyclerView mIngredientRecyclerView;
    @BindView(R.id.rv_recipe_steps) RecyclerView mRecipeStepRecyclerView;

    //Define a new interface OnRecipeStepClickListener that triggers a callback in the host
    //activity.
    private OnRecipeStepClickListener mCallback;
    //Variable for the ButterKnife unbinder.
    private Unbinder unbinder;

    public MasterListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Inflate the Fragment layout.
        View rootView = inflater.inflate(R.layout.fragment_master_list, container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<String> ingredients = ((MasterListActivity) getActivity()).getIngredients();
        List<RecipeStep> recipeSteps = ((MasterListActivity) getActivity()).getRecipeSteps();
        boolean twoPane = ((MasterListActivity) getActivity()).getTwoPane();
        int clickedRecipeStepPosition = ((MasterListActivity) getActivity())
                .getClickedRecipeStepPosition();

         //Set up the Ingredients RecyclerView and Adapter.
         LinearLayoutManager ingredientLayoutManager= new LinearLayoutManager(getContext());
         mIngredientRecyclerView.setLayoutManager(ingredientLayoutManager);
         mIngredientRecyclerView.setHasFixedSize(true);
         IngredientAdapter ingredientAdapter = new IngredientAdapter(ingredients);
         mIngredientRecyclerView.setAdapter(ingredientAdapter);

         //Set up the Recipe Steps RecyclerView and Adapter.
         LinearLayoutManager recipeStepLayoutManager= new LinearLayoutManager(getContext());
         mRecipeStepRecyclerView.setLayoutManager(recipeStepLayoutManager);
         mRecipeStepRecyclerView.setHasFixedSize(true);
         RecipeStepAdapter recipeStepAdapter = new RecipeStepAdapter(getContext(), mCallback,
                 recipeSteps, twoPane, clickedRecipeStepPosition);
         mRecipeStepRecyclerView.setAdapter(recipeStepAdapter);
    }

    //OnRecipeStepClickListener interface, calls a method in the host activity named
    //onRecipeStepClicked.
    public interface OnRecipeStepClickListener{
        void onRecipeStepClicked(int recipeStepPosition);
    }

    // Override onAttach to make sure that the container activity has implemented the callback.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //Make sure that the host has implemented the OnRecipeStepClickListener callback activity.
        //If not, throw an exception.
        try{
            mCallback = (OnRecipeStepClickListener) context;
        } catch(ClassCastException e){
            throw new ClassCastException(context.toString() +
                    " must implement OnRecipeStepClickListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
