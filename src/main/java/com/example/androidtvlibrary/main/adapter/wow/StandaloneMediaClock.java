package com.example.androidtvlibrary.main.adapter.wow;

import com.example.androidtvlibrary.main.adapter.C;

public final class StandaloneMediaClock implements MediaClock {

    private final Clock clock;

    private boolean started;
    private long baseUs;
    private long baseElapsedMs;
    private PlaybackParameters playbackParameters;

    /**
     * Creates a new standalone media clock using the given {@link Clock} implementation.
     *
     * @param clock A {@link Clock}.
     */
    public StandaloneMediaClock(Clock clock) {
        this.clock = clock;
        this.playbackParameters = PlaybackParameters.DEFAULT;
    }

    /**
     * Starts the clock. Does nothing if the clock is already started.
     */
    public void start() {
        if (!started) {
            baseElapsedMs = clock.elapsedRealtime();
            started = true;
        }
    }

    /**
     * Stops the clock. Does nothing if the clock is already stopped.
     */
    public void stop() {
        if (started) {
            resetPosition(getPositionUs());
            started = false;
        }
    }

    /**
     * Resets the clock's position.
     *
     * @param positionUs The position to set in microseconds.
     */
    public void resetPosition(long positionUs) {
        baseUs = positionUs;
        if (started) {
            baseElapsedMs = clock.elapsedRealtime();
        }
    }

    @Override
    public long getPositionUs() {
        long positionUs = baseUs;
        if (started) {
            long elapsedSinceBaseMs = clock.elapsedRealtime() - baseElapsedMs;
            if (playbackParameters.speed == 1f) {
                positionUs += C.msToUs(elapsedSinceBaseMs);
            } else {
                positionUs += playbackParameters.getMediaTimeUsForPlayoutTimeMs(elapsedSinceBaseMs);
            }
        }
        return positionUs;
    }

    @Override
    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        // Store the current position as the new base, in case the playback speed has changed.
        if (started) {
            resetPosition(getPositionUs());
        }
        this.playbackParameters = playbackParameters;
    }

    @Override
    public PlaybackParameters getPlaybackParameters() {
        return playbackParameters;
    }

}
