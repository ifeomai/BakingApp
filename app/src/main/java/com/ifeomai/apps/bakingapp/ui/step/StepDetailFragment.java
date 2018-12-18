package com.ifeomai.apps.bakingapp.ui.step;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Step;
import com.ifeomai.apps.bakingapp.ui.detail.StepsList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class StepDetailFragment extends Fragment //implements Player.EventListener {
{
    private static final String STEP_ARG = "step_arg";
    private static final String MEDIA_SESSION_TAG = "MEDIA_SESSION";
    private static final String EXTRA_PLAYBACK_STATE = "playback_state";
    private static final String EXTRA_CURRENT_POSITION = "current_position";
    private static final String STEP_POSITION = "step_position";

   // private ExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private Step step;
    private int position;

    @BindView(R.id.step_instruction_text_view)
    TextView stepTextView;

//    @BindView(R.id.step_video_view)
//    PlayerView exoPlayerView;

    @BindView(R.id.move_left_image_view)
    ImageView swipeLeftImageView;

    @BindView(R.id.move_right_image_view)
    ImageView swipeRightImageView;

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
            step = args.getParcelable(STEP_ARG);
            position = args.getInt(STEP_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step_detail, container, false);

        ButterKnife.bind(this, v);

        initUI();

        return v;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            long currentPosition = savedInstanceState.getLong(EXTRA_CURRENT_POSITION);
            int currentState = savedInstanceState.getInt(EXTRA_PLAYBACK_STATE);

//            if (playbackStateBuilder != null && exoPlayer != null) {
//                playbackStateBuilder.setState(currentState, currentPosition, 1f);
//                exoPlayer.seekTo(currentPosition);
//            }
        }
    }

    private void initUI() {
        if (step != null) {
            if (TextUtils.isEmpty(step.getVideoUrl())) {
               // exoPlayerView.setVisibility(GONE);
            } else {
               // exoPlayerView.setDefaultArtwork( requireContext().getResources().getDrawable(R.drawable.exo_icon_play));
                Uri videoUri = Uri.parse(step.getVideoUrl());
                initialiseMediaSession();
                initialisePlayer(videoUri);
            }
            stepTextView.setText(step.getDescription());

            if (requireContext().getResources().getBoolean(R.bool.is_tablet)) {
                swipeLeftImageView.setVisibility(GONE);
                swipeRightImageView.setVisibility(GONE);
            }

            //TODO: Add swipe Listeners
            /*swipeLeftImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Step> steps = StepsList.getSteps();
                    if (position != 0) {
                        int newPosition = position - 1;
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.activity_step_fragment_container,
                                        StepDetailFragment.getInstance(
                                                steps.get(newPosition), newPosition))
                                .commit();
                    }
                }
            });

            swipeRightImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Step> steps = StepsList.getSteps();
                    if (position != steps.size() - 1) {
                        int newPosition = position + 1;
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.activity_step_fragment_container,
                                        StepDetailFragment.getInstance(
                                                steps.get(newPosition), newPosition))
                                .commit();
                    }
                }
            });*/
        }
    }

    private void initialiseMediaSession() {
        mediaSession = new MediaSessionCompat(requireContext(), MEDIA_SESSION_TAG);

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setMediaButtonReceiver(null);

        playbackStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(playbackStateBuilder.build());

        //mediaSession.setCallback(new StepSessionCallback());
        mediaSession.setActive(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        // releasePlayer();
        if (mediaSession != null) {
            mediaSession.setActive(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        if (exoPlayer != null) {
//            long currentPosition = exoPlayer.getCurrentPosition();
//            int currentState = exoPlayer.getPlaybackState();
//
//            outState.putInt(EXTRA_PLAYBACK_STATE, currentState);
//            outState.putLong(EXTRA_CURRENT_POSITION, currentPosition);
//        }

        super.onSaveInstanceState(outState);
    }

    private void initialisePlayer(Uri mediaUri) {
  /*      if (exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(requireContext());

            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    requireContext(), renderersFactory, trackSelector, loadControl);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.addListener(this);

            MediaSource mediaSource = new ExtractorMediaSource.Factory(
                    new DefaultDataSourceFactory(requireContext(), Constants.STEP_VIDEO_AGENT))
                    .createMediaSource(mediaUri);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }*/
    }

/*    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            playbackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            playbackStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(), 1f);
        }

        mediaSession.setPlaybackState(playbackStateBuilder.build());
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

    private class StepSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0);
        }
    }*/
}

