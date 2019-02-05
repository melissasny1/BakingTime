package com.example.android.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Service to populate the app widget ListView with recipe ingredients.
 */

public class ListViewWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewRemoteViewsFactory(this.getApplicationContext());
    }

    class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

        final Context mContext;
        List<String> mIngredients = new ArrayList<>();

        ListViewRemoteViewsFactory(Context applicationContext){
            mContext = applicationContext;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            mIngredients = getSharedPreferenceIngredients(mContext);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if(mIngredients == null) return 0;
            return mIngredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_list_item);
            if(mIngredients != null){
                views.setTextViewText(R.id.widget_ingredient_list_item_tv,
                        mIngredients.get(position));
            }
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * Helper method to retrieve the list of recipe ingredients from shared preferences.
         *
         * @param context       The context
         * @return              The recipe ingredients
         */
        private List<String> getSharedPreferenceIngredients(Context context){
            List<String> ingredients = new ArrayList<>();
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getResources().getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE);
            //Retrieve from shared preferences the string containing the master
            //list of ingredients.
            String ingredientsString = sharedPref.getString(context.getResources()
                    .getString(R.string.preference_ingredients_key), null);
            //Separate the ingredients string into individual ingredients and add each to
            //the list of ingredients.
            if(ingredientsString != null){
                StringTokenizer tokens = new StringTokenizer(ingredientsString, ";");
                while(tokens.hasMoreTokens()){
                   ingredients.add(tokens.nextToken());
                }
            }
            return ingredients;
        }
    }
}
