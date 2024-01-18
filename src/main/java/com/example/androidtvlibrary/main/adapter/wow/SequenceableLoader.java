package com.example.androidtvlibrary.main.adapter.wow;

import com.example.androidtvlibrary.main.adapter.C;

public interface SequenceableLoader {

    /**
     * A callback to be notified of {@link SequenceableLoader} events.
     */
    interface Callback<T extends SequenceableLoader> {

        /**
         * Called by the loader to indicate that it wishes for its {@link #continueLoading(long)} method
         * to be called when it can continue to load data. Called on the playback thread.
         */
        void onContinueLoadingRequested(T source);

    }

    /**
     * Returns an estimate of the position up to which data is buffered.
     *
     * @return An estimate of the absolute position in microseconds up to which data is buffered, or
     *     {@link C#TIME_END_OF_SOURCE} if the data is fully buffered.
     */
    long getBufferedPositionUs();

    /**
     * Returns the next load time, or {@link C#TIME_END_OF_SOURCE} if loading has finished.
     */
    long getNextLoadPositionUs();

    /**
     * Attempts to continue loading.
     *
     * @param positionUs The current playback position in microseconds. If playback of the period to
     *     which this loader belongs has not yet started, the value will be the starting position
     *     in the period minus the duration of any media in previous periods still to be played.
     * @return True if progress was made, meaning that {@link #getNextLoadPositionUs()} will return
     *     a different value than prior to the call. False otherwise.
     */
    boolean continueLoading(long positionUs);

    /** Returns whether the loader is currently loading. */
    boolean isLoading();

    /**
     * Re-evaluates the buffer given the playback position.
     *
     * <p>Re-evaluation may discard buffered media so that it can be re-buffered in a different
     * quality.
     *
     * @param positionUs The current playback position in microseconds. If playback of this period has
     *     not yet started, the value will be the starting position in this period minus the duration
     *     of any media in previous periods still to be played.
     */
    void reevaluateBuffer(long positionUs);
}
