package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.Media.ExtractorOutput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.ParserException;
import com.example.androidtvlibrary.main.adapter.Media.TrackOutput;

public interface ElementaryStreamReader {

    /**
     * Notifies the reader that a seek has occurred.
     */
    void seek();

    /**
     * Initializes the reader by providing outputs and ids for the tracks.
     *
     * @param extractorOutput The {@link ExtractorOutput} that receives the extracted data.
     * @param idGenerator A {@link PesReader.TrackIdGenerator} that generates unique track ids for the
     *     {@link TrackOutput}s.
     */
    void createTracks(ExtractorOutput extractorOutput, PesReader.TrackIdGenerator idGenerator);

    /**
     * Called when a packet starts.
     *
     * @param pesTimeUs The timestamp associated with the packet.
     * @param flags See {@link TsPayloadReader.Flags}.
     */
    void packetStarted(long pesTimeUs, @TsPayloadReader.Flags int flags);

    /**
     * Consumes (possibly partial) data from the current packet.
     *
     * @param data The data to consume.
     * @throws ParserException If the data could not be parsed.
     */
    void consume(ParsableByteArray data) throws ParserException;

    /**
     * Called when a packet ends.
     */
    void packetFinished();

}
