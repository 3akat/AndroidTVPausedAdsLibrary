package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.Media.ExtractorInput;
import com.example.androidtvlibrary.main.adapter.Media.SeekMap;

import java.io.IOException;

public interface OggSeeker {

    /**
     * Returns a {@link SeekMap} that returns an initial estimated position for progressive seeking
     * or the final position for direct seeking. Returns null if {@link #read} has yet to return -1.
     */
    SeekMap createSeekMap();

    /**
     * Starts a seek operation.
     *
     * @param targetGranule The target granule position.
     */
    void startSeek(long targetGranule);

    /**
     * Reads data from the {@link ExtractorInput} to build the {@link SeekMap} or to continue a seek.
     * <p/>
     * If more data is required or if the position of the input needs to be modified then a position
     * from which data should be provided is returned. Else a negative value is returned. If a seek
     * has been completed then the value returned is -(currentGranule + 2). Else it is -1.
     *
     * @param input The {@link ExtractorInput} to read from.
     * @return A non-negative position to seek the {@link ExtractorInput} to, or -(currentGranule + 2)
     *     if the progressive seek has completed, or -1 otherwise.
     * @throws IOException If reading from the {@link ExtractorInput} fails.
     * @throws InterruptedException If the thread is interrupted.
     */
    long read(ExtractorInput input) throws IOException, InterruptedException;

}
