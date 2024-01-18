package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.Media.ExtractorInput;
import com.example.androidtvlibrary.main.adapter.Media.ParserException;

import java.io.IOException;

public interface EbmlReader {

    /**
     * Initializes the extractor with an {@link EbmlProcessor}.
     *
     * @param processor An {@link EbmlProcessor} to process events.
     */
    void init(EbmlProcessor processor);

    /**
     * Resets the state of the reader.
     * <p>
     * Subsequent calls to {@link #read(ExtractorInput)} will start reading a new EBML structure
     * from scratch.
     */
    void reset();

    /**
     * Reads from an {@link ExtractorInput}, invoking an event callback if possible.
     *
     * @param input The {@link ExtractorInput} from which data should be read.
     * @return True if data can continue to be read. False if the end of the input was encountered.
     * @throws ParserException If parsing fails.
     * @throws IOException If an error occurs reading from the input.
     * @throws InterruptedException If the thread is interrupted.
     */
    boolean read(ExtractorInput input) throws IOException, InterruptedException;

}
