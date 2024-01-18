package com.example.androidtvlibrary.main.adapter.wow;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Assertions;

public final class SeekParameters {

    /** Parameters for exact seeking. */
    public static final SeekParameters EXACT = new SeekParameters(0, 0);
    /** Parameters for seeking to the closest sync point. */
    public static final SeekParameters CLOSEST_SYNC =
            new SeekParameters(Long.MAX_VALUE, Long.MAX_VALUE);
    /** Parameters for seeking to the sync point immediately before a requested seek position. */
    public static final SeekParameters PREVIOUS_SYNC = new SeekParameters(Long.MAX_VALUE, 0);
    /** Parameters for seeking to the sync point immediately after a requested seek position. */
    public static final SeekParameters NEXT_SYNC = new SeekParameters(0, Long.MAX_VALUE);
    /** Default parameters. */
    public static final SeekParameters DEFAULT = EXACT;

    /**
     * The maximum time that the actual position seeked to may precede the requested seek position, in
     * microseconds.
     */
    public final long toleranceBeforeUs;
    /**
     * The maximum time that the actual position seeked to may exceed the requested seek position, in
     * microseconds.
     */
    public final long toleranceAfterUs;

    /**
     * @param toleranceBeforeUs The maximum time that the actual position seeked to may precede the
     *     requested seek position, in microseconds. Must be non-negative.
     * @param toleranceAfterUs The maximum time that the actual position seeked to may exceed the
     *     requested seek position, in microseconds. Must be non-negative.
     */
    public SeekParameters(long toleranceBeforeUs, long toleranceAfterUs) {
        Assertions.checkArgument(toleranceBeforeUs >= 0);
        Assertions.checkArgument(toleranceAfterUs >= 0);
        this.toleranceBeforeUs = toleranceBeforeUs;
        this.toleranceAfterUs = toleranceAfterUs;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SeekParameters other = (SeekParameters) obj;
        return toleranceBeforeUs == other.toleranceBeforeUs
                && toleranceAfterUs == other.toleranceAfterUs;
    }

    @Override
    public int hashCode() {
        return (31 * (int) toleranceBeforeUs) + (int) toleranceAfterUs;
    }
}
