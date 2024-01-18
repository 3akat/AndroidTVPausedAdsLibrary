package com.example.androidtvlibrary.main.adapter.ads;

import com.example.androidtvlibrary.main.adapter.AdPlaybackState;
import com.example.androidtvlibrary.main.adapter.AdPlaybackStateTest;
import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Timeline;

public final class SinglePeriodAdTimeline extends ForwardingTimeline {

    private final AdPlaybackStateTest adPlaybackState;

    /**
     * Creates a new timeline with a single period containing ads.
     *
     * @param contentTimeline The timeline of the content alongside which ads will be played. It must
     *     have one window and one period.
     * @param adPlaybackState The state of the period's ads.
     */
    public SinglePeriodAdTimeline(Timeline contentTimeline, AdPlaybackStateTest adPlaybackState) {
        super(contentTimeline);
        Assertions.checkState(contentTimeline.getPeriodCount() == 1);
        Assertions.checkState(contentTimeline.getWindowCount() == 1);
        this.adPlaybackState = adPlaybackState;
    }

    @Override
    public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIds) {
        timeline.getPeriod(periodIndex, period, setIds);
        long durationUs =
                period.durationUs == C.TIME_UNSET ? adPlaybackState.contentDurationUs : period.durationUs;
        period.set(
                period.id,
                period.uid,
                period.windowIndex,
                durationUs,
                period.getPositionInWindowUs(),
                adPlaybackState);
        return period;
    }

}
