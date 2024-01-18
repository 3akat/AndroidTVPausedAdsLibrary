package com.example.androidtvlibrary.main.adapter.wow;

import java.io.IOException;

public interface SampleStream {

    boolean isReady();

    /**
     * Throws an error that's preventing data from being read. Does nothing if no such error exists.
     *
     * @throws IOException The underlying error.
     */
    void maybeThrowError() throws IOException;

    int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired);

    /**
     * Attempts to skip to the keyframe before the specified position, or to the end of the stream if
     * {@code positionUs} is beyond it.
     *
     * @param positionUs The specified time.
     * @return The number of samples that were skipped.
     */
    int skipData(long positionUs);

}
