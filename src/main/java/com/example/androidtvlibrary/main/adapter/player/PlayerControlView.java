package com.example.androidtvlibrary.main.adapter.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.example.androidtvlibrary.R;
import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.TestPlayer;
import com.example.androidtvlibrary.main.adapter.Timeline;
import com.example.androidtvlibrary.main.adapter.Util;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerControlView extends FrameLayout {

    static {
//        ExoPlayerLibraryInfo.registerModule("goog.exo.ui");
    }

    /** Listener to be notified about changes of the visibility of the UI control. */
    public interface VisibilityListener {

        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
         */
        void onVisibilityChange(int visibility);
    }

    /** Listener to be notified when progress has been updated. */
    public interface ProgressUpdateListener {

        /**
         * Called when progress needs to be updated.
         *
         * @param position The current position.
         * @param bufferedPosition The current buffered position.
         */
        void onProgressUpdate(long position, long bufferedPosition);
    }

    /** The default fast forward increment, in milliseconds. */
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    /** The default rewind increment, in milliseconds. */
    public static final int DEFAULT_REWIND_MS = 5000;
    /** The default show timeout, in milliseconds. */
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;
    /** The default repeat toggle modes. */
    public static final @RepeatModeUtil.RepeatToggleModes int DEFAULT_REPEAT_TOGGLE_MODES =
            RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE;
    /** The default minimum interval between time bar position updates. */
    public static final int DEFAULT_TIME_BAR_MIN_UPDATE_INTERVAL_MS = 200;
    /** The maximum number of windows that can be shown in a multi-window time bar. */
    public static final int MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = 100;

    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    /** The maximum interval between time bar position updates. */
    private static final int MAX_UPDATE_INTERVAL_MS = 1000;

    private final ComponentListener componentListener;
    private final CopyOnWriteArrayList<VisibilityListener> visibilityListeners;
    @Nullable private final View previousButton;
    @Nullable private final View nextButton;
    @Nullable private final View playButton;
    @Nullable private final View pauseButton;
    @Nullable private final View fastForwardButton;
    @Nullable private final View rewindButton;
    @Nullable private final ImageView repeatToggleButton;
    @Nullable private final ImageView shuffleButton;
    @Nullable private final View vrButton;
    @Nullable private final TextView durationView;
    @Nullable private final TextView positionView;
    @Nullable private final TimeBar timeBar;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final com.example.androidtvlibrary.main.adapter.Timeline.Period period;
    private final com.example.androidtvlibrary.main.adapter.Timeline.Window window;
    private final Runnable updateProgressAction;
    private final Runnable hideAction;

    private final Drawable repeatOffButtonDrawable;
    private final Drawable repeatOneButtonDrawable;
    private final Drawable repeatAllButtonDrawable;
    private final String repeatOffButtonContentDescription;
    private final String repeatOneButtonContentDescription;
    private final String repeatAllButtonContentDescription;
    private final Drawable shuffleOnButtonDrawable;
    private final Drawable shuffleOffButtonDrawable;
    private final float buttonAlphaEnabled;
    private final float buttonAlphaDisabled;
    private final String shuffleOnContentDescription;
    private final String shuffleOffContentDescription;

    @Nullable private TestPlayer player;
    private ControlDispatcher controlDispatcher;
    @Nullable private ProgressUpdateListener progressUpdateListener;
    @Nullable private PlaybackPreparer playbackPreparer;

    private boolean isAttachedToWindow;
    private boolean showMultiWindowTimeBar;
    private boolean multiWindowTimeBar;
    private boolean scrubbing;
    private int rewindMs;
    private int fastForwardMs;
    private int showTimeoutMs;
    private int timeBarMinUpdateIntervalMs;
    private @RepeatModeUtil.RepeatToggleModes int repeatToggleModes;
    private boolean showShuffleButton;
    private long hideAtMs;
    private long[] adGroupTimesMs;
    private boolean[] playedAdGroups;
    private long[] extraAdGroupTimesMs;
    private boolean[] extraPlayedAdGroups;
    private long currentWindowOffset;

    public PlayerControlView(Context context) {
        this(context, /* attrs= */ null);
    }

    public PlayerControlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, /* defStyleAttr= */ 0);
    }

    public PlayerControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, attrs);
    }

    @SuppressWarnings({
            "nullness:argument.type.incompatible",
            "nullness:method.invocation.invalid",
            "nullness:methodref.receiver.bound.invalid"
    })
    public PlayerControlView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            @Nullable AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr);
        int controllerLayoutId = R.layout.wow_player_control_view;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;
        repeatToggleModes = DEFAULT_REPEAT_TOGGLE_MODES;
        timeBarMinUpdateIntervalMs = DEFAULT_TIME_BAR_MIN_UPDATE_INTERVAL_MS;
        hideAtMs = C.TIME_UNSET;
        showShuffleButton = false;
        if (playbackAttrs != null) {
//            TypedArray a =
//                    context
//                            .getTheme()
//                            .obtainStyledAttributes(playbackAttrs, R.styleable.PlayerControlView, 0, 0);
//            try {
//                rewindMs = a.getInt(R.styleable.PlayerControlView_rewind_increment, rewindMs);
//                fastForwardMs =
//                        a.getInt(R.styleable.PlayerControlView_fastforward_increment, fastForwardMs);
//                showTimeoutMs = a.getInt(R.styleable.PlayerControlView_show_timeout, showTimeoutMs);
//                controllerLayoutId =
//                        a.getResourceId(R.styleable.PlayerControlView_controller_layout_id, controllerLayoutId);
//                repeatToggleModes = getRepeatToggleModes(a, repeatToggleModes);
//                showShuffleButton =
//                        a.getBoolean(R.styleable.PlayerControlView_show_shuffle_button, showShuffleButton);
//                setTimeBarMinUpdateInterval(
//                        a.getInt(
//                                R.styleable.PlayerControlView_time_bar_min_update_interval,
//                                timeBarMinUpdateIntervalMs));
//            } finally {
//                a.recycle();
//            }
        }
        visibilityListeners = new CopyOnWriteArrayList<>();
        period = new com.example.androidtvlibrary.main.adapter.Timeline.Period();
        window = new com.example.androidtvlibrary.main.adapter.Timeline.Window();
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        adGroupTimesMs = new long[0];
        playedAdGroups = new boolean[0];
        extraAdGroupTimesMs = new long[0];
        extraPlayedAdGroups = new boolean[0];
        componentListener = new ComponentListener();
        controlDispatcher = new DefaultControlDispatcher();
        updateProgressAction = this::updateProgress;
        hideAction = this::hide;

        LayoutInflater.from(context).inflate(controllerLayoutId, /* root= */ this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        TimeBar customTimeBar = findViewById(R.id.exo_progress);
        View timeBarPlaceholder = findViewById(R.id.exo_progress_placeholder);
        if (customTimeBar != null) {
            timeBar = customTimeBar;
        } else if (timeBarPlaceholder != null) {
            // Propagate attrs as timebarAttrs so that DefaultTimeBar's custom attributes are transferred,
            // but standard attributes (e.g. background) are not.
            DefaultTimeBar defaultTimeBar = new DefaultTimeBar(context, null, 0, playbackAttrs);
            defaultTimeBar.setId(R.id.exo_progress);
            defaultTimeBar.setLayoutParams(timeBarPlaceholder.getLayoutParams());
            ViewGroup parent = ((ViewGroup) timeBarPlaceholder.getParent());
            int timeBarIndex = parent.indexOfChild(timeBarPlaceholder);
            parent.removeView(timeBarPlaceholder);
            parent.addView(defaultTimeBar, timeBarIndex);
            timeBar = defaultTimeBar;
        } else {
            timeBar = null;
        }
        durationView = findViewById(R.id.exo_duration);
        positionView = findViewById(R.id.exo_position);

        if (timeBar != null) {
            timeBar.addListener(componentListener);
        }
        playButton = findViewById(R.id.exo_play);
        if (playButton != null) {
            playButton.setOnClickListener(componentListener);
        }
        pauseButton = findViewById(R.id.exo_pause);
        if (pauseButton != null) {
            pauseButton.setOnClickListener(componentListener);
        }
        previousButton = findViewById(R.id.exo_prev);
        if (previousButton != null) {
            previousButton.setOnClickListener(componentListener);
        }
        nextButton = findViewById(R.id.exo_next);
        if (nextButton != null) {
            nextButton.setOnClickListener(componentListener);
        }
        rewindButton = findViewById(R.id.exo_rew);
        if (rewindButton != null) {
            rewindButton.setOnClickListener(componentListener);
        }
        fastForwardButton = findViewById(R.id.exo_ffwd);
        if (fastForwardButton != null) {
            fastForwardButton.setOnClickListener(componentListener);
        }
        repeatToggleButton = findViewById(R.id.exo_repeat_toggle);
        if (repeatToggleButton != null) {
            repeatToggleButton.setOnClickListener(componentListener);
        }
        shuffleButton = findViewById(R.id.exo_shuffle);
        if (shuffleButton != null) {
            shuffleButton.setOnClickListener(componentListener);
        }
        vrButton = findViewById(R.id.exo_vr);
        setShowVrButton(false);

        Resources resources = context.getResources();

        buttonAlphaEnabled =
                (float) 50 / 100;
        buttonAlphaDisabled =
                (float) 50 / 100;

//        repeatOffButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_off);
//        repeatOneButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_one);
//        repeatAllButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_all);
//        shuffleOnButtonDrawable = resources.getDrawable(R.drawable.exo_controls_shuffle_on);
//        shuffleOffButtonDrawable = resources.getDrawable(R.drawable.exo_controls_shuffle_off);
        repeatOffButtonDrawable = resources.getDrawable(R.drawable.lb_ic_replay);
        repeatOneButtonDrawable = resources.getDrawable(R.drawable.lb_ic_replay);
        repeatAllButtonDrawable = resources.getDrawable(R.drawable.lb_ic_replay);
        shuffleOnButtonDrawable = resources.getDrawable(R.drawable.lb_ic_shuffle);
        shuffleOffButtonDrawable = resources.getDrawable(R.drawable.lb_ic_shuffle);
        repeatOffButtonContentDescription =
                "resources.getString(R.string.exo_controls_repeat_off_description)";
        repeatOneButtonContentDescription =
                "resources.getString(R.string.exo_controls_repeat_one_description)";
        repeatAllButtonContentDescription =
                "resources.getString(R.string.exo_controls_repeat_all_description)";
        shuffleOnContentDescription = "resources.getString(R.string.exo_controls_shuffle_on_description)";
        shuffleOffContentDescription =
                "resources.getString(R.string.exo_controls_shuffle_off_description)";
    }

    @SuppressWarnings("ResourceType")
//    private static @RepeatModeUtil.RepeatToggleModes int getRepeatToggleModes(
//            TypedArray a, @RepeatModeUtil.RepeatToggleModes int repeatToggleModes) {
//        return a.getInt(R.styleable.PlayerControlView_repeat_toggle_modes, repeatToggleModes);
//    }

    /**
     * Returns the {@link TestPlayer} currently being controlled by this view, or null if no player is
     * set.
     */
    @Nullable
    public TestPlayer getPlayer() {
        return player;
    }

    /**
     * Sets the {@link TestPlayer} to control.
     *
     * @param player The {@link TestPlayer} to control, or {@code null} to detach the current player. Only
     *     players which are accessed on the main thread are supported ({@code
     *     player.getApplicationLooper() == Looper.getMainLooper()}).
     */
    public void setPlayer(@Nullable TestPlayer player) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        Assertions.checkArgument(
                player == null || player.getApplicationLooper() == Looper.getMainLooper());
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);
        }
        updateAll();
    }

    /**
     * Sets whether the time bar should show all windows, as opposed to just the current one. If the
     * timeline has a period with unknown duration or more than {@link
     * #MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR} windows the time bar will fall back to showing a single
     * window.
     *
     * @param showMultiWindowTimeBar Whether the time bar should show all windows.
     */
    public void setShowMultiWindowTimeBar(boolean showMultiWindowTimeBar) {
        this.showMultiWindowTimeBar = showMultiWindowTimeBar;
        updateTimeline();
    }

    /**
     * Sets the millisecond positions of extra ad markers relative to the start of the window (or
     * timeline, if in multi-window mode) and whether each extra ad has been played or not. The
     * markers are shown in addition to any ad markers for ads in the player's timeline.
     *
     * @param extraAdGroupTimesMs The millisecond timestamps of the extra ad markers to show, or
     *     {@code null} to show no extra ad markers.
     * @param extraPlayedAdGroups Whether each ad has been played. Must be the same length as {@code
     *     extraAdGroupTimesMs}, or {@code null} if {@code extraAdGroupTimesMs} is {@code null}.
     */
    public void setExtraAdGroupMarkers(
            @Nullable long[] extraAdGroupTimesMs, @Nullable boolean[] extraPlayedAdGroups) {
        if (extraAdGroupTimesMs == null) {
            this.extraAdGroupTimesMs = new long[0];
            this.extraPlayedAdGroups = new boolean[0];
        } else {
            extraPlayedAdGroups = Assertions.checkNotNull(extraPlayedAdGroups);
            Assertions.checkArgument(extraAdGroupTimesMs.length == extraPlayedAdGroups.length);
            this.extraAdGroupTimesMs = extraAdGroupTimesMs;
            this.extraPlayedAdGroups = extraPlayedAdGroups;
        }
        updateTimeline();
    }

    /**
     * Adds a {@link VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void addVisibilityListener(VisibilityListener listener) {
        visibilityListeners.add(listener);
    }

    /**
     * Removes a {@link VisibilityListener}.
     *
     * @param listener The listener to be removed.
     */
    public void removeVisibilityListener(VisibilityListener listener) {
        visibilityListeners.remove(listener);
    }

    /**
     * Sets the {@link ProgressUpdateListener}.
     *
     * @param listener The listener to be notified about when progress is updated.
     */
    public void setProgressUpdateListener(@Nullable ProgressUpdateListener listener) {
        this.progressUpdateListener = listener;
    }

    /**
     * Sets the {@link PlaybackPreparer}.
     *
     * @param playbackPreparer The {@link PlaybackPreparer}, or null to remove the current playback
     *     preparer.
     */
    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        this.playbackPreparer = playbackPreparer;
    }

    /**
     * Sets the {@link ControlDispatcher}.
     *
     * @param controlDispatcher The {@link ControlDispatcher}, or null
     *     to use {@link DefaultControlDispatcher}.
     */
    public void setControlDispatcher(
            @Nullable ControlDispatcher controlDispatcher) {
        this.controlDispatcher =
                controlDispatcher == null
                        ? new DefaultControlDispatcher()
                        : controlDispatcher;
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
     *     rewind button to be disabled.
     */
    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
        updateNavigation();
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
     *     cause the fast forward button to be disabled.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
        updateNavigation();
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input.
     *
     * @return The duration in milliseconds. A non-positive value indicates that the controls will
     *     remain visible indefinitely.
     */
    public int getShowTimeoutMs() {
        return showTimeoutMs;
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input.
     *
     * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
     *     to remain visible indefinitely.
     */
    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
        if (isVisible()) {
            // Reset the timeout.
            hideAfterTimeout();
        }
    }

    /**
     * Returns which repeat toggle modes are enabled.
     *
     * @return The currently enabled {@link RepeatModeUtil.RepeatToggleModes}.
     */
    public @RepeatModeUtil.RepeatToggleModes int getRepeatToggleModes() {
        return repeatToggleModes;
    }

    /**
     * Sets which repeat toggle modes are enabled.
     *
     * @param repeatToggleModes A set of {@link RepeatModeUtil.RepeatToggleModes}.
     */
    public void setRepeatToggleModes(@RepeatModeUtil.RepeatToggleModes int repeatToggleModes) {
        this.repeatToggleModes = repeatToggleModes;
        if (player != null) {
            @TestPlayer.RepeatMode int currentMode = player.getRepeatMode();
            if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE
                    && currentMode != TestPlayer.REPEAT_MODE_OFF) {
                controlDispatcher.dispatchSetRepeatMode(player, TestPlayer.REPEAT_MODE_OFF);
            } else if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_ONE
                    && currentMode == TestPlayer.REPEAT_MODE_ALL) {
                controlDispatcher.dispatchSetRepeatMode(player, TestPlayer.REPEAT_MODE_ONE);
            } else if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL
                    && currentMode == TestPlayer.REPEAT_MODE_ONE) {
                controlDispatcher.dispatchSetRepeatMode(player, TestPlayer.REPEAT_MODE_ALL);
            }
        }
        updateRepeatModeButton();
    }

    /** Returns whether the shuffle button is shown. */
    public boolean getShowShuffleButton() {
        return showShuffleButton;
    }

    /**
     * Sets whether the shuffle button is shown.
     *
     * @param showShuffleButton Whether the shuffle button is shown.
     */
    public void setShowShuffleButton(boolean showShuffleButton) {
        this.showShuffleButton = showShuffleButton;
        updateShuffleButton();
    }

    /** Returns whether the VR button is shown. */
    public boolean getShowVrButton() {
        return vrButton != null && vrButton.getVisibility() == VISIBLE;
    }

    /**
     * Sets whether the VR button is shown.
     *
     * @param showVrButton Whether the VR button is shown.
     */
    public void setShowVrButton(boolean showVrButton) {
        if (vrButton != null) {
            vrButton.setVisibility(showVrButton ? VISIBLE : GONE);
        }
    }

    /**
     * Sets listener for the VR button.
     *
     * @param onClickListener Listener for the VR button, or null to clear the listener.
     */
    public void setVrButtonListener(@Nullable OnClickListener onClickListener) {
        if (vrButton != null) {
            vrButton.setOnClickListener(onClickListener);
        }
    }

    /**
     * Sets the minimum interval between time bar position updates.
     *
     * <p>Note that smaller intervals, e.g. 33ms, will result in a smooth movement but will use more
     * CPU resources while the time bar is visible, whereas larger intervals, e.g. 200ms, will result
     * in a step-wise update with less CPU usage.
     *
     * @param minUpdateIntervalMs The minimum interval between time bar position updates, in
     *     milliseconds.
     */
    public void setTimeBarMinUpdateInterval(int minUpdateIntervalMs) {
        // Do not accept values below 16ms (60fps) and larger than the maximum update interval.
        timeBarMinUpdateIntervalMs =
                Util.constrainValue(minUpdateIntervalMs, 16, MAX_UPDATE_INTERVAL_MS);
    }

    /**
     * Shows the playback controls. If {@link #getShowTimeoutMs()} is positive then the controls will
     * be automatically hidden after this duration of time has elapsed without user input.
     */
    public void show() {
        if (!isVisible()) {
            setVisibility(VISIBLE);
            for (VisibilityListener visibilityListener : visibilityListeners) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            updateAll();
            requestPlayPauseFocus();
        }
        // Call hideAfterTimeout even if already visible to reset the timeout.
        hideAfterTimeout();
    }

    /** Hides the controller. */
    public void hide() {
        if (isVisible()) {
            setVisibility(GONE);
            for (VisibilityListener visibilityListener : visibilityListeners) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            removeCallbacks(updateProgressAction);
            removeCallbacks(hideAction);
            hideAtMs = C.TIME_UNSET;
        }
    }

    /** Returns whether the controller is currently visible. */
    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    private void hideAfterTimeout() {
        removeCallbacks(hideAction);
        if (showTimeoutMs > 0) {
            hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
            if (isAttachedToWindow) {
                postDelayed(hideAction, showTimeoutMs);
            }
        } else {
            hideAtMs = C.TIME_UNSET;
        }
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateRepeatModeButton();
        updateShuffleButton();
        updateTimeline();
    }

    private void updatePlayPauseButton() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        boolean requestPlayPauseFocus = false;
        boolean shouldShowPauseButton = shouldShowPauseButton();
        if (playButton != null) {
            requestPlayPauseFocus |= shouldShowPauseButton && playButton.isFocused();
            playButton.setVisibility(shouldShowPauseButton ? GONE : VISIBLE);
        }
        if (pauseButton != null) {
            requestPlayPauseFocus |= !shouldShowPauseButton && pauseButton.isFocused();
            pauseButton.setVisibility(shouldShowPauseButton ? VISIBLE : GONE);
        }
        if (requestPlayPauseFocus) {
            requestPlayPauseFocus();
        }
    }

    private void updateNavigation() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }

        @Nullable TestPlayer player = this.player;
        boolean enableSeeking = false;
        boolean enablePrevious = false;
        boolean enableRewind = false;
        boolean enableFastForward = false;
        boolean enableNext = false;
        if (player != null) {
            com.example.androidtvlibrary.main.adapter.Timeline timeline = player.getCurrentTimeline();
            if (!timeline.isEmpty() && !player.isPlayingAd()) {
                timeline.getWindow(player.getCurrentWindowIndex(), window);
                boolean isSeekable = window.isSeekable;
                enableSeeking = isSeekable;
                enablePrevious = isSeekable || !window.isDynamic || player.hasPrevious();
                enableRewind = isSeekable && rewindMs > 0;
                enableFastForward = isSeekable && fastForwardMs > 0;
                enableNext = window.isDynamic || player.hasNext();
            }
        }

        setButtonEnabled(enablePrevious, previousButton);
        setButtonEnabled(enableRewind, rewindButton);
        setButtonEnabled(enableFastForward, fastForwardButton);
        setButtonEnabled(enableNext, nextButton);
        if (timeBar != null) {
            timeBar.setEnabled(enableSeeking);
        }
    }

    private void updateRepeatModeButton() {
        if (!isVisible() || !isAttachedToWindow || repeatToggleButton == null) {
            return;
        }

        if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE) {
            repeatToggleButton.setVisibility(GONE);
            return;
        }

        @Nullable TestPlayer player = this.player;
        if (player == null) {
            setButtonEnabled(false, repeatToggleButton);
            repeatToggleButton.setImageDrawable(repeatOffButtonDrawable);
            repeatToggleButton.setContentDescription(repeatOffButtonContentDescription);
            return;
        }

        setButtonEnabled(true, repeatToggleButton);
        switch (player.getRepeatMode()) {
            case TestPlayer.REPEAT_MODE_OFF:
                repeatToggleButton.setImageDrawable(repeatOffButtonDrawable);
                repeatToggleButton.setContentDescription(repeatOffButtonContentDescription);
                break;
            case TestPlayer.REPEAT_MODE_ONE:
                repeatToggleButton.setImageDrawable(repeatOneButtonDrawable);
                repeatToggleButton.setContentDescription(repeatOneButtonContentDescription);
                break;
            case TestPlayer.REPEAT_MODE_ALL:
                repeatToggleButton.setImageDrawable(repeatAllButtonDrawable);
                repeatToggleButton.setContentDescription(repeatAllButtonContentDescription);
                break;
            default:
                // Never happens.
        }
        repeatToggleButton.setVisibility(VISIBLE);
    }

    private void updateShuffleButton() {
        if (!isVisible() || !isAttachedToWindow || shuffleButton == null) {
            return;
        }

        @Nullable TestPlayer player = this.player;
        if (!showShuffleButton) {
            shuffleButton.setVisibility(GONE);
        } else if (player == null) {
            setButtonEnabled(false, shuffleButton);
            shuffleButton.setImageDrawable(shuffleOffButtonDrawable);
            shuffleButton.setContentDescription(shuffleOffContentDescription);
        } else {
            setButtonEnabled(true, shuffleButton);
            shuffleButton.setImageDrawable(
                    player.getShuffleModeEnabled() ? shuffleOnButtonDrawable : shuffleOffButtonDrawable);
            shuffleButton.setContentDescription(
                    player.getShuffleModeEnabled()
                            ? shuffleOnContentDescription
                            : shuffleOffContentDescription);
        }
    }

    private void updateTimeline() {
        @Nullable TestPlayer player = this.player;
        if (player == null) {
            return;
        }
        multiWindowTimeBar =
                showMultiWindowTimeBar && canShowMultiWindowTimeBar(player.getCurrentTimeline(), window);
        currentWindowOffset = 0;
        long durationUs = 0;
        int adGroupCount = 0;
        com.example.androidtvlibrary.main.adapter.Timeline timeline = player.getCurrentTimeline();
        if (!timeline.isEmpty()) {
            int currentWindowIndex = player.getCurrentWindowIndex();
            int firstWindowIndex = multiWindowTimeBar ? 0 : currentWindowIndex;
            int lastWindowIndex = multiWindowTimeBar ? timeline.getWindowCount() - 1 : currentWindowIndex;
            for (int i = firstWindowIndex; i <= lastWindowIndex; i++) {
                if (i == currentWindowIndex) {
                    currentWindowOffset = C.usToMs(durationUs);
                }
                timeline.getWindow(i, window);
                if (window.durationUs == C.TIME_UNSET) {
                    Assertions.checkState(!multiWindowTimeBar);
                    break;
                }
                for (int j = window.firstPeriodIndex; j <= window.lastPeriodIndex; j++) {
                    timeline.getPeriod(j, period);
                    int periodAdGroupCount = period.getAdGroupCount();
                    for (int adGroupIndex = 0; adGroupIndex < periodAdGroupCount; adGroupIndex++) {
                        long adGroupTimeInPeriodUs = period.getAdGroupTimeUs(adGroupIndex);
                        if (adGroupTimeInPeriodUs == C.TIME_END_OF_SOURCE) {
                            if (period.durationUs == C.TIME_UNSET) {
                                // Don't show ad markers for postrolls in periods with unknown duration.
                                continue;
                            }
                            adGroupTimeInPeriodUs = period.durationUs;
                        }
                        long adGroupTimeInWindowUs = adGroupTimeInPeriodUs + period.getPositionInWindowUs();
                        if (adGroupTimeInWindowUs >= 0) {
                            if (adGroupCount == adGroupTimesMs.length) {
                                int newLength = adGroupTimesMs.length == 0 ? 1 : adGroupTimesMs.length * 2;
                                adGroupTimesMs = Arrays.copyOf(adGroupTimesMs, newLength);
                                playedAdGroups = Arrays.copyOf(playedAdGroups, newLength);
                            }
                            adGroupTimesMs[adGroupCount] = C.usToMs(durationUs + adGroupTimeInWindowUs);
                            playedAdGroups[adGroupCount] = period.hasPlayedAdGroup(adGroupIndex);
                            adGroupCount++;
                        }
                    }
                }
                durationUs += window.durationUs;
            }
        }
        long durationMs = C.usToMs(durationUs);
        if (durationView != null) {
            durationView.setText(Util.getStringForTime(formatBuilder, formatter, durationMs));
        }
        if (timeBar != null) {
            timeBar.setDuration(durationMs);
            int extraAdGroupCount = extraAdGroupTimesMs.length;
            int totalAdGroupCount = adGroupCount + extraAdGroupCount;
            if (totalAdGroupCount > adGroupTimesMs.length) {
                adGroupTimesMs = Arrays.copyOf(adGroupTimesMs, totalAdGroupCount);
                playedAdGroups = Arrays.copyOf(playedAdGroups, totalAdGroupCount);
            }
            System.arraycopy(extraAdGroupTimesMs, 0, adGroupTimesMs, adGroupCount, extraAdGroupCount);
            System.arraycopy(extraPlayedAdGroups, 0, playedAdGroups, adGroupCount, extraAdGroupCount);
            timeBar.setAdGroupTimesMs(adGroupTimesMs, playedAdGroups, totalAdGroupCount);
        }
        updateProgress();
    }

    private void updateProgress() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }

        @Nullable TestPlayer player = this.player;
        long position = 0;
        long bufferedPosition = 0;
        if (player != null) {
            position = currentWindowOffset + player.getContentPosition();
            bufferedPosition = currentWindowOffset + player.getContentBufferedPosition();
        }
        if (positionView != null && !scrubbing) {
            positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
        }
        if (timeBar != null) {
            timeBar.setPosition(position);
            timeBar.setBufferedPosition(bufferedPosition);
        }
        if (progressUpdateListener != null) {
            progressUpdateListener.onProgressUpdate(position, bufferedPosition);
        }

        // Cancel any pending updates and schedule a new one if necessary.
        removeCallbacks(updateProgressAction);
        int playbackState = player == null ? TestPlayer.STATE_IDLE : player.getPlaybackState();
        if (player != null && player.isPlaying()) {
            long mediaTimeDelayMs =
                    timeBar != null ? timeBar.getPreferredUpdateDelay() : MAX_UPDATE_INTERVAL_MS;

            // Limit delay to the start of the next full second to ensure position display is smooth.
            long mediaTimeUntilNextFullSecondMs = 1000 - position % 1000;
            mediaTimeDelayMs = Math.min(mediaTimeDelayMs, mediaTimeUntilNextFullSecondMs);

            // Calculate the delay until the next update in real time, taking playbackSpeed into account.
            float playbackSpeed = player.getPlaybackParameters().speed;
            long delayMs =
                    playbackSpeed > 0 ? (long) (mediaTimeDelayMs / playbackSpeed) : MAX_UPDATE_INTERVAL_MS;

            // Constrain the delay to avoid too frequent / infrequent updates.
            delayMs = Util.constrainValue(delayMs, timeBarMinUpdateIntervalMs, MAX_UPDATE_INTERVAL_MS);
            postDelayed(updateProgressAction, delayMs);
        } else if (playbackState != TestPlayer.STATE_ENDED && playbackState != TestPlayer.STATE_IDLE) {
            postDelayed(updateProgressAction, MAX_UPDATE_INTERVAL_MS);
        }
    }

    private void requestPlayPauseFocus() {
        boolean shouldShowPauseButton = shouldShowPauseButton();
        if (!shouldShowPauseButton && playButton != null) {
            playButton.requestFocus();
        } else if (shouldShowPauseButton && pauseButton != null) {
            pauseButton.requestFocus();
        }
    }

    private void setButtonEnabled(boolean enabled, @Nullable View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? buttonAlphaEnabled : buttonAlphaDisabled);
        view.setVisibility(VISIBLE);
    }

    private void previous(TestPlayer player) {
        com.example.androidtvlibrary.main.adapter.Timeline timeline = player.getCurrentTimeline();
        if (timeline.isEmpty() || player.isPlayingAd()) {
            return;
        }
        int windowIndex = player.getCurrentWindowIndex();
        timeline.getWindow(windowIndex, window);
        int previousWindowIndex = player.getPreviousWindowIndex();
        if (previousWindowIndex != C.INDEX_UNSET
                && (player.getCurrentPosition() <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS
                || (window.isDynamic && !window.isSeekable))) {
            seekTo(player, previousWindowIndex, C.TIME_UNSET);
        } else {
            seekTo(player, windowIndex, /* positionMs= */ 0);
        }
    }

    private void next(TestPlayer player) {
        com.example.androidtvlibrary.main.adapter.Timeline timeline = player.getCurrentTimeline();
        if (timeline.isEmpty() || player.isPlayingAd()) {
            return;
        }
        int windowIndex = player.getCurrentWindowIndex();
        int nextWindowIndex = player.getNextWindowIndex();
        if (nextWindowIndex != C.INDEX_UNSET) {
            seekTo(player, nextWindowIndex, C.TIME_UNSET);
        } else if (timeline.getWindow(windowIndex, window).isDynamic) {
            seekTo(player, windowIndex, C.TIME_UNSET);
        }
    }

    private void rewind(TestPlayer player) {
        if (player.isCurrentWindowSeekable() && rewindMs > 0) {
            seekToOffset(player, -rewindMs);
        }
    }

    private void fastForward(TestPlayer player) {
        if (player.isCurrentWindowSeekable() && fastForwardMs > 0) {
            seekToOffset(player, fastForwardMs);
        }
    }

    private void seekToOffset(TestPlayer player, long offsetMs) {
        long positionMs = player.getCurrentPosition() + offsetMs;
        long durationMs = player.getDuration();
        if (durationMs != C.TIME_UNSET) {
            positionMs = Math.min(positionMs, durationMs);
        }
        positionMs = Math.max(positionMs, 0);
        seekTo(player, player.getCurrentWindowIndex(), positionMs);
    }

    private void seekToTimeBarPosition(TestPlayer player, long positionMs) {
        int windowIndex;
        com.example.androidtvlibrary.main.adapter.Timeline timeline = player.getCurrentTimeline();
        if (multiWindowTimeBar && !timeline.isEmpty()) {
            int windowCount = timeline.getWindowCount();
            windowIndex = 0;
            while (true) {
                long windowDurationMs = timeline.getWindow(windowIndex, window).getDurationMs();
                if (positionMs < windowDurationMs) {
                    break;
                } else if (windowIndex == windowCount - 1) {
                    // Seeking past the end of the last window should seek to the end of the timeline.
                    positionMs = windowDurationMs;
                    break;
                }
                positionMs -= windowDurationMs;
                windowIndex++;
            }
        } else {
            windowIndex = player.getCurrentWindowIndex();
        }
        boolean dispatched = seekTo(player, windowIndex, positionMs);
        if (!dispatched) {
            // The seek wasn't dispatched then the progress bar scrubber will be in the wrong position.
            // Trigger a progress update to snap it back.
            updateProgress();
        }
    }

    private boolean seekTo(TestPlayer player, int windowIndex, long positionMs) {
        return controlDispatcher.dispatchSeekTo(player, windowIndex, positionMs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        if (hideAtMs != C.TIME_UNSET) {
            long delayMs = hideAtMs - SystemClock.uptimeMillis();
            if (delayMs <= 0) {
                hide();
            } else {
                postDelayed(hideAction, delayMs);
            }
        } else if (isVisible()) {
            hideAfterTimeout();
        }
        updateAll();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        removeCallbacks(updateProgressAction);
        removeCallbacks(hideAction);
    }

    @Override
    public final boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            removeCallbacks(hideAction);
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            hideAfterTimeout();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        super.setOnKeyListener(l);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    /**
     * Called to process media key events. Any {@link KeyEvent} can be passed but only media key
     * events will be handled.
     *
     * @param event A key event.
     * @return Whether the key event was handled.
     */
    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        @Nullable TestPlayer player = this.player;
        if (player == null || !isHandledMediaKey(keyCode)) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                fastForward(player);
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                rewind(player);
            } else if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        controlDispatcher.dispatchSetPlayWhenReady(player, !player.getPlayWhenReady());
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        controlDispatcher.dispatchSetPlayWhenReady(player, true);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        controlDispatcher.dispatchSetPlayWhenReady(player, false);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        next(player);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        previous(player);
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }

    private boolean shouldShowPauseButton() {
        return player != null
                && player.getPlaybackState() != TestPlayer.STATE_ENDED
                && player.getPlaybackState() != TestPlayer.STATE_IDLE
                && player.getPlayWhenReady();
    }

    @SuppressLint("InlinedApi")
    private static boolean isHandledMediaKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT
                || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS;
    }

    /**
     * Returns whether the specified {@code timeline} can be shown on a multi-window time bar.
     *
     * @param timeline The {@link Timeline} to check.
     * @param window A scratch {@link Timeline.Window} instance.
     * @return Whether the specified timeline can be shown on a multi-window time bar.
     */
    private static boolean canShowMultiWindowTimeBar(com.example.androidtvlibrary.main.adapter.Timeline timeline,
                                                     com.example.androidtvlibrary.main.adapter.Timeline.Window window) {
        if (timeline.getWindowCount() > MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR) {
            return false;
        }
        int windowCount = timeline.getWindowCount();
        for (int i = 0; i < windowCount; i++) {
            if (timeline.getWindow(i, window).durationUs == C.TIME_UNSET) {
                return false;
            }
        }
        return true;
    }

    private final class ComponentListener
            implements TestPlayer.EventListener, TimeBar.OnScrubListener, OnClickListener {

        @Override
        public void onScrubStart(TimeBar timeBar, long position) {
            scrubbing = true;
            if (positionView != null) {
                positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
            }
        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position) {
            if (positionView != null) {
                positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
            }
        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
            scrubbing = false;
            if (!canceled && player != null) {
                seekToTimeBarPosition(player, position);
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, @TestPlayer.State int playbackState) {
            updatePlayPauseButton();
            updateProgress();
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            updateProgress();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            updateRepeatModeButton();
            updateNavigation();
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            updateShuffleButton();
            updateNavigation();
        }

        @Override
        public void onPositionDiscontinuity(@TestPlayer.DiscontinuityReason int reason) {
            updateNavigation();
            updateTimeline();
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @TestPlayer.TimelineChangeReason int reason) {
            updateNavigation();
            updateTimeline();
        }

        @Override
        public void onClick(View view) {
            TestPlayer player = PlayerControlView.this.player;
            if (player == null) {
                return;
            }
            if (nextButton == view) {
                next(player);
            } else if (previousButton == view) {
                previous(player);
            } else if (fastForwardButton == view) {
                fastForward(player);
            } else if (rewindButton == view) {
                rewind(player);
            } else if (playButton == view) {
                if (player.getPlaybackState() == TestPlayer.STATE_IDLE) {
                    if (playbackPreparer != null) {
                        playbackPreparer.preparePlayback();
                    }
                } else if (player.getPlaybackState() == TestPlayer.STATE_ENDED) {
                    seekTo(player, player.getCurrentWindowIndex(), C.TIME_UNSET);
                }
                controlDispatcher.dispatchSetPlayWhenReady(player, true);
            } else if (pauseButton == view) {
                controlDispatcher.dispatchSetPlayWhenReady(player, false);
            } else if (repeatToggleButton == view) {
                controlDispatcher.dispatchSetRepeatMode(
                        player, RepeatModeUtil.getNextRepeatMode(player.getRepeatMode(), repeatToggleModes));
            } else if (shuffleButton == view) {
                controlDispatcher.dispatchSetShuffleModeEnabled(player, !player.getShuffleModeEnabled());
            }
        }
    }
}

