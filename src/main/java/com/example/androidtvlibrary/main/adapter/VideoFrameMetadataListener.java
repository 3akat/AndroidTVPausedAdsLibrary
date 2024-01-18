package com.example.androidtvlibrary.main.adapter;

import android.media.MediaFormat;

import androidx.annotation.Nullable;

public interface VideoFrameMetadataListener {
    /**
     * Called when the video frame about to be rendered. This method is called on the playback thread.
     *
     * @param presentationTimeUs The presentation time of the output buffer, in microseconds.
     * @param releaseTimeNs The wallclock time at which the frame should be displayed, in nanoseconds.
     *     If the platform API version of the device is less than 21, then this is the best effort.
     * @param format The format associated with the frame.
     * @param mediaFormat The framework media format associated with the frame, or {@code null} if not
     *     known or not applicable (e.g., because the frame was not output by a {@link
     *     android.media.MediaCodec MediaCodec}).
     */
    void onVideoFrameAboutToBeRendered(
            long presentationTimeUs,
            long releaseTimeNs,
            Format format,
            @Nullable MediaFormat mediaFormat);
}
