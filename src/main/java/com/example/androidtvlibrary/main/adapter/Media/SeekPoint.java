package com.example.androidtvlibrary.main.adapter.Media;

import androidx.annotation.Nullable;

public final class SeekPoint {

    /** A {@link SeekPoint} whose time and byte offset are both set to 0. */
    public static final SeekPoint START = new SeekPoint(0, 0);

    /** The time of the seek point, in microseconds. */
    public final long timeUs;

    /** The byte offset of the seek point. */
    public final long position;

    /**
     * @param timeUs The time of the seek point, in microseconds.
     * @param position The byte offset of the seek point.
     */
    public SeekPoint(long timeUs, long position) {
        this.timeUs = timeUs;
        this.position = position;
    }

    @Override
    public String toString() {
        return "[timeUs=" + timeUs + ", position=" + position + "]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SeekPoint other = (SeekPoint) obj;
        return timeUs == other.timeUs && position == other.position;
    }

    @Override
    public int hashCode() {
        int result = (int) timeUs;
        result = 31 * result + (int) position;
        return result;
    }
}
