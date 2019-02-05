package com.example.android.bakingtime;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Helper methods related to retrieving recipes from the online database.
 */

final class QueryUtils {

    static final String RECIPES_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String INGREDIENT_MEASURE_TO_IGNORE = "UNIT";
    private static final String INGREDIENT_MEASURE_TO_PLURALIZE = "CUP";
    private static final String INGREDIENT_MEASURE_CUPS = "Cups";

    /**
     * From the recipe database, fetch all recipes.
     *
     * @param recipesStringUrl The string containing the recipes database url.
     * @return The list of recipes.
     */

    static List<Recipe> fetchRecipes(String recipesStringUrl){
        //Create the query URL.
        URL recipesUrl = null;
        try {
            recipesUrl = new URL(recipesStringUrl);
        } catch (MalformedURLException e) {
            Log.e(MainActivity.LOG_TAG, "Error creating URL.", e);
        }

        if (recipesUrl == null) return null;

        try{
            //Perform the network request for recipes.
            String jsonResponse = getResultsFromHttpRequest(recipesUrl);
            //Extract the recipe data required to create the Recipe objects and return the list
            //of recipes to be displayed in the RecyclerView of query results.
            return getRecipeInfo(jsonResponse);
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP query.
     * @throws IOException related to network and stream reading.
     */
    private static String getResultsFromHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);

        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static List<Recipe> getRecipeInfo(String recipeJsonString) {
        //Create a list of recipe objects to be populated with the data to be extracted from the
        //recipeJsonString.
        List<Recipe> recipes = new ArrayList<>();

        try {
            JSONArray recipeDataArray = new JSONArray(recipeJsonString);

            for (int i = 0; i < recipeDataArray.length(); i++) {
                JSONObject individualRecipe = recipeDataArray.getJSONObject(i);
                List<String> ingredients = new ArrayList<>();
                List<RecipeStep> steps = new ArrayList<>();
                String recipeName = individualRecipe.optString("name", "NA");
                String imageUrl = individualRecipe.optString("image", "");
                JSONArray ingredientArray = individualRecipe.getJSONArray("ingredients");
                JSONArray recipeStepsArray = individualRecipe.getJSONArray("steps");

                for(int j = 0; j < ingredientArray.length(); j++){
                    StringBuilder ingredient = new StringBuilder();
                    JSONObject individualIngredient = ingredientArray.getJSONObject(j);
                    String name = individualIngredient.optString("ingredient",
                            "NA");
                    double qty = individualIngredient.optDouble("quantity");
                    String quantity = formatDouble(qty);
                    String measure = individualIngredient.optString("measure",
                                   "NA");

                    if(qty > 1 && measure.equalsIgnoreCase(INGREDIENT_MEASURE_TO_PLURALIZE)){
                        measure = INGREDIENT_MEASURE_CUPS;
                    }

                    if(measure.equalsIgnoreCase(INGREDIENT_MEASURE_TO_IGNORE)){
                        ingredient.append(quantity)
                                .append(" ")
                                .append(name);
                    } else{
                        ingredient.append(quantity)
                                .append(" ")
                                .append(measure.toLowerCase())
                                .append(" ")
                                .append(name);
                    }
                    //Add the ingredient to the list of ingredients.
                    ingredients.add(ingredient.toString());
                }

                for(int k = 0; k < recipeStepsArray.length(); k++){
                    JSONObject individualRecipeStep = recipeStepsArray.getJSONObject(k);
                    String shortDescription = individualRecipeStep
                            .optString("shortDescription","NA");
                    String description = individualRecipeStep
                            .optString("description","NA");
                    String videoUrl = individualRecipeStep
                            .optString("videoURL","");
                    String thumbnailUrl = individualRecipeStep
                            .optString("thumbnailURL","");

                    //Create a new Recipe Step Object and add it to the list of Recipe Steps.
                    steps.add(new RecipeStep(shortDescription, description,
                            videoUrl, thumbnailUrl));
                }

                //Create a new Recipe Object and add it to the list of recipes.
                recipes.add(new Recipe(recipeName, ingredients, steps, imageUrl));
            }

            return recipes;

        } catch (JSONException e) {
            Log.e(MainActivity.LOG_TAG, "Error parsing JSON data. ", e);
            return null;
        }
    }

    /**
     * Display a double as an integer in a String, if the double has no fraction.
     *
     * @param d     The double to display as a String
     * @return      The double formatted as a String
     */
    private static String formatDouble(double d)
    {
        if(d == (long) d)
            return String.format(Locale.US,"%d",(long)d);
        else
            return String.format("%s",d);
    }

}
