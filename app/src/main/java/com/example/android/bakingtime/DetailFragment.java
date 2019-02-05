package com.example.android.bakingtime;

// For the recipe step clicked by the user, this fragment displays the video or, if no video is
//available, a thumbnail image, if one is provided, plus the long description of the recipe step.

import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.GONE;

public class DetailFragment extends Fragment implements Player.EventListener{

    private static final String TAG = DetailFragment.class.getSimpleName();
    private static final String RESUME_WINDOW_KEY = "resume window";
    private static final String RESUME_POSITION_KEY = "resume position";
    private static final String PLAY_WHEN_READY_KEY = "play when ready";

    private RecipeStep mRecipeStep;
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private boolean mPlayWhenReady = false;
    private int mResumeWindow;
    private long mResumePosition;
    @BindView(R.id.player_view) PlayerView mPlayerView;
    @BindView(R.id.recipe_step_image_view) ImageView mThumbnailView;
    @BindView(R.id.recipe_step_description_textview) TextView mRecipeStepDescTextView;

    //Variable for the ButterKnife unbinder.
    private Unbinder unbinder;

    public DetailFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Inflate the Fragment layout.
        View rootView = inflater.inflate(R.layout.fragment_detail, container,
                false);
        unbinder = ButterKnife.bind(this, rootView);

        if (savedInstanceState != null && savedInstanceState.containsKey(RESUME_POSITION_KEY)){
            mResumePosition = savedInstanceState.getLong(RESUME_POSITION_KEY);
            mResumeWindow = savedInstanceState.getInt(RESUME_WINDOW_KEY);
            mPlayWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY_KEY);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Ensure that the volume controls control the correct audio stream.
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Get the Urls for the recipe step video and thumbnail.
        String videoUrl = mRecipeStep.getVideoUrl();
        String thumbnailUrl = mRecipeStep.getThumbnailUrl();

        //If there is a video for the recipe step, initialize the media session, and initialize
        //the SimpleExoPlayer; if there is no video but there is a thumbnail, display that
        //image with the recipe step long description; otherwise hide the player view and
        //display the detailed recipe step description with a generic baking image.
        if(videoUrl != null && !videoUrl.isEmpty()) {
            Uri recipeStepUri = Uri.parse(videoUrl);

            // Initialize the Media Session.
            initializeMediaSession();

            //Initialize the SimpleExoPlayer.
            initializePlayer(recipeStepUri);

            //Hide the recipe step long description if there's an internet connection and the
            //device is smaller than a tablet and in landscape mode.
            adjustViewForDeviceSizeAndOrientation();
        } else if (thumbnailUrl != null && !thumbnailUrl.isEmpty()
                && SharedHelper.hasInternetConnection(getContext())){
            mPlayerView.setVisibility(GONE);

            Picasso.get().load(thumbnailUrl)
                    .error(R.drawable.baked_goods)
                    .into(mThumbnailView);
            mThumbnailView.setVisibility(View.VISIBLE);
            mRecipeStepDescTextView.setVisibility(View.VISIBLE);
        } else{
            mPlayerView.setVisibility(GONE);
            mThumbnailView.setImageResource(R.drawable.baked_goods);
            mThumbnailView.setVisibility(View.VISIBLE);
            mRecipeStepDescTextView.setVisibility(View.VISIBLE);
        }

        mRecipeStepDescTextView.setText(mRecipeStep.getDescription());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mExoPlayer != null) {
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mMediaSession != null){
            mMediaSession.setActive(false);
        }
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mExoPlayer != null){
            outState.putLong(RESUME_POSITION_KEY, mResumePosition);
            outState.putInt(RESUME_WINDOW_KEY, mResumeWindow);
            outState.putBoolean(PLAY_WHEN_READY_KEY, mPlayWhenReady);
        }
    }

    public void setRecipeStep(RecipeStep recipeStep){
        mRecipeStep = recipeStep;
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_REWIND );

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Initialize ExoPlayer.
     * @param videoUri The URI of the recipe step video to play.
     */
    private void initializePlayer(Uri videoUri) {

        if (mExoPlayer == null) {
            if(SharedHelper.hasInternetConnection(getContext())){
                // Create an instance of the ExoPlayer.
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                //Create the player.
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

                //Bind the player to the view.
                mPlayerView.setPlayer(mExoPlayer);

                //Prepare the MediaSource.
                String userAgent = Util.getUserAgent(getContext(), "BakingTime");
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                        getContext(), userAgent, bandwidthMeter);

                // This is the MediaSource representing the media to be played.
                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(videoUri);
                //If there is a saved player position, set it.
                boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
                if (haveResumePosition) {
                    mExoPlayer.seekTo(mResumeWindow, mResumePosition);
                }
                // Prepare the player with the source.
                mExoPlayer.prepare(videoSource, !haveResumePosition, false);
                mExoPlayer.setPlayWhenReady(mPlayWhenReady);
            } else{
                mPlayerView.setVisibility(GONE);
                mThumbnailView.setImageResource(R.drawable.baked_goods);
                mThumbnailView.setVisibility(View.VISIBLE);
                mRecipeStepDescTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    //Helper method to release the player resources.
    private void releasePlayer(){
        mExoPlayer.stop();
        updateResumePosition();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    //Helper method to save the current player position.
    private void updateResumePosition() {
        mResumeWindow = mExoPlayer.getCurrentWindowIndex();
        mResumePosition = Math.max(0, mExoPlayer.getContentPosition());
    }

    //Helper method. If there's a video to display and an internet connection, if the device
    //is smaller than a tablet and in landscape mode, set the visibility for the view containing
    //the recipe step long description to GONE.
    private void adjustViewForDeviceSizeAndOrientation(){
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE
                && config.smallestScreenWidthDp < 600) {
            if (SharedHelper.hasInternetConnection(getContext())) {
                mRecipeStepDescTextView.setVisibility(GONE);
            } else {
                mRecipeStepDescTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onRewind() {mExoPlayer.setPlayWhenReady(true); }
    }


    //Player.EventListener methods
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) { }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

    @Override
    public void onLoadingChanged(boolean isLoading) {}

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of Player. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == Player.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == Player.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {}

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {}

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPositionDiscontinuity(int reason) {}

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}

    @Override
    public void onSeekProcessed() {}
}

