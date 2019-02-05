package com.example.android.bakingtime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality to create a widget that displays the ingredient
 * list for the last recipe selected within the app by the user.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.baking_widget_provider);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setEmptyView(R.id.widget_linear_layout, R.id.empty_view);

        //Get the name of the saved recipe from Shared Preferences.
        String recipeName = getSharedPreferenceRecipeName(context);
        //Hide the empty view if there's a recipe name to display from Shared Preferences.
        if(recipeName != null && !recipeName.isEmpty()){
            views.setViewVisibility(R.id.empty_view, View.GONE);
        }
        views.setTextViewText(R.id.widget_recipe_name_text, recipeName);

        Intent intent = new Intent(context, ListViewWidgetService.class);
        views.setRemoteAdapter(R.id.widget_ingredients_list_view, intent);

        //Create an Intent to launch MainActivity when the widget is clicked.
        Intent launchMainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, 0 , launchMainActivityIntent, 0);
        //Set click handler to launch pending intent.
        views.setOnClickPendingIntent(R.id.widget_linear_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care
        //of updating the widgets UI
        IngredientUpdateService.startActionUpdateIngredients(context);
    }

    public static void updateBakingWidgets(Context context, AppWidgetManager appWidgetManager,
                                           int[] appWidgetIds){
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Helper method to retrieve the recipe name from shared preferences.
     *
     * @param context       The context
     * @return              The recipe name
     */
    private static String getSharedPreferenceRecipeName(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        return sharedPref.getString(context.getResources()
                .getString(R.string.preference_recipe_name_key), null);
    }
}

