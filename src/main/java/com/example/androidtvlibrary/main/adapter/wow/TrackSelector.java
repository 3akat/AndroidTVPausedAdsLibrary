package com.example.androidtvlibrary.main.adapter.wow;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaPeriodId;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.BandwidthMeter;
import com.example.androidtvlibrary.main.adapter.Timeline;
import com.example.androidtvlibrary.main.adapter.TrackGroupArray;

public abstract class TrackSelector {

    /**
     * Notified when selections previously made by a {@link TrackSelector} are no longer valid.
     */
    public interface InvalidationListener {

        /**
         * Called by a {@link TrackSelector} to indicate that selections it has previously made are no
         * longer valid. May be called from any thread.
         */
        void onTrackSelectionsInvalidated();

    }

    @Nullable
    private InvalidationListener listener;
    @Nullable private BandwidthMeter bandwidthMeter;

    /**
     * Called by the player to initialize the selector.
     *
     * @param listener An invalidation listener that the selector can call to indicate that selections
     *     it has previously made are no longer valid.
     * @param bandwidthMeter A bandwidth meter which can be used by track selections to select tracks.
     */
    public final void init(InvalidationListener listener, BandwidthMeter bandwidthMeter) {
        this.listener = listener;
        this.bandwidthMeter = bandwidthMeter;
    }


    public abstract TrackSelectorResult selectTracks(
            RendererCapabilities[] rendererCapabilities,
            TrackGroupArray trackGroups,
            MediaSource.MediaPeriodId periodId,
            Timeline timeline)
            throws Exception;

    public abstract void onSelectionActivated(Object info);

    /**
     * Calls {@link InvalidationListener#onTrackSelectionsInvalidated()} to invalidate all previously
     * generated track selections.
     */
    protected final void invalidate() {
        if (listener != null) {
            listener.onTrackSelectionsInvalidated();
        }
    }

    /**
     * Returns a bandwidth meter which can be used by track selections to select tracks. Must only be
     * called after {@link #init(InvalidationListener, BandwidthMeter)} has been called.
     */
    protected final BandwidthMeter getBandwidthMeter() {
        return Assertions.checkNotNull(bandwidthMeter);
    }
}
