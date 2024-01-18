package com.example.androidtvlibrary.main.adapter;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.util.List;

public interface TrackSelection {

    @IntDef(
            open = true,
            value = {TYPE_UNSET})
    @interface Type {}
    /** An unspecified track selection type. */
    int TYPE_UNSET = 0;
    /** The first value that can be used for application specific track selection types. */
    int TYPE_CUSTOM_BASE = 10000;

    /**
     * Returns an integer specifying the type of the selection, or {@link #TYPE_UNSET} if not
     * specified.
     *
     * <p>Track selection types are specific to individual applications, but should be defined
     * starting from {@link #TYPE_CUSTOM_BASE} to ensure they don't conflict with any types that may
     * be added to the library in the future.
     */
    @Type
    int getType();

    public static final int SELECTION_REASON_UNKNOWN = 0;

    /** Contains of a subset of selected tracks belonging to a {@link TrackGroup}. */
    final class Definition {
        /** The {@link TrackGroup} which tracks belong to. */
        public final TrackGroup group;
        /** The indices of the selected tracks in {@link #group}. */
        public final int[] tracks;
        /** The track selection reason. One of the {@link C} SELECTION_REASON_ constants. */
        public final int reason;
        /** Optional data associated with this selection of tracks. */
        @Nullable
        public final Object data;

        /**
         * @param group The {@link TrackGroup}. Must not be null.
         * @param tracks The indices of the selected tracks within the {@link TrackGroup}. Must not be
         *     null or empty. May be in any order.
         */
        public Definition(TrackGroup group, int... tracks) {
            this(group, tracks, SELECTION_REASON_UNKNOWN, /* data= */ null);
        }

        /**
         * @param group The {@link TrackGroup}. Must not be null.
         * @param tracks The indices of the selected tracks within the {@link TrackGroup}. Must not be
         * @param reason The track selection reason. One of the {@link C} SELECTION_REASON_ constants.
         * @param data Optional data associated with this selection of tracks.
         */
        public Definition(TrackGroup group, int[] tracks, int reason, @Nullable Object data) {
            this.group = group;
            this.tracks = tracks;
            this.reason = reason;
            this.data = data;
        }
    }

    /**
     * Factory for {@link TrackSelection} instances.
     */
    interface Factory {



        TrackSelection[] createTrackSelections(
                 Definition[] definitions, BandwidthMeter bandwidthMeter);
    }


    void enable();


    void disable();

    /**
     * Returns the {@link TrackGroup} to which the selected tracks belong.
     */
    TrackGroup getTrackGroup();

    // Static subset of selected tracks.

    /**
     * Returns the number of tracks in the selection.
     */
    int length();

    /**
     * Returns the format of the track at a given index in the selection.
     *
     * @param index The index in the selection.
     * @return The format of the selected track.
     */
    Format getFormat(int index);

    /**
     * Returns the index in the track group of the track at a given index in the selection.
     *
     * @param index The index in the selection.
     * @return The index of the selected track.
     */
    int getIndexInTrackGroup(int index);

    /**
     * Returns the index in the selection of the track with the specified format. The format is
     * located by identity so, for example, {@code selection.indexOf(selection.getFormat(index)) ==
     * index} even if multiple selected tracks have formats that contain the same values.
     *
     * @param format The format.
     * @return The index in the selection, or {@link C#INDEX_UNSET} if the track with the specified
     *     format is not part of the selection.
     */
    int indexOf(Format format);

    /**
     * Returns the index in the selection of the track with the specified index in the track group.
     *
     * @param indexInTrackGroup The index in the track group.
     * @return The index in the selection, or {@link C#INDEX_UNSET} if the track with the specified
     *     index is not part of the selection.
     */
    int indexOf(int indexInTrackGroup);

    // Individual selected track.

    /**
     * Returns the {@link Format} of the individual selected track.
     */
    Format getSelectedFormat();

    /**
     * Returns the index in the track group of the individual selected track.
     */
    int getSelectedIndexInTrackGroup();

    /**
     * Returns the index of the selected track.
     */
    int getSelectedIndex();

    /**
     * Returns the reason for the current track selection.
     */
    int getSelectionReason();

    /** Returns optional data associated with the current track selection. */
    @Nullable Object getSelectionData();

    // Adaptation.

    /**
     * Called to notify the selection of the current playback speed. The playback speed may affect
     * adaptive track selection.
     *
     * @param speed The playback speed.
     */
    void onPlaybackSpeed(float speed);

    /**
     * Called to notify the selection of a position discontinuity.
     *
     * <p>This happens when the playback position jumps, e.g., as a result of a seek being performed.
     */
    default void onDiscontinuity() {}


    void updateSelectedTrack(
            long playbackPositionUs,
            long bufferedDurationUs,
            long availableDurationUs,
            List<? extends MediaChunk> queue,
            MediaChunkIterator[] mediaChunkIterators);

    /**
     * May be called periodically by sources that load media in discrete {@link MediaChunk}s and
     * support discarding of buffered chunks in order to re-buffer using a different selected track.
     * Returns the number of chunks that should be retained in the queue.
     * <p>
     * To avoid excessive re-buffering, implementations should normally return the size of the queue.
     * An example of a case where a smaller value may be returned is if network conditions have
     * improved dramatically, allowing chunks to be discarded and re-buffered in a track of
     * significantly higher quality. Discarding chunks may allow faster switching to a higher quality
     * track in this case. This method may only be called when the selection is enabled.
     *
     * @param playbackPositionUs The current playback position in microseconds. If playback of the
     *     period to which this track selection belongs has not yet started, the value will be the
     *     starting position in the period minus the duration of any media in previous periods still
     *     to be played.
     * @param queue The queue of buffered {@link MediaChunk}s. Must not be modified.
     * @return The number of chunks to retain in the queue.
     */
    int evaluateQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue);


    boolean blacklist(int index, long blacklistDurationMs);
}
