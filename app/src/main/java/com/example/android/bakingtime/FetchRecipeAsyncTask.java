package com.example.android.bakingtime;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Create a custom AsyncTask to get the recipe data on a background thread
 * and load the resulting list of Recipe objects into the RecipeAdapter to be displayed in the
 * RecyclerView.
 */

class FetchRecipeAsyncTask extends AsyncTask<String, Void, List<Recipe>> {

    private final AsyncTaskCompleteListener<List<Recipe>> mListener;
    private final SimpleIdlingResource mIdlingResource;

    FetchRecipeAsyncTask(AsyncTaskCompleteListener<List<Recipe>> listener,
                         @Nullable final SimpleIdlingResource idlingResource) {

        mListener = listener;
        mIdlingResource = idlingResource;

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
    }

    @Override
    protected List<Recipe> doInBackground(String...recipesStringUrls) {
        if (recipesStringUrls.length == 0) {
            return null;
        }
        String recipesStringUrl = recipesStringUrls[0];
        return QueryUtils.fetchRecipes(recipesStringUrl);
    }

    @Override
    protected void onPostExecute(List<Recipe> recipes) {
        super.onPostExecute(recipes);
        mListener.onTaskComplete(recipes);
        if(mIdlingResource != null){
            mIdlingResource.setIdleState(true);
        }
    }
}
