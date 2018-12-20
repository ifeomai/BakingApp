package com.ifeomai.apps.bakingapp.ui.step;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Recipe;
import com.ifeomai.apps.bakingapp.data.model.Step;
import com.ifeomai.apps.bakingapp.ui.detail.StepsList;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class StepDetailFragment extends Fragment implements Player.EventListener
{

    private static final String STEP_VIDEO_AGENT = "STEP_VIDEO_AGENT";

    private static final String STEP_ARG = "step_arg";
    private static final String STEP_POSITION = "step_position";

    public static final String SAVE_STEP = "save_step";
    public static final String STATE_STEP_INDEX = "state_step_index";
    public static final String STATE_PLAYBACK_POSITION = "state_playback_position";
    public static final String STATE_CURRENT_WINDOW = "state_current_window";
    public static final String STATE_PLAY_WHEN_READY = "state_play_when_ready";

    /** Constants for ExoPlayer */
    public static final float PLAYER_PLAYBACK_SPEED = 1f;
    public static final int REWIND_INCREMENT = 3000;
    public static final int FAST_FORWARD_INCREMENT = 3000;
    public static final int START_POSITION = 0;

    /** The notification channel ID is used to link notifications to this channel */
    public static final String BAKING_NOTIFICATION_CHANNEL_ID = "baking_notification_channel_id";
    /** The pending intent ID is used to uniquely reference the pending intent */
    public static final int BAKING_PENDING_INTENT_ID = 0;
    /** The notification ID is used to access our notification after we've displayed it */
    public static final int BAKING_NOTIFICATION_ID = 20;

    @BindView(R.id.step_instruction_text_view)
    TextView stepTextView;

    @BindView(R.id.step_video_view)
    PlayerView exoPlayerView;

    @BindView(R.id.move_left_image_view)
    ImageView swipeLeftImageView;

    @BindView(R.id.move_right_image_view)
    ImageView swipeRightImageView;


    /** Member variable for Step that this fragment displays */
    private Step mStep;
    /** Position of the step in the list*/
    private int mStepIndex;
    /** Member variable for the Recipe */
    private Recipe mRecipe;

    /** Member variable for a video URL */
    private String mVideoUrl;
    /** Member variable for a thumbnail URL */
    private String mThumbnailUrl;
    /** Indicate whether the step of the recipe has a video URL */
    private boolean mHasVideoUrl = false;

    /** Member variable for the ExoPlayer */
    private SimpleExoPlayer mExoPlayer;

    /** Initialize with 0 to start from the beginning of the window */
    private long mPlaybackPosition;

    /** Initialize with 0 to start from the first item in the TimeLine */
    private int mCurrentWindow;

    private boolean mPlayWhenReady;

    private static MediaSessionCompat sMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

    // Tag for a MediaSessionCompat
    private static final String TAG = StepDetailFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public StepDetailFragment() {
    }

    public static StepDetailFragment getInstance(Step step, int position) {
        StepDetailFragment stepDetailFragment = new StepDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable(STEP_ARG, step);
        args.putInt(STEP_POSITION, position);

        stepDetailFragment.setArguments(args);

        return stepDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mStep = args.getParcelable(STEP_ARG);
            mStepIndex = args.getInt(STEP_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Instantiate FragmentStepDetailBinding using DataBindingUtil

        View v = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, v);

        // Load the saved state (the step, step index, playback position, current window,
        // play when ready) if there is one
        if (savedInstanceState != null) {
           /* mStep = savedInstanceState.getParcelable(SAVE_STEP);
            mStepIndex = savedInstanceState.getInt(STATE_STEP_INDEX);*/
            mPlaybackPosition = savedInstanceState.getLong(STATE_PLAYBACK_POSITION);
            mCurrentWindow = savedInstanceState.getInt(STATE_CURRENT_WINDOW);
            mPlayWhenReady = savedInstanceState.getBoolean(STATE_PLAY_WHEN_READY);
        } else {
            // Clear the start position
            mCurrentWindow = C.INDEX_UNSET;
            mPlaybackPosition = C.TIME_UNSET;
            mPlayWhenReady = true;
        }

        // If the Step exists, set the description to the TextView and handle media URL.
        // Otherwise, create a Log statement that indicates that the step was not found
        if(mStep != null) {

            // Set correct description
            setCorrectDescription(mStep);

            // Handles video URL and thumbnail URL
            handleMediaUrl();

        }

        // Initialize the Media Session
        initializeMediaSession();

        // Get the recipe data from the intent
        getRecipeData();

        // When the user clicks the next button, it navigates to the next step
        onNextButtonClick();
        // When the user clicks the previous button, it navigates to the previous step
        onPreviousButtonClick();
        // Display the current step in a single-pane phone case (e.g. "Step 5 of 12")
        setStepId();

        // Hide next button at the end of the step and the previous button at the beginning
        hideButtonAtBeginningEnd();

        // Return the rootView
        return v;
    }

    /**
     *  If the step ID does not correspond to the step index, replace the number in the
     *  description which represents step ID with the correct id.
     *  (e.g. Step ID of Yellow cake from  8 to 13)
     *
     *  @return Step description with the correct step ID
     */
    private String getDescriptionWithCorrectStepId(Step step) {
        // Extract step ID and description
        int stepId = 0;
        String description = step.getDescription();

       /* if (stepId != mStepIndex) {
            // If the step ID does not correspond to the step index, replace step ID with step index
            stepId = mStepIndex;

            // Find the position of the first occurrence of the period
            int periodIndex = description.indexOf(getString(R.string.period));
            // Concatenate correct step ID and the substring
            description = stepId + description.substring(periodIndex);

            // Set a new description of the step
            //step.setDescription(description);
        //}*/
        return description;
    }

    /**
     * Sets the correct description.
     */
    private void setCorrectDescription(Step step) {
        // Replace the number in the description which represents step ID with the correct ID.
        String descriptionWithCorrectStepId = getDescriptionWithCorrectStepId(step);

        // Replace a question mark "�" with the degree "°"(e.g. step 1 description of Brownies)
        String replacedDescription = replaceString(descriptionWithCorrectStepId);

        // Display the correct description

        stepTextView.setText(replacedDescription);
    }

    /**
     * When the user clicks the next button, it navigates to the next step.
     */
    private void onNextButtonClick() {
        // Set a click listener on the next button
        swipeRightImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                // Increment position as long as the index remains <= the size of the step list
                if (mStepIndex < StepsList.getSteps().size() - 1) {
                    mStepIndex++;
                    stepDetailFragment.setStep(StepsList.getSteps().get(mStepIndex));
                    stepDetailFragment.setStepIndex(mStepIndex);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.activity_step_fragment_container, stepDetailFragment)
                            .commit();
                }
            }
        });
    }

    /**
     * When the user clicks the previous button, it navigates to the previous step.
     */
    private void onPreviousButtonClick() {
        // Set a click listener on the previous button
        swipeLeftImageView.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                // Decrement position by one if the index is greater than zero
                if (mStepIndex > 0) {
                    mStepIndex--;
                    stepDetailFragment.setStep(StepsList.getSteps().get(mStepIndex));
                    stepDetailFragment.setStepIndex(mStepIndex);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.activity_step_fragment_container, stepDetailFragment)
                            .commit();
                }
            }
        });
    }

    /**
     * Hide next button at the end of the step and the previous button at the beginning.
     */
    private void hideButtonAtBeginningEnd() {
        int lastStep = StepsList.getSteps().size() -1;
        if (mStepIndex == lastStep) {
            swipeRightImageView.setVisibility(View.INVISIBLE);
        } else if (mStepIndex == 0) {
            swipeLeftImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Display the current step in a single-pane phone case (e.g. "Step 5 of 12")
     */
    private void setStepId() {
        if (!isTwoPane()) {
            //int lastStep = mRecipe.getSteps().size() - 1;
            int lastStep = StepsList.getSteps().size() - 1;
            String currentStepOfLast = getString(R.string.step) + String.valueOf(mStepIndex)
                    + getString(R.string.space) + getString(R.string.of)
                    + getString(R.string.space) + String.valueOf(lastStep);
            //mStepDetailBinding.tvStepId.setText(currentStepOfLast);
        }
    }

    /**
     * Get the recipe data from the intent.
     */
    private void getRecipeData() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
           /* if (intent.hasExtra(EXTRA_RECIPE)) {
                // Get the recipe from the intent
                Bundle b = intent.getBundleExtra(EXTRA_RECIPE);}
                mRecipe = b.getParcelable(EXTRA_RECIPE);*/

        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat
        sMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls
        sMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible
        sMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_REWIND |
                                PlaybackStateCompat.ACTION_FAST_FORWARD |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        sMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller
        sMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the fragment is active
        sMediaSession.setActive(true);
    }

    /**
     * Handles video URL and thumbnail URL
     */
    private void handleMediaUrl() {
        // Get video URL and thumbnail URL from the step of the recipe
        mVideoUrl = mStep.getVideoUrl();
        mThumbnailUrl = mStep.getThumbnailUrl();

        // Check if the thumbnail URL contains an "mp4" file
        // Step 5 of the Nutella Pie has an mp4 file in the thumbnail URL
        if (mThumbnailUrl.contains(getResources().getString(R.string.mp4))) {
            mVideoUrl = mThumbnailUrl;
        }

        if (!mVideoUrl.isEmpty()) {
            // If the video URL exists, set the boolean variable to true
            mHasVideoUrl = true;
        } else if (!mThumbnailUrl.isEmpty()) {
            // If the thumbnail URL exists, load thumbnail with Picasso
            exoPlayerView.setVisibility(View.GONE);
            /*Picasso.with(getContext())
                    .load(mThumbnailUrl)
                    .error(R.drawable.woman_with_dish)
                    .placeholder(R.drawable.woman_with_dish)
                    .into(mStepDetailBinding.ivEmpty);*/
        } else {
            // If the step of the recipe has no visual media, load chef image
            exoPlayerView.setVisibility(View.GONE);
            //mStepDetailBinding.ivEmpty.setImageResource(R.drawable.woman_with_dish);
        }
    }

    /**
     * Setter method for displaying current step
     */
    public void setStep(Step step) {
        mStep = step;
    }

    /**
     * Setter method for displaying which step in the list is currently displayed.
     *
     * @param stepIndex Position of the step in the list
     */
    public void setStepIndex(int stepIndex) {
        mStepIndex = stepIndex;
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param hasVideoUrl True if the step of the recipe has a video
     */
    private void initializePlayer(boolean hasVideoUrl) {
        // Check if the step of the recipe has a video
        if (hasVideoUrl) {
            if (mExoPlayer == null) {
                // Create an instance of the ExoPlayer
                /*DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(getContext());
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                        defaultRenderersFactory, trackSelector, loadControl);*/

                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                RenderersFactory renderersFactory = new DefaultRenderersFactory(requireContext());

                mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                        requireContext(), renderersFactory, trackSelector, loadControl);

                // Set the ExoPlayer to the playerView
                exoPlayerView.setPlayer(mExoPlayer);

                mExoPlayer.setPlayWhenReady(mPlayWhenReady);
            }

            // Set the Player.EventListener to this fragment
            mExoPlayer.addListener(this);

            // Prepare the MediaSource
            Uri mediaUri = Uri.parse(mVideoUrl);
            MediaSource mediaSource = buildMediaSource(mediaUri);

            // Restore the playback position
            boolean haveStartPosition = mCurrentWindow != C.INDEX_UNSET;
            if (haveStartPosition) {
                mExoPlayer.seekTo(mCurrentWindow, mPlaybackPosition);
            }
            // The boolean flags indicate whether to reset position and state of the player
            mExoPlayer.prepare(mediaSource, !haveStartPosition, false);
        }
    }

    /**
     * Create a MediaSource
     *
     * @param mediaUri The URI of the sample to play.
     */
    private MediaSource buildMediaSource(Uri mediaUri) {
        /*String userAgent = Util.getUserAgent(this.getContext(), getString(R.string.app_name));
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(mediaUri);*/

        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(requireContext(), STEP_VIDEO_AGENT))
                .createMediaSource(mediaUri);
        return mediaSource;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Starting with API level 24 Android supports multiple windows. As our app can be visible
        // but not active in split window mode, we need to initialize the player in onStart().
        if (Util.SDK_INT > Build.VERSION_CODES.M) {
            // Initialize the player if the step of the recipe has a video URL
            initializePlayer(mHasVideoUrl);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        // Before API level 24 we wait as long as possible until we grab resources, so we wait until
        // onResume() before initializing the player.
        if (Util.SDK_INT <= Build.VERSION_CODES.M || mExoPlayer == null) {
            // Initialize the player if the step of the recipe has a video URL
            initializePlayer(mHasVideoUrl);
        }
    }

    /**
     * Determine if you're creating a two-pane or single-pane display.
     * A single-pane display refers to phone screens, and two-pane to larger tablet screens.
     */
    private boolean isTwoPane() {
        // The TextView that displays the step ID will only initially exist in a single-pane phone case.
        return getResources().getBoolean(R.bool.is_tablet);
        //return mStepDetailBinding.tvStepId == null;
    }

    /**
     * Returns true in a single-pane landscape mode.
     */
    private boolean isSinglePaneLand() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && !isTwoPane();
    }

    /**
     * Enables the user to have a pure full-screen experience in a single-pane landscape mode.
     */
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        if (isSinglePaneLand()) {
            int flagFullScreen = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            // Enable full-screen mode on PlayerView, empty ImageView
            exoPlayerView.setSystemUiVisibility(flagFullScreen);
            //mStepDetailBinding.ivEmpty.setSystemUiVisibility(flagFullScreen);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Before API level 24 there is no guarantee of onStop() being called. So we have to release
        // the player as early as possible in onPause().
        if (Util.SDK_INT <= Build.VERSION_CODES.M) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Starting with API level 24 onStop() is guaranteed to be called and in the paused mode
        // our activity is eventually still visible. Hence we need to wait releasing until onStop().
        if (Util.SDK_INT > Build.VERSION_CODES.M) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // End the Media session when it is no longer needed
        sMediaSession.setActive(false);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mNotificationManager.cancelAll();
            updateCurrentPosition();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * Updates the current state of the player
     */
    private void updateCurrentPosition() {
        if (mExoPlayer != null) {
            mPlaybackPosition = mExoPlayer.getCurrentPosition();
            mCurrentWindow = mExoPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
        }
    }

    /**
     * Replace a question mark "�" with the degree "°".
     * The Step 1 description of Brownies recipe contains a question mark, so replace it with the degree sign.
     */
    private String replaceString(String target) {
        if (target.contains(getString(R.string.question_mark))) {
            target = target.replace(getString(R.string.question_mark), getString(R.string.degree));
        }
        return target;
    }

    /**
     * When ExoPlayer is playing, hide the previous and next button in the two-pane tablet or
     * single-pane landscape mode.
     */
    private void hideButtonWhenPlaying() {
        if (isTwoPane() | isSinglePaneLand()){
            swipeRightImageView.setVisibility(View.GONE);
            swipeLeftImageView.setVisibility(View.GONE);
        }
    }

    /**
     * When ExoPlayer is paused or ended, show the previous and next button in the two-pane tablet or
     * single-pane landscape mode.
     */
    private void showButtonWhenPausedEnded() {
        if (isTwoPane() | isSinglePaneLand()) {
            swipeLeftImageView.setVisibility(View.VISIBLE);
            swipeRightImageView.setVisibility(View.VISIBLE);
        }

        // Hide next button at the end of the step and the previous button at the beginning.
        hideButtonAtBeginningEnd();
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store the step and step index to our bundle
        outState.putParcelable(SAVE_STEP, mStep);
        outState.putInt(STATE_STEP_INDEX, mStepIndex);

        // Update the current state of the player
        updateCurrentPosition();
        // Store the playback position to our bundle
        outState.putLong(STATE_PLAYBACK_POSITION, mPlaybackPosition);
        outState.putInt(STATE_CURRENT_WINDOW, mCurrentWindow);
        outState.putBoolean(STATE_PLAY_WHEN_READY, mPlayWhenReady);
    }

    // Player Event Listeners

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                        STATE_BUFFERING, or STATE_ENDED
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            // When ExoPlayer is playing, update the PlayBackState
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), PLAYER_PLAYBACK_SPEED);

            // When ExoPlayer is playing, hide the previous and next button
            hideButtonWhenPlaying();
        } else if (playbackState == Player.STATE_READY) {
            // When ExoPlayer is paused, update the PlayBackState
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), PLAYER_PLAYBACK_SPEED);

            // When ExoPlayer is paused, show the previous and next button
            showButtonWhenPausedEnded();
        } else if (playbackState == Player.STATE_ENDED) {
            // When ExoPlayer is ended, show the previous and next button
            showButtonWhenPausedEnded();
        }
        sMediaSession.setPlaybackState(mStateBuilder.build());

        // Shows Media Style notification
        showNotification(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    /**
     * Shows Media Style notification, with actions that depend on the current MediaSession
     * PlaybackState
     *
     * @param state The PlaybackState of the MediaSession
     */
    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext(), BAKING_NOTIFICATION_CHANNEL_ID);

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }

        // Create play pause notification action
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        // Create rewind notification action
        NotificationCompat.Action rewindAction = new NotificationCompat.Action(
                R.drawable.exo_controls_rewind, getString(R.string.rewind),
                MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(),
                        PlaybackStateCompat.ACTION_REWIND));

        // Create fast forward notification action
        NotificationCompat.Action fastForwardAction = new NotificationCompat.Action(
                R.drawable.exo_controls_fastforward, getString(R.string.fast_forward),
                MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(),
                        PlaybackStateCompat.ACTION_FAST_FORWARD));

        // Create a content Pending Intent that relaunches the PlayerActivity
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                getContext(),
                BAKING_PENDING_INTENT_ID,
                new Intent(getContext(), StepActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_pots)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(rewindAction)
                .addAction(playPauseAction)
                .addAction(fastForwardAction)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(sMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0,1,2));

        mNotificationManager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    BAKING_NOTIFICATION_CHANNEL_ID,
                    getContext().getString(R.string.baking_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // If the build version is greater than JELLY_BEAN and lower than OREO,
        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        // Trigger the notification
        mNotificationManager.notify(BAKING_NOTIFICATION_ID, builder.build());
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
        public void onRewind() {
            mExoPlayer.seekTo(Math.max(mExoPlayer.getCurrentPosition()
                    - REWIND_INCREMENT, START_POSITION));
        }

        @Override
        public void onFastForward() {
            long duration = mExoPlayer.getDuration();
            mExoPlayer.seekTo(Math.min(mExoPlayer.getCurrentPosition()
                    + FAST_FORWARD_INCREMENT, duration));
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(sMediaSession, intent);
        }
    }
}
