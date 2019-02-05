package com.example.android.bakingtime;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * IntentService to update the recipe ingredients displayed in the app widget.
 */

public class IngredientUpdateService extends IntentService {

    private static final String ACTION_UPDATE_INGREDIENTS = "com.example.android.bakingtime" +
            ".action.update_ingredients";

    public IngredientUpdateService(){ super("IngredientUpdateService"); }

    public static void startActionUpdateIngredients(Context context){
        Intent intent = new Intent(context, IngredientUpdateService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_UPDATE_INGREDIENTS.equals(action)){
                handleActionUpdateIngredients();
            }
        }
    }

    private void handleActionUpdateIngredients(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(this, BakingWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
                R.id.widget_ingredients_list_view);
        BakingWidgetProvider.updateBakingWidgets(this,
                appWidgetManager, appWidgetIds);
    }
}
