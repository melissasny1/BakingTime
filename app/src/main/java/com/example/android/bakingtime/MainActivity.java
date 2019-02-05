package com.example.android.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    static final String LOG_TAG = MainActivity.class.getSimpleName();
    static final String INTENT_EXTRA_KEY = "Recipe";
    private static final int GRID_COLUMN_WIDTH_DP = 300;
    private static final String RECIPE_LIST_KEY = "recipes";
    private static final String SAVED_STATE_KEY = "saved state";

    @BindView(R.id.rv_recipes) RecyclerView mRecipeRecyclerView;
    @BindView(R.id.error_message_view) CardView mErrorMessageView;
    @BindView(R.id.error_message_textview) TextView mErrorMessageText;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    private RecipeAdapter mRecipeAdapter;
    private List<Recipe> mRecipes;
    //Variable for the ButterKnife unbinder.
    private Unbinder unbinder;
    private boolean mNoSavedState = true;

    @Nullable
    private
    SimpleIdlingResource mIdlingResource;

    //Test-only method that instantiates a new instance of SimpleIdlingResource if
    //IdlingResource is null.
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource(){
        if(mIdlingResource == null){
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        //Create a new RecipeAdapter.
        mRecipeAdapter = new RecipeAdapter(this, this);
        //Create a new GridLayoutManager and use the RecyclerView reference
        //to assign the layout manager.
        GridLayoutManager layoutManager = new GridLayoutManager(this, calcNumberOfGridCols());
        mRecipeRecyclerView.setLayoutManager(layoutManager);
        mRecipeRecyclerView.setHasFixedSize(true);
        //Connect the RecipeAdapter to the RecipeRecyclerView.
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);

        mIdlingResource = (SimpleIdlingResource) getIdlingResource();

        if(savedInstanceState != null && savedInstanceState.containsKey(RECIPE_LIST_KEY)){
            mRecipes = savedInstanceState.getParcelableArrayList(RECIPE_LIST_KEY);
            mRecipeAdapter.setRecipeData(mRecipes);
            mNoSavedState = savedInstanceState.getBoolean(SAVED_STATE_KEY);
        }
    }

    //Call displayRecipes from onStart for testing purposes, to ensure that the IdlingResource
    //has been registered first.
    @Override
    protected void onStart() {
        super.onStart();
        if(mNoSavedState){
            displayRecipes();
            mNoSavedState = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //If there are recipes displayed, save them.
        if(mRecipes != null && mRecipes.size() > 0){
            outState.putParcelableArrayList(RECIPE_LIST_KEY, new ArrayList<>(mRecipes));
        }
        outState.putBoolean(SAVED_STATE_KEY, mNoSavedState);
    }

    /**
     * Helper method that checks the internet connection, fetches and displays recipes
     * if there is a connection and displays an error message if there is no connection.
     *
     */
    private void displayRecipes(){
        if (SharedHelper.hasInternetConnection(this)) {
            //Display the loading indicator while the data request to the recipes url is executed.
            mLoadingIndicator.setVisibility(View.VISIBLE);
            new FetchRecipeAsyncTask(new FetchRecipeTaskCompleteListener(), mIdlingResource)
                    .execute(QueryUtils.RECIPES_URL);
        } else {
            mRecipeRecyclerView.setVisibility(View.GONE);
            mErrorMessageText.setText(R.string.error_msg_text_no_internet);
            mErrorMessageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Create an intent to start the MasterListActivity, pass the parcel containing the
     * clicked recipe data to MasterListActivity with the intent, and start the activity.
     *
     * @param clickedRecipe The Recipe object for the recipe clicked by the user.
     */
    @Override
    public void onRecipeClick(Recipe clickedRecipe) {
        //Save the clicked recipe name and ingredients in shared preferences.
        setSharedPreferences(clickedRecipe.getRecipeName(), clickedRecipe.getIngredients());
        //Create and start an intent to pass the clicked recipe to the Detail Activity.
        Intent intent = new Intent(MainActivity.this, MasterListActivity.class);
        intent.putExtra(INTENT_EXTRA_KEY, clickedRecipe);
        startActivity(intent);
        //Update the widget to display the ingredients for the clicked recipe.
        IngredientUpdateService.startActionUpdateIngredients(this);
    }

    class FetchRecipeTaskCompleteListener implements AsyncTaskCompleteListener<List<Recipe>>{
        @Override
        public void onTaskComplete(List<Recipe> recipes) {
            //Hide the loading indicator when the data request for recipes is complete.
            mLoadingIndicator.setVisibility(View.GONE);

            //If recipes are returned, set the new recipe data on the recipe adapter, to
            //display the recipes.
            if (recipes != null) {
                mRecipes = recipes;
                mErrorMessageView.setVisibility(View.GONE);
                mRecipeRecyclerView.setVisibility(View.VISIBLE);
                mRecipeAdapter.setRecipeData(mRecipes);
            }
            //If no recipes are returned, display an error message to that effect.
            else{
                mRecipeRecyclerView.setVisibility(View.GONE);
                mErrorMessageText.setText(R.string.info_msg_text_no_recipes);
                mErrorMessageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     * Helper method to set shared preferences for the recipe name and ingredient list for the
     * clicked recipe, to be displayed in the app widget.
     *
     * @param recipeName    The recipe name for the clicked recipe.
     * @param ingredients   The ingredient list for the clicked recipe.
     */
    private void setSharedPreferences(String recipeName, List<String> ingredients){
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //Clear existing shared preferences
        if(sharedPref.contains(getString(R.string.preference_recipe_name_key))
                && sharedPref.contains(getString(R.string.preference_ingredients_key))){
            editor.clear();
        }

        //Add the clicked recipe name to shared preferences
        editor.putString(getString(R.string.preference_recipe_name_key), recipeName);

        //Use StringBuilder to create a single string containing all of the ingredients,
        //separated by semicolons.
        StringBuilder sb = new StringBuilder();
        for(String ingredient : ingredients){
            sb.append(ingredient);
            sb.append(";");
        }

        editor.putString(getString(R.string.preference_ingredients_key), sb.toString());

        //Save the shared preferences, writing the updates to disk asynchronously.
        editor.apply();
    }

    /**
     * Helper method to calculate the number of grid columns to display based upon device screen
     * width.
     *
     * @return      The number of grid columns to display.
     */
    private int calcNumberOfGridCols(){
        Configuration config = getResources().getConfiguration();
        int width = config.screenWidthDp;
        return width/GRID_COLUMN_WIDTH_DP;
    }
}
