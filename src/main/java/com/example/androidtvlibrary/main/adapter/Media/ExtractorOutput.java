package com.example.androidtvlibrary.main.adapter.Media;

public interface ExtractorOutput {

    TrackOutput track(int id, int type);

    /**
     * Called when all tracks have been identified, meaning no new {@code trackId} values will be
     * passed to {@link #track(int, int)}.
     */
    void endTracks();

    /**
     * Called when a {@link SeekMap} has been extracted from the stream.
     *
     * @param seekMap The extracted {@link SeekMap}.
     */
    void seekMap(SeekMap seekMap);

}