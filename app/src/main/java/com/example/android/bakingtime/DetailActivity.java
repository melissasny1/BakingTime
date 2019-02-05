package com.example.android.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DetailActivity extends AppCompatActivity {
    private static final String RECIPE_NAME_KEY = "recipe name";
    private static final String RECIPE_STEP_POSITION_KEY = "position of selected recipe step";
    private static final String RECIPE_NUMBER_OF_STEPS_KEY = "number of recipe steps";
    private static final String INITIAL_FRAGMENT_KEY = "first fragment";

    //Variable for the recipe name passed into the DetailActivity via Intent.
    private String mRecipeName;
    //Variable for the number of recipe steps for the selected recipe, passed into the
    //DetailActivity via Intent.
    private int mNumberOfRecipeSteps;
    private int mPositionOfRecipeStepToDisplay;
    private int mIsInitialFragmentCreated = 1;
    @BindView(R.id.nav_to_previous_step_image) ImageView navToPreviousStep;
    @BindView(R.id.nav_to_next_step_image) ImageView navToNextStep;
    //Variable for the ButterKnife unbinder.
    private Unbinder unbinder;
    private static final int ONE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        unbinder = ButterKnife.bind(this);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(RECIPE_NAME_KEY)){
                mRecipeName = savedInstanceState.getString(RECIPE_NAME_KEY);
                mPositionOfRecipeStepToDisplay = savedInstanceState.getInt(RECIPE_STEP_POSITION_KEY);
                mNumberOfRecipeSteps = savedInstanceState.getInt(RECIPE_NUMBER_OF_STEPS_KEY);
                mIsInitialFragmentCreated = savedInstanceState.getInt(INITIAL_FRAGMENT_KEY);
            }
        }
        else {
            //Get the intent used to start DetailActivity.
            Intent intentUsedToStartThisActivity = getIntent();

            //If the intent used to start DetailActivity is not null and contains the recipe name
            //string, the position of the user-selected recipe step and the number of recipe steps,
            //"unpack" them.
            if(intentUsedToStartThisActivity != null
                    && intentUsedToStartThisActivity.hasExtra
                    (MasterListActivity.INTENT_RECIPE_NAME_KEY)
                    && intentUsedToStartThisActivity.hasExtra
                    (MasterListActivity.INTENT_POSITION_OF_SELECTED_RECIPE_STEP_KEY)
                    && intentUsedToStartThisActivity.hasExtra
                    (MasterListActivity.INTENT_NUMBER_OF_RECIPE_STEPS_KEY)){

                mRecipeName = intentUsedToStartThisActivity.getStringExtra
                        (MasterListActivity.INTENT_RECIPE_NAME_KEY);
                mPositionOfRecipeStepToDisplay = intentUsedToStartThisActivity.getIntExtra
                        (MasterListActivity.INTENT_POSITION_OF_SELECTED_RECIPE_STEP_KEY,
                                0);
                mNumberOfRecipeSteps = intentUsedToStartThisActivity.getIntExtra
                        (MasterListActivity.INTENT_NUMBER_OF_RECIPE_STEPS_KEY, 0);

                FragmentManager fragmentManager = getSupportFragmentManager();
                SharedHelper.createDetailFragment(mPositionOfRecipeStepToDisplay, fragmentManager,
                        mIsInitialFragmentCreated);
                mIsInitialFragmentCreated = 0;
            }
        }
        setTitle(mRecipeName);
        setNavigationVisibility();
    }

    @OnClick(R.id.nav_to_previous_step_image)
    public void clickPreviousStepNav(){
        mPositionOfRecipeStepToDisplay = mPositionOfRecipeStepToDisplay - ONE;
        setNavigationVisibility();
        FragmentManager fragmentManager = getSupportFragmentManager();
        SharedHelper.createDetailFragment(mPositionOfRecipeStepToDisplay,
                fragmentManager, mIsInitialFragmentCreated);
        mIsInitialFragmentCreated = 0;
    }

    @OnClick(R.id.nav_to_next_step_image)
    public void clickNextStepNav(){
        mPositionOfRecipeStepToDisplay = mPositionOfRecipeStepToDisplay + ONE;
        setNavigationVisibility();
        FragmentManager fragmentManager = getSupportFragmentManager();
        SharedHelper.createDetailFragment(mPositionOfRecipeStepToDisplay,
                fragmentManager, mIsInitialFragmentCreated);
        mIsInitialFragmentCreated = 0;
    }

    /**
     * Helper method to set the visibility of the "previous" and "next" navigation arrows,
     * depending upon the position of the recipe step selected.
     */
    private void setNavigationVisibility(){
        navToPreviousStep.setVisibility(View.VISIBLE);
        navToNextStep.setVisibility(View.VISIBLE);

        if(mPositionOfRecipeStepToDisplay == 0){
            navToPreviousStep.setVisibility(View.INVISIBLE);
        }
        if(mPositionOfRecipeStepToDisplay == mNumberOfRecipeSteps - ONE){
            navToNextStep.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mRecipeName != null){
            outState.putString(RECIPE_NAME_KEY, mRecipeName);
            outState.putInt(RECIPE_STEP_POSITION_KEY, mPositionOfRecipeStepToDisplay);
            outState.putInt(RECIPE_NUMBER_OF_STEPS_KEY, mNumberOfRecipeSteps);
            outState.putInt(INITIAL_FRAGMENT_KEY, mIsInitialFragmentCreated);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
