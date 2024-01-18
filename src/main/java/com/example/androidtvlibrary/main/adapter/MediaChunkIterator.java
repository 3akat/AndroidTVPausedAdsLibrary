package com.example.androidtvlibrary.main.adapter;

import java.util.NoSuchElementException;

public interface MediaChunkIterator {

    /** An empty media chunk iterator without available data. */
    MediaChunkIterator EMPTY =
            new MediaChunkIterator() {
                @Override
                public boolean isEnded() {
                    return true;
                }

                @Override
                public boolean next() {
                    return false;
                }

                @Override
                public DataSpec getDataSpec() {
                    throw new NoSuchElementException();
                }

                @Override
                public long getChunkStartTimeUs() {
                    throw new NoSuchElementException();
                }

                @Override
                public long getChunkEndTimeUs() {
                    throw new NoSuchElementException();
                }

                @Override
                public void reset() {
                    // Do nothing.
                }
            };

    /** Returns whether the iteration has reached the end of the available data. */
    boolean isEnded();

    /**
     * Moves the iterator to the next media chunk.
     *
     * <p>Check the return value or {@link #isEnded()} to determine whether the iterator reached the
     * end of the available data.
     *
     * @return Whether the iterator points to a media chunk with available data.
     */
    boolean next();

    /**
     * Returns the {@link DataSpec} used to load the media chunk.
     *
     * @throws java.util.NoSuchElementException If the method is called before the first call to
     *     {@link #next()} or when {@link #isEnded()} is true.
     */
    DataSpec getDataSpec();

    /**
     * Returns the media start time of the chunk, in microseconds.
     *
     * @throws java.util.NoSuchElementException If the method is called before the first call to
     *     {@link #next()} or when {@link #isEnded()} is true.
     */
    long getChunkStartTimeUs();

    /**
     * Returns the media end time of the chunk, in microseconds.
     *
     * @throws java.util.NoSuchElementException If the method is called before the first call to
     *     {@link #next()} or when {@link #isEnded()} is true.
     */
    long getChunkEndTimeUs();

    /** Resets the iterator to the initial position. */
    void reset();
}
