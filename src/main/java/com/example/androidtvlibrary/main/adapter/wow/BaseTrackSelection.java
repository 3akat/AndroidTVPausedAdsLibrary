package com.example.androidtvlibrary.main.adapter.wow;

import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.media3.common.Format;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.MediaChunk;
import com.example.androidtvlibrary.main.adapter.TrackGroup;
import com.example.androidtvlibrary.main.adapter.TrackSelection;
import com.example.androidtvlibrary.main.adapter.Util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class BaseTrackSelection implements TrackSelection {

    /**
     * The selected {@link TrackGroup}.
     */
    protected final TrackGroup group;
    /**
     * The number of selected tracks within the {@link TrackGroup}. Always greater than zero.
     */
    protected final int length;
    /**
     * The indices of the selected tracks in {@link #group}, in order of decreasing bandwidth.
     */
    protected final int[] tracks;

    /**
     * The {@link Format}s of the selected tracks, in order of decreasing bandwidth.
     */
    private final com.example.androidtvlibrary.main.adapter.Format[] formats;
    /**
     * Selected track blacklist timestamps, in order of decreasing bandwidth.
     */
    private final long[] blacklistUntilTimes;

    // Lazily initialized hashcode.
    private int hashCode;

    /**
     * @param group The {@link TrackGroup}. Must not be null.
     * @param tracks The indices of the selected tracks within the {@link TrackGroup}. Must not be
     *     null or empty. May be in any order.
     */
    public BaseTrackSelection(TrackGroup group, int... tracks) {
        //todo
//        Assertions.checkState(tracks.length > 0);
        this.group = Assertions.checkNotNull(group);
        this.length = tracks.length;
        // Set the formats, sorted in order of decreasing bandwidth.
        formats = new com.example.androidtvlibrary.main.adapter.Format[length];
        for (int i = 0; i < tracks.length; i++) {
            formats[i] = group.getFormat(tracks[i]);
        }
        Arrays.sort(formats, new DecreasingBandwidthComparator());
        // Set the format indices in the same order.
        this.tracks = new int[length];
        for (int i = 0; i < length; i++) {
            this.tracks[i] = group.indexOf(formats[i]);
        }
        blacklistUntilTimes = new long[length];
    }

    @Override
    public void enable() {
        // Do nothing.
    }

    @Override
    public void disable() {
        // Do nothing.
    }

    @Override
    public final TrackGroup getTrackGroup() {
        return group;
    }

    @Override
    public final int length() {
        return tracks.length;
    }

    @Override
    public final com.example.androidtvlibrary.main.adapter.Format getFormat(int index) {
        return formats[index];
    }

    @Override
    public final int getIndexInTrackGroup(int index) {
        return tracks[index];
    }

    @Override
    @SuppressWarnings("ReferenceEquality")
    public final int indexOf(com.example.androidtvlibrary.main.adapter.Format format) {
        for (int i = 0; i < length; i++) {
            if (formats[i] == format) {
                return i;
            }
        }
        return C.INDEX_UNSET;
    }

    @Override
    public final int indexOf(int indexInTrackGroup) {
        for (int i = 0; i < length; i++) {
            if (tracks[i] == indexInTrackGroup) {
                return i;
            }
        }
        return C.INDEX_UNSET;
    }

    @Override
    public final com.example.androidtvlibrary.main.adapter.Format getSelectedFormat() {
        return formats[getSelectedIndex()];
    }

    @Override
    public final int getSelectedIndexInTrackGroup() {
        return tracks[getSelectedIndex()];
    }

    @Override
    public void onPlaybackSpeed(float playbackSpeed) {
        // Do nothing.
    }

    @Override
    public int evaluateQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        return queue.size();
    }

    @Override
    public final boolean blacklist(int index, long blacklistDurationMs) {
        long nowMs = SystemClock.elapsedRealtime();
        boolean canBlacklist = isBlacklisted(index, nowMs);
        for (int i = 0; i < length && !canBlacklist; i++) {
            canBlacklist = i != index && !isBlacklisted(i, nowMs);
        }
        if (!canBlacklist) {
            return false;
        }
        blacklistUntilTimes[index] =
                Math.max(
                        blacklistUntilTimes[index],
                        Util.addWithOverflowDefault(nowMs, blacklistDurationMs, Long.MAX_VALUE));
        return true;
    }

    /**
     * Returns whether the track at the specified index in the selection is blacklisted.
     *
     * @param index The index of the track in the selection.
     * @param nowMs The current time in the timebase of {@link SystemClock#elapsedRealtime()}.
     */
    protected final boolean isBlacklisted(int index, long nowMs) {
        return blacklistUntilTimes[index] > nowMs;
    }

    // Object overrides.

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = 31 * System.identityHashCode(group) + Arrays.hashCode(tracks);
        }
        return hashCode;
    }

    // Track groups are compared by identity not value, as distinct groups may have the same value.
    @Override
    @SuppressWarnings({"ReferenceEquality", "EqualsGetClass"})
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BaseTrackSelection other = (BaseTrackSelection) obj;
        return group == other.group && Arrays.equals(tracks, other.tracks);
    }


    private static final class DecreasingBandwidthComparator implements Comparator<com.example.androidtvlibrary.main.adapter.Format> {

        @Override
        public int compare(com.example.androidtvlibrary.main.adapter.Format a, com.example.androidtvlibrary.main.adapter.Format b) {
            return b.bitrate - a.bitrate;
        }

    }

}

