package com.example.android.bakingtime;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Create a Recipe Step Object that is part of a Recipe Object.
 */

public class RecipeStep implements Parcelable{

    public static final Parcelable.Creator<RecipeStep> CREATOR = new Parcelable.Creator<RecipeStep>() {
        @Override
        public RecipeStep createFromParcel(Parcel parcel) {
            return new RecipeStep(parcel);
        }

        @Override
        public RecipeStep[] newArray(int i) {
            return new RecipeStep[i];
        }
    };

    private final String mShortDescription;
    private final String mDescription;
    private final String mVideoUrl;
    private final String mThumbnailUrl;

    RecipeStep(String shortDescription, String description, String videoUrl,
                      String thumbnailUrl){
        mShortDescription = shortDescription;
        mDescription = description;
        mVideoUrl = videoUrl;
        mThumbnailUrl = thumbnailUrl;
    }

    private RecipeStep(Parcel in) {
        mShortDescription = in.readString();
        mDescription = in.readString();
        mVideoUrl = in.readString();
        mThumbnailUrl = in.readString();
    }

    //Getter methods
    public String getShortDescription(){
        return mShortDescription;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getVideoUrl(){
        return mVideoUrl;
    }

    public String getThumbnailUrl(){
        return mThumbnailUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(mShortDescription);
        parcel.writeString(mDescription);
        parcel.writeString(mVideoUrl);
        parcel.writeString(mThumbnailUrl);
    }
}
