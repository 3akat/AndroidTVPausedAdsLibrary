package com.example.androidtvlibrary.main.adapter.mp3;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Media.SeekMap;

public interface Seeker extends SeekMap {

    /**
     * Maps a position (byte offset) to a corresponding sample timestamp.
     *
     * @param position A seek position (byte offset) relative to the start of the stream.
     * @return The corresponding timestamp of the next sample to be read, in microseconds.
     */
    long getTimeUs(long position);

    /**
     * Returns the position (byte offset) in the stream that is immediately after audio data, or
     * {@link C#POSITION_UNSET} if not known.
     */
    long getDataEndPosition();

    /** A {@link Seeker} that does not support seeking through audio data. */
    /* package */ class UnseekableSeeker extends SeekMap.Unseekable implements Seeker {

        public UnseekableSeeker() {
            super(/* durationUs= */ C.TIME_UNSET);
        }

        @Override
        public long getTimeUs(long position) {
            return 0;
        }

        @Override
        public long getDataEndPosition() {
            // Position unset as we do not know the data end position. Note that returning 0 doesn't work.
            return C.POSITION_UNSET;
        }
    }
}
