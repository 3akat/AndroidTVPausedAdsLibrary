package com.example.androidtvlibrary.main.adapter.wow;

import static androidx.media3.common.C.*;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.MediaChunk;
import com.example.androidtvlibrary.main.adapter.MediaChunkIterator;
import com.example.androidtvlibrary.main.adapter.TrackGroup;
import com.example.androidtvlibrary.main.adapter.TrackSelection;

import java.util.List;

public final class FixedTrackSelection extends BaseTrackSelection {

    private final @SelectionReason int reason;
    @Nullable private final Object data;

    /**
     * @param group The {@link TrackGroup}. Must not be null.
     * @param track The index of the selected track within the {@link TrackGroup}.
     */
    public FixedTrackSelection(TrackGroup group, int track) {
        this(group, /* track= */ track, /* type= */ TrackSelection.TYPE_UNSET);
    }

    /**
     * @param group The {@link TrackGroup}. Must not be null.
     * @param track The index of the selected track within the {@link TrackGroup}.
     * @param type The type that will be returned from {@link TrackSelection#getType()}.
     */
    public FixedTrackSelection(TrackGroup group, int track, @Type int type) {
        this(group, track, type, C.SELECTION_REASON_UNKNOWN, /* data= */ null);
    }

    /**
     * @param group The {@link TrackGroup}. Must not be null.
     * @param track The index of the selected track within the {@link TrackGroup}.
     * @param type The type that will be returned from {@link TrackSelection#getType()}.
     * @param reason A reason for the track selection.
     * @param data Optional data associated with the track selection.
     */
    public FixedTrackSelection(
            TrackGroup group,
            int track,
            @Type int type,
            @SelectionReason int reason,
            @Nullable Object data) {
        super(group);
        this.reason = reason;
        this.data = data;
    }

    @Override
    public void updateSelectedTrack(
            long playbackPositionUs,
            long bufferedDurationUs,
            long availableDurationUs,
            List<? extends MediaChunk> queue,
            MediaChunkIterator[] mediaChunkIterators) {
        // Do nothing.
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getSelectedIndex() {
        return 0;
    }

    @Override
    public @SelectionReason int getSelectionReason() {
        return reason;
    }

    @Override
    @Nullable
    public Object getSelectionData() {
        return data;
    }
}
