package com.example.androidtvlibrary.main.adapter.wow;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

import com.example.androidtvlibrary.main.adapter.Format;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Renderer extends PlayerMessage.Target {

    /**
     * The renderer states. One of {@link #STATE_DISABLED}, {@link #STATE_ENABLED} or {@link
     * #STATE_STARTED}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_DISABLED, STATE_ENABLED, STATE_STARTED})
    @interface State {}
    /**
     * The renderer is disabled. A renderer in this state may hold resources that it requires for
     * rendering (e.g. media decoders), for use if it's subsequently enabled. {@link #reset()} can be
     * called to force the renderer to release these resources.
     */
    int STATE_DISABLED = 0;
    /**
     * The renderer is enabled but not started. A renderer in this state may render media at the
     * current position (e.g. an initial video frame), but the position will not advance. A renderer
     * in this state will typically hold resources that it requires for rendering (e.g. media
     * decoders).
     */
    int STATE_ENABLED = 1;
    /**
     * The renderer is started. Calls to {@link #render(long, long)} will cause media to be rendered.
     */
    int STATE_STARTED = 2;


    int getTrackType();

    /**
     * Returns the capabilities of the renderer.
     *
     * @return The capabilities of the renderer.
     */
    RendererCapabilities getCapabilities();

    /**
     * Sets the index of this renderer within the player.
     *
     * @param index The renderer index.
     */
    void setIndex(int index);

    /**
     * If the renderer advances its own playback position then this method returns a corresponding
     * {@link MediaClock}. If provided, the player will use the returned {@link MediaClock} as its
     * source of time during playback. A player may have at most one renderer that returns a {@link
     * MediaClock} from this method.
     *
     * @return The {@link MediaClock} tracking the playback position of the renderer, or null.
     */
    @Nullable
    MediaClock getMediaClock();

    /**
     * Returns the current state of the renderer.
     *
     * @return The current state. One of {@link #STATE_DISABLED}, {@link #STATE_ENABLED} and {@link
     *     #STATE_STARTED}.
     */
    @State
    int getState();

    /**
     * Enables the renderer to consume from the specified {@link SampleStream}.
     * <p>
     * This method may be called when the renderer is in the following states:
     * {@link #STATE_DISABLED}.
     *
     * @param configuration The renderer configuration.
     * @param formats The enabled formats.
     * @param stream The {@link SampleStream} from which the renderer should consume.
     * @param positionUs The player's current position.
     * @param joining Whether this renderer is being enabled to join an ongoing playback.
     * @param offsetUs The offset to be added to timestamps of buffers read from {@code stream}
     *     before they are rendered.
     * @throws Exception If an error occurs.
     */
    void enable(RendererConfiguration configuration, Format[] formats, SampleStream stream,
                long positionUs, boolean joining, long offsetUs) throws Exception;

    /**
     * Starts the renderer, meaning that calls to {@link #render(long, long)} will cause media to be
     * rendered.
     * <p>
     * This method may be called when the renderer is in the following states:
     * {@link #STATE_ENABLED}.
     *
     * @throws Exception If an error occurs.
     */
    void start() throws Exception;

    /**
     * Replaces the {@link SampleStream} from which samples will be consumed.
     * <p>
     * This method may be called when the renderer is in the following states:
     * {@link #STATE_ENABLED}, {@link #STATE_STARTED}.
     *
     * @param formats The enabled formats.
     * @param stream The {@link SampleStream} from which the renderer should consume.
     * @param offsetUs The offset to be added to timestamps of buffers read from {@code stream} before
     *     they are rendered.
     * @throws Exception If an error occurs.
     */
    void replaceStream(Format[] formats, SampleStream stream, long offsetUs)
            throws Exception;

    /** Returns the {@link SampleStream} being consumed, or null if the renderer is disabled. */
    @Nullable
    SampleStream getStream();

    /**
     * Returns whether the renderer has read the current {@link SampleStream} to the end.
     * <p>
     * This method may be called when the renderer is in the following states:
     * {@link #STATE_ENABLED}, {@link #STATE_STARTED}.
     */
    boolean hasReadStreamToEnd();


    long getReadingPositionUs();

    /**
     * Signals to the renderer that the current {@link SampleStream} will be the final one supplied
     * before it is next disabled or reset.
     * <p>
     * This method may be called when the renderer is in the following states:
     * {@link #STATE_ENABLED}, {@link #STATE_STARTED}.
     */
    void setCurrentStreamFinal();


    boolean isCurrentStreamFinal();

    void maybeThrowStreamError() throws IOException;


    void resetPosition(long positionUs) throws Exception;

    default void setOperatingRate(float operatingRate) throws Exception {}

    void render(long positionUs, long elapsedRealtimeUs) throws Exception;

    /**
     * Whether the renderer is able to immediately render media from the current position.
     * <p>
     * If the renderer is in the {@link #STATE_STARTED} state then returning true indicates that the
     * renderer has everything that it needs to continue playback. Returning false indicates that
     * the player should pause until the renderer is ready.
     * <p>
     * If the renderer is in the {@link #STATE_ENABLED} state then returning true indicates that the
     * renderer is ready for playback to be started. Returning false indicates that it is not.
     * <p>
     * This method may be called when the renderer is in the following states:
     * {@link #STATE_ENABLED}, {@link #STATE_STARTED}.
     *
     * @return Whether the renderer is ready to render media.
     */
    boolean isReady();


    boolean isEnded();


    void stop() throws Exception;

    /**
     * Disable the renderer, transitioning it to the {@link #STATE_DISABLED} state.
     * <p>
     * This method may be called when the renderer is in the following states:
     * {@link #STATE_ENABLED}.
     */
    void disable();

    /**
     * Forces the renderer to give up any resources (e.g. media decoders) that it may be holding. If
     * the renderer is not holding any resources, the call is a no-op.
     *
     * <p>This method may be called when the renderer is in the following states: {@link
     * #STATE_DISABLED}.
     */
    void reset();
}
