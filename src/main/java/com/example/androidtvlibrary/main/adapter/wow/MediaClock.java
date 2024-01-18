package com.example.androidtvlibrary.main.adapter.wow;

public interface MediaClock {

    /**
     * Returns the current media position in microseconds.
     */
    long getPositionUs();

    /**
     * Attempts to set the playback parameters. The media clock may override these parameters if they
     * are not supported.
     *
     * @param playbackParameters The playback parameters to attempt to set.
     */
    void setPlaybackParameters(PlaybackParameters playbackParameters);

    /**
     * Returns the active playback parameters.
     */
    PlaybackParameters getPlaybackParameters();

}
