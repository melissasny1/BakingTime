package com.example.android.bakingtime;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a Recipe Object
 */

public class Recipe implements Parcelable {

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel parcel) {
            return new Recipe(parcel);
        }

        @Override
        public Recipe[] newArray(int i) {
            return new Recipe[i];
        }
    };

    private final String mRecipeName;
    private final List<String> mIngredients;
    private final List<RecipeStep> mRecipeSteps;
    private final String mImageUrl;

    public Recipe(String recipeName, List<String> ingredients,
                  List<RecipeStep> recipeSteps, String imageUrl){
        mRecipeName = recipeName;
        mIngredients = ingredients;
        mRecipeSteps = recipeSteps;
        mImageUrl = imageUrl;
    }

    private Recipe(Parcel in){
        mRecipeName = in.readString();
        mIngredients = new ArrayList<>();
        in.readStringList(mIngredients);
        mRecipeSteps = new ArrayList<>();
        in.readTypedList(mRecipeSteps, RecipeStep.CREATOR);
        mImageUrl = in.readString();
    }

    //Getter methods
    public String getRecipeName(){
        return mRecipeName;
    }

    public List<String> getIngredients(){return mIngredients;}

    public List<RecipeStep> getRecipeSteps(){return mRecipeSteps;}

    public String getImageUrl() { return mImageUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mRecipeName);
        parcel.writeStringList(mIngredients);
        parcel.writeTypedList(mRecipeSteps);
        parcel.writeString(mImageUrl);
    }
}
