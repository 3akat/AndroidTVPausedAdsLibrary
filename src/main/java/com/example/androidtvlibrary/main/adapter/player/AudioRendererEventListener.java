package com.example.androidtvlibrary.main.adapter.player;

import static com.example.androidtvlibrary.main.adapter.Util.castNonNull;

import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.Format;

public interface AudioRendererEventListener {

    /**
     * Called when the renderer is enabled.
     *
     * @param counters {@link DecoderCounters} that will be updated by the renderer for as long as it
     *     remains enabled.
     */
    default void onAudioEnabled(DecoderCounters counters) {}

    /**
     * Called when the audio session is set.
     *
     * @param audioSessionId The audio session id.
     */
    default void onAudioSessionId(int audioSessionId) {}

    /**
     * Called when a decoder is created.
     *
     * @param decoderName The decoder that was created.
     * @param initializedTimestampMs {@link SystemClock#elapsedRealtime()} when initialization
     *     finished.
     * @param initializationDurationMs The time taken to initialize the decoder in milliseconds.
     */
    default void onAudioDecoderInitialized(
            String decoderName, long initializedTimestampMs, long initializationDurationMs) {}

    /**
     * Called when the format of the media being consumed by the renderer changes.
     *
     * @param format The new format.
     */
    default void onAudioInputFormatChanged(Format format) {}


    default void onAudioSinkUnderrun(
            int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {}

    /**
     * Called when the renderer is disabled.
     *
     * @param counters {@link DecoderCounters} that were updated by the renderer.
     */
    default void onAudioDisabled(DecoderCounters counters) {}

    /**
     * Dispatches events to a {@link AudioRendererEventListener}.
     */
    final class EventDispatcher {

        @Nullable
        private final Handler handler;
        @Nullable private final AudioRendererEventListener listener;

        /**
         * @param handler A handler for dispatching events, or null if creating a dummy instance.
         * @param listener The listener to which events should be dispatched, or null if creating a
         *     dummy instance.
         */
        public EventDispatcher(@Nullable Handler handler,
                               @Nullable AudioRendererEventListener listener) {
            this.handler = listener != null ? Assertions.checkNotNull(handler) : null;
            this.listener = listener;
        }

        /**
         * Invokes {@link AudioRendererEventListener#onAudioEnabled(DecoderCounters)}.
         */
        public void enabled(final DecoderCounters decoderCounters) {
            if (handler != null) {
                handler.post(() -> castNonNull(listener).onAudioEnabled(decoderCounters));
            }
        }

        /**
         * Invokes {@link AudioRendererEventListener#onAudioDecoderInitialized(String, long, long)}.
         */
        public void decoderInitialized(final String decoderName,
                                       final long initializedTimestampMs, final long initializationDurationMs) {
            if (handler != null) {
                handler.post(
                        () ->
                                castNonNull(listener)
                                        .onAudioDecoderInitialized(
                                                decoderName, initializedTimestampMs, initializationDurationMs));
            }
        }

        /**
         * Invokes {@link AudioRendererEventListener#onAudioInputFormatChanged(Format)}.
         */
        public void inputFormatChanged(final Format format) {
            if (handler != null) {
                handler.post(() -> castNonNull(listener).onAudioInputFormatChanged(format));
            }
        }

        /**
         * Invokes {@link AudioRendererEventListener#onAudioSinkUnderrun(int, long, long)}.
         */
        public void audioTrackUnderrun(final int bufferSize, final long bufferSizeMs,
                                       final long elapsedSinceLastFeedMs) {
            if (handler != null) {
                handler.post(
                        () ->
                                castNonNull(listener)
                                        .onAudioSinkUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs));
            }
        }

        /**
         * Invokes {@link AudioRendererEventListener#onAudioDisabled(DecoderCounters)}.
         */
        public void disabled(final DecoderCounters counters) {
            counters.ensureUpdated();
            if (handler != null) {
                handler.post(
                        () -> {
                            counters.ensureUpdated();
                            castNonNull(listener).onAudioDisabled(counters);
                        });
            }
        }

        /**
         * Invokes {@link AudioRendererEventListener#onAudioSessionId(int)}.
         */
        public void audioSessionId(final int audioSessionId) {
            if (handler != null) {
                handler.post(() -> castNonNull(listener).onAudioSessionId(audioSessionId));
            }
        }
    }
}
