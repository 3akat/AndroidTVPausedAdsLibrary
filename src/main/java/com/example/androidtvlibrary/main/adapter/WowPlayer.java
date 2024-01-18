package com.example.androidtvlibrary.main.adapter;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.example.androidtvlibrary.main.adapter.wow.AnalyticsCollector;
import com.example.androidtvlibrary.main.adapter.wow.Clock;
import com.example.androidtvlibrary.main.adapter.wow.DefaultBandwidthMeter;
import com.example.androidtvlibrary.main.adapter.wow.DefaultLoadControl;
import com.example.androidtvlibrary.main.adapter.wow.DefaultTrackSelector;
import com.example.androidtvlibrary.main.adapter.wow.LoadControl;
import com.example.androidtvlibrary.main.adapter.wow.MediaSource;
import com.example.androidtvlibrary.main.adapter.wow.PlayerMessage;
import com.example.androidtvlibrary.main.adapter.wow.Renderer;
import com.example.androidtvlibrary.main.adapter.wow.SeekParameters;
import com.example.androidtvlibrary.main.adapter.wow.TrackSelector;
import com.example.androidtvlibrary.main.adapter.wow.WowPlayerImpl;

public interface WowPlayer extends TestPlayer {

    /**
     * A builder for {@link WowPlayer} instances.
     *
     * <p>See {@link #Builder(Context, Renderer...)} for the list of default values.
     */
    final class Builder {

        private final Renderer[] renderers;

        private Clock clock;
        private TrackSelector trackSelector;
        private LoadControl loadControl;
        private BandwidthMeter bandwidthMeter;
        private Looper looper;
//        private AnalyticsCollector analyticsCollector;
        private boolean useLazyPreparation;
        private boolean buildCalled;

        /**
         * Creates a builder with a list of {@link Renderer Renderers}.
         *
         * <p>The builder uses the following default values:
         *
         * <ul>
         *   <li>{@link TrackSelector}: {@link DefaultTrackSelector}
         *   <li>{@link LoadControl}: {@link DefaultLoadControl}
         *   <li>{@link BandwidthMeter}: {@link DefaultBandwidthMeter#getSingletonInstance(Context)}
         *   <li>{@link Looper}: The {@link Looper} associated with the current thread, or the {@link
         *       Looper} of the application's main thread if the current thread doesn't have a {@link
         *       Looper}
         *   <li>{@link AnalyticsCollector}: {@link AnalyticsCollector} with {@link Clock#DEFAULT}
         *   <li>{@code useLazyPreparation}: {@code true}
         *   <li>{@link Clock}: {@link Clock#DEFAULT}
         * </ul>
         *
         * @param context A {@link Context}.
         * @param renderers The {@link Renderer Renderers} to be used by the player.
         */
        public Builder(Context context, Renderer... renderers) {
            this(
                    renderers,
                    new DefaultTrackSelector(context),
                    new DefaultLoadControl(),
                    DefaultBandwidthMeter.getSingletonInstance(context),
                    Util.getLooper(),
//                    new AnalyticsCollector(Clock.DEFAULT),
                    /* useLazyPreparation= */ true,
                    Clock.DEFAULT);
        }

        /**
         * Creates a builder with the specified custom components.
         *
         * <p>Note that this constructor is only useful if you try to ensure that ExoPlayer's default
         * components can be removed by ProGuard or R8. For most components except renderers, there is
         * only a marginal benefit of doing that.
         *
         * @param renderers The {@link Renderer Renderers} to be used by the player.
         * @param trackSelector A {@link TrackSelector}.
         * @param loadControl A {@link LoadControl}.
         * @param bandwidthMeter A {@link BandwidthMeter}.
         * @param looper A {@link Looper} that must be used for all calls to the player.
//         * @param analyticsCollector An {@link AnalyticsCollector}.
         * @param useLazyPreparation Whether media sources should be initialized lazily.
         * @param clock A {@link Clock}. Should always be {@link Clock#DEFAULT}.
         */
        public Builder(
                Renderer[] renderers,
                TrackSelector trackSelector,
                LoadControl loadControl,
                BandwidthMeter bandwidthMeter,
                Looper looper,
//                AnalyticsCollector analyticsCollector,
                boolean useLazyPreparation,
                Clock clock) {
            Assertions.checkArgument(renderers.length > 0);
            this.renderers = renderers;
            this.trackSelector = trackSelector;
            this.loadControl = loadControl;
            this.bandwidthMeter = bandwidthMeter;
            this.looper = looper;
//            this.analyticsCollector = analyticsCollector;
            this.useLazyPreparation = useLazyPreparation;
            this.clock = clock;
        }

        /**
         * Sets the {@link TrackSelector} that will be used by the player.
         *
         * @param trackSelector A {@link TrackSelector}.
         * @return This builder.
         * @throws IllegalStateException If {@link #build()} has already been called.
         */
        public Builder setTrackSelector(TrackSelector trackSelector) {
            Assertions.checkState(!buildCalled);
            this.trackSelector = trackSelector;
            return this;
        }

        /**
         * Sets the {@link LoadControl} that will be used by the player.
         *
         * @param loadControl A {@link LoadControl}.
         * @return This builder.
         * @throws IllegalStateException If {@link #build()} has already been called.
         */
        public Builder setLoadControl(LoadControl loadControl) {
            Assertions.checkState(!buildCalled);
            this.loadControl = loadControl;
            return this;
        }

        /**
         * Sets the {@link BandwidthMeter} that will be used by the player.
         *
         * @param bandwidthMeter A {@link BandwidthMeter}.
         * @return This builder.
         * @throws IllegalStateException If {@link #build()} has already been called.
         */
        public Builder setBandwidthMeter(BandwidthMeter bandwidthMeter) {
            Assertions.checkState(!buildCalled);
            this.bandwidthMeter = bandwidthMeter;
            return this;
        }

        /**
         * Sets the {@link Looper} that must be used for all calls to the player and that is used to
         * call listeners on.
         *
         * @param looper A {@link Looper}.
         * @return This builder.
         * @throws IllegalStateException If {@link #build()} has already been called.
         */
        public Builder setLooper(Looper looper) {
            Assertions.checkState(!buildCalled);
            this.looper = looper;
            return this;
        }

        /**
         * Sets the {@link AnalyticsCollector} that will collect and forward all player events.
         *
         * @param analyticsCollector An {@link AnalyticsCollector}.
         * @return This builder.
         * @throws IllegalStateException If {@link #build()} has already been called.
         */
        public Builder setAnalyticsCollector(AnalyticsCollector analyticsCollector) {
            Assertions.checkState(!buildCalled);
//            this.analyticsCollector = analyticsCollector;
            return this;
        }

        /**
         * Sets whether media sources should be initialized lazily.
         *
         * <p>If false, all initial preparation steps (e.g., manifest loads) happen immediately. If
         * true, these initial preparations are triggered only when the player starts buffering the
         * media.
         *
         * @param useLazyPreparation Whether to use lazy preparation.
         * @return This builder.
         * @throws IllegalStateException If {@link #build()} has already been called.
         */
        public Builder setUseLazyPreparation(boolean useLazyPreparation) {
            Assertions.checkState(!buildCalled);
            this.useLazyPreparation = useLazyPreparation;
            return this;
        }

        /**
         * Sets the {@link Clock} that will be used by the player. Should only be set for testing
         * purposes.
         *
         * @param clock A {@link Clock}.
         * @return This builder.
         * @throws IllegalStateException If {@link #build()} has already been called.
         */
        @VisibleForTesting
        public Builder setClock(Clock clock) {
            Assertions.checkState(!buildCalled);
            this.clock = clock;
            return this;
        }

        /**
         * Builds an {@link WowPlayer} instance.
         *
         * @throws IllegalStateException If {@link #build()} has already been called.
         */
        public WowPlayer build() {
            Assertions.checkState(!buildCalled);
            buildCalled = true;
            return new WowPlayerImpl(
                    renderers, trackSelector, loadControl, bandwidthMeter, clock, looper);
        }
    }

    /** Returns the {@link Looper} associated with the playback thread. */
    Looper getPlaybackLooper();

    /**
     * Retries a failed or stopped playback. Does nothing if the player has been reset, or if playback
     * has not failed or been stopped.
     */
    void retry();

    /**
     * Prepares the player to play the provided {@link MediaSource}. Equivalent to {@code
     * prepare(mediaSource, true, true)}.
     */
    void prepare(MediaSource mediaSource);

    /**
     * Prepares the player to play the provided {@link MediaSource}, optionally resetting the playback
     * position the default position in the first {@link Timeline.Window}.
     *
     * @param mediaSource The {@link MediaSource} to play.
     * @param resetPosition Whether the playback position should be reset to the default position in
     *     the first {@link Timeline.Window}. If false, playback will start from the position defined
     *     by {@link #getCurrentWindowIndex()} and {@link #getCurrentPosition()}.
     * @param resetState Whether the timeline, manifest, tracks and track selections should be reset.
     *     Should be true unless the player is being prepared to play the same media as it was playing
     *     previously (e.g. if playback failed and is being retried).
     */
    void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetState);

    /**
     * Creates a message that can be sent to a {@link PlayerMessage.Target}. By default, the message
     * will be delivered immediately without blocking on the playback thread. The default {@link
     * PlayerMessage#getType()} is 0 and the default {@link PlayerMessage#getPayload()} is null. If a
     * position is specified with {@link PlayerMessage#setPosition(long)}, the message will be
     * delivered at this position in the current window defined by {@link #getCurrentWindowIndex()}.
     * Alternatively, the message can be sent at a specific window using {@link
     * PlayerMessage#setPosition(int, long)}.
     */
    PlayerMessage createMessage(PlayerMessage.Target target);

    /**
     * Sets the parameters that control how seek operations are performed.
     *
     * @param seekParameters The seek parameters, or {@code null} to use the defaults.
     */
    void setSeekParameters(@Nullable SeekParameters seekParameters);

    /** Returns the currently active {@link SeekParameters} of the player. */
    SeekParameters getSeekParameters();

    /**
     * Sets whether the player is allowed to keep holding limited resources such as video decoders,
     * even when in the idle state. By doing so, the player may be able to reduce latency when
     * starting to play another piece of content for which the same resources are required.
     *
     * <p>This mode should be used with caution, since holding limited resources may prevent other
     * players of media components from acquiring them. It should only be enabled when <em>both</em>
     * of the following conditions are true:
     *
     * <ul>
     *   <li>The application that owns the player is in the foreground.
     *   <li>The player is used in a way that may benefit from foreground mode. For this to be true,
     *       the same player instance must be used to play multiple pieces of content, and there must
     *       be gaps between the playbacks (i.e. {@link #stop} is called to halt one playback, and
     *       {@link #prepare} is called some time later to start a new one).
     * </ul>
     *
     * <p>Note that foreground mode is <em>not</em> useful for switching between content without gaps
     * between the playbacks. For this use case {@link #stop} does not need to be called, and simply
     * calling {@link #prepare} for the new media will cause limited resources to be retained even if
     * foreground mode is not enabled.
     *
     * <p>If foreground mode is enabled, it's the application's responsibility to disable it when the
     * conditions described above no longer hold.
     *
     * @param foregroundMode Whether the player is allowed to keep limited resources even when in the
     *     idle state.
     */
    void setForegroundMode(boolean foregroundMode);
}
