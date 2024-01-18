package com.example.androidtvlibrary.main.adapter.mp3;

import com.example.androidtvlibrary.main.adapter.C;

public final class ConstantBitrateSeeker extends ConstantBitrateSeekMap implements Seeker {

    /**
     * @param inputLength The length of the stream in bytes, or {@link C#LENGTH_UNSET} if unknown.
     * @param firstFramePosition The position of the first frame in the stream.
     * @param mpegAudioHeader The MPEG audio header associated with the first frame.
     */
    public ConstantBitrateSeeker(
            long inputLength, long firstFramePosition, MpegAudioHeader mpegAudioHeader) {
        super(inputLength, firstFramePosition, mpegAudioHeader.bitrate, mpegAudioHeader.frameSize);
    }

    @Override
    public long getTimeUs(long position) {
        return getTimeUsAtPosition(position);
    }

    @Override
    public long getDataEndPosition() {
        return C.POSITION_UNSET;
    }
}
