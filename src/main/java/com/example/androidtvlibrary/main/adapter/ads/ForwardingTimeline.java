package com.example.androidtvlibrary.main.adapter.ads;

import androidx.media3.common.Player;

import com.example.androidtvlibrary.main.adapter.Timeline;

public abstract class ForwardingTimeline extends Timeline {

    protected final Timeline timeline;

    public ForwardingTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    @Override
    public int getWindowCount() {
        return timeline.getWindowCount();
    }

    @Override
    public int getNextWindowIndex(int windowIndex, @Player.RepeatMode int repeatMode,
                                  boolean shuffleModeEnabled) {
        return timeline.getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
    }

    @Override
    public int getPreviousWindowIndex(int windowIndex, @Player.RepeatMode int repeatMode,
                                      boolean shuffleModeEnabled) {
        return timeline.getPreviousWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
    }

    @Override
    public int getLastWindowIndex(boolean shuffleModeEnabled) {
        return timeline.getLastWindowIndex(shuffleModeEnabled);
    }

    @Override
    public int getFirstWindowIndex(boolean shuffleModeEnabled) {
        return timeline.getFirstWindowIndex(shuffleModeEnabled);
    }

    @Override
    public Window getWindow(int windowIndex, Window window, long defaultPositionProjectionUs) {
        return timeline.getWindow(windowIndex, window, defaultPositionProjectionUs);
    }

    @Override
    public int getPeriodCount() {
        return timeline.getPeriodCount();
    }

    @Override
    public Timeline.Period getPeriod(int periodIndex, Period period, boolean setIds) {
        return timeline.getPeriod(periodIndex, period, setIds);
    }

    @Override
    public int getIndexOfPeriod(Object uid) {
        return timeline.getIndexOfPeriod(uid);
    }

    @Override
    public Object getUidOfPeriod(int periodIndex) {
        return timeline.getUidOfPeriod(periodIndex);
    }
}
