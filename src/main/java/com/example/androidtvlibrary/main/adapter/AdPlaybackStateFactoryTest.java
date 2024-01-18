package com.example.androidtvlibrary.main.adapter;

import androidx.media3.common.AdPlaybackState;
import androidx.media3.common.C;

import java.util.Arrays;
import java.util.List;

/* package */ final class AdPlaybackStateFactoryTest {
    private AdPlaybackStateFactoryTest() {}

    public static final long MICROS_PER_SECOND = 1000000L;

    /**
     * Construct an {@link AdPlaybackState} from the provided {@code cuePoints}.
     *
     * @param cuePoints The cue points of the ads in seconds.
     * @return The {@link AdPlaybackState}.
     */
    public static AdPlaybackStateTest fromCuePoints(List<Float> cuePoints) {
        if (cuePoints.isEmpty()) {
            // If no cue points are specified, there is a preroll ad.
            return new AdPlaybackStateTest(/* adGroupTimesUs...= */ 0);
        }

        int count = cuePoints.size();
        long[] adGroupTimesUs = new long[count];
        int adGroupIndex = 0;
        for (int i = 0; i < count; i++) {
            double cuePoint = cuePoints.get(i);
            if (cuePoint == -1.0) {
                adGroupTimesUs[count - 1] = C.TIME_END_OF_SOURCE;
            } else {
                adGroupTimesUs[adGroupIndex++] = Math.round(MICROS_PER_SECOND * cuePoint);
            }
        }
        // Cue points may be out of order, so sort them.
        Arrays.sort(adGroupTimesUs, 0, adGroupIndex);
        return new AdPlaybackStateTest(adGroupTimesUs);
    }
}
