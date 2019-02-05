package com.example.android.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MasterListActivity extends AppCompatActivity implements
        MasterListFragment.OnRecipeStepClickListener {

    private static final String RECIPE_KEY = "Recipe";
    private static final String CLICKED_RECIPE_POSITION_KEY = "Clicked Recipe";
    static final String INTENT_RECIPE_NAME_KEY = "Recipe Name";
    static final String INTENT_POSITION_OF_SELECTED_RECIPE_STEP_KEY = "Position of " +
            "Selected Recipe Step";
    static final String INTENT_NUMBER_OF_RECIPE_STEPS_KEY = "Number of Recipe Steps";
    private static final String INITIAL_FRAGMENT_KEY = "first fragment";

    //Variable for the Recipe object passed into the MasterListActivity via Intent.
    private Recipe mRecipe;
    //Variables for the recipe components.
    private String mName;
    private List<String> mIngredients;
    static List<RecipeStep> mRecipeSteps;
    private int mClickedRecipeStepPosition = -1;
    private int mIsInitialFragmentCreated = 1;
    //Variable to track whether the UI is in one-pane or two-pane mode.
    private boolean mTwoPane;
    //Variable for the ButterKnife unbinder.
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_list);
        unbinder = ButterKnife.bind(this);

        mTwoPane = findViewById(R.id.detail_fragment_layout) != null;

        if(savedInstanceState != null && savedInstanceState.containsKey(RECIPE_KEY)){
                mRecipe = savedInstanceState.getParcelable(RECIPE_KEY);
                mClickedRecipeStepPosition = savedInstanceState.getInt(CLICKED_RECIPE_POSITION_KEY);
                mIsInitialFragmentCreated = savedInstanceState.getInt(INITIAL_FRAGMENT_KEY);
        } else {
            //Get the intent used to start MasterListFragment.
            Intent intentUsedToStartThisActivity = getIntent();

            //If the intent used to start MasterListActivity is not null and contains the Recipe parcel,
            //"unpack" the properties contained in the parcel to be displayed in the recipe detail.
            if (intentUsedToStartThisActivity != null &&
                    intentUsedToStartThisActivity.hasExtra(MainActivity.INTENT_EXTRA_KEY)) {
                mRecipe = intentUsedToStartThisActivity
                        .getParcelableExtra(MainActivity.INTENT_EXTRA_KEY);
            }
        }

        if(mRecipe != null){
            mName = mRecipe.getRecipeName();
            mIngredients = mRecipe.getIngredients();
            mRecipeSteps = mRecipe.getRecipeSteps();

            //Set the Activity title to the name of the recipe.
            setTitle(mName);
        }

        if(mTwoPane){
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(mClickedRecipeStepPosition == -1){
                int defaultRecipeStepPosition = 0;
                SharedHelper.createDetailFragment(defaultRecipeStepPosition, fragmentManager,
                        mIsInitialFragmentCreated);
                mIsInitialFragmentCreated = 0;
            } else{
                mIsInitialFragmentCreated = 0;
                SharedHelper.createDetailFragment(mClickedRecipeStepPosition, fragmentManager,
                        mIsInitialFragmentCreated);
            }
        }
    }

    @Override
    public void onRecipeStepClicked(int recipeStepPosition) {
        mClickedRecipeStepPosition = recipeStepPosition;

        //For the two-pane layout, replace the detail fragment with a new fragment for the
        //clicked recipe step.
        if(mTwoPane){
            FragmentManager fragmentManager = getSupportFragmentManager();
            mIsInitialFragmentCreated = 0;
            SharedHelper.createDetailFragment(mClickedRecipeStepPosition, fragmentManager,
                    mIsInitialFragmentCreated);
        } else{
            //For the single-pane layout, create a Bundle object to pass the the recipe name
            //and the values of the selected recipe step position and the number of recipe steps
            //to DetailActivity.
            Bundle b = new Bundle();
            b.putString(INTENT_RECIPE_NAME_KEY, mName);
            b.putInt(INTENT_POSITION_OF_SELECTED_RECIPE_STEP_KEY, recipeStepPosition);
            b.putInt(INTENT_NUMBER_OF_RECIPE_STEPS_KEY, mRecipeSteps.size());

            final Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the recipe selected by the user.
        if(mRecipe != null){
            outState.putParcelable(RECIPE_KEY, mRecipe);
        }
        outState.putInt(CLICKED_RECIPE_POSITION_KEY, mClickedRecipeStepPosition);
        outState.putInt(INITIAL_FRAGMENT_KEY, mIsInitialFragmentCreated);
    }

    //Getter methods for the lists of ingredients, recipe steps, boolean that indicates
    //whether the layout is two-pane and clicked recipe step position.
    public List<String> getIngredients(){return mIngredients;}
    public List<RecipeStep> getRecipeSteps(){return mRecipeSteps;}
    public boolean getTwoPane(){return mTwoPane;}
    public int getClickedRecipeStepPosition(){return mClickedRecipeStepPosition;}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
