package com.example.android.bakingtime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;

/**
 * Helper methods used by MainActivity, MasterListActivity and DetailActivity
 */

class SharedHelper {

    /**
     * Helper method to determine whether there is an Internet connection.
     *
     * @return True if there is an Internet connection.
     */
    static boolean hasInternetConnection(Context context) {
        boolean connectedToInternet = false;
        //Get a reference to the Connectivity Manager to check the state of network connectivity.
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network.
        if(connectivityManager != null){
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            connectedToInternet = activeNetwork != null && activeNetwork.isConnected();
        }
        return connectedToInternet;
    }

    /**
     * Helper method to create a new detail fragment for the specified recipe step position.
     *
     * @param recipeStepToDisplay   The number of the recipe step to display.
     */
    static void createDetailFragment(int recipeStepToDisplay, FragmentManager fragmentManager,
                                     int isFirstFragment){
        DetailFragment newDetailFragment = new DetailFragment();
        newDetailFragment.setRecipeStep(MasterListActivity.mRecipeSteps.get(recipeStepToDisplay));
        switch(isFirstFragment){
            case(1):
                fragmentManager.beginTransaction()
                        .add(R.id.detail_fragment_layout, newDetailFragment)
                        .commit();
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.detail_fragment_layout, newDetailFragment)
                        .commit();
        }
    }

}
