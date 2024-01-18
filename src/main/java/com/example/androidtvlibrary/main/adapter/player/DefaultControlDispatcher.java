package com.example.androidtvlibrary.main.adapter.player;

import com.example.androidtvlibrary.main.adapter.TestPlayer;

public class DefaultControlDispatcher implements ControlDispatcher {

    @Override
    public boolean dispatchSetPlayWhenReady(TestPlayer player, boolean playWhenReady) {
        player.setPlayWhenReady(playWhenReady);
        return true;
    }

    @Override
    public boolean dispatchSeekTo(TestPlayer player, int windowIndex, long positionMs) {
        player.seekTo(windowIndex, positionMs);
        return true;
    }

    @Override
    public boolean dispatchSetRepeatMode(TestPlayer player, @TestPlayer.RepeatMode int repeatMode) {
        player.setRepeatMode(repeatMode);
        return true;
    }

    @Override
    public boolean dispatchSetShuffleModeEnabled(TestPlayer player, boolean shuffleModeEnabled) {
        player.setShuffleModeEnabled(shuffleModeEnabled);
        return true;
    }

    @Override
    public boolean dispatchStop(TestPlayer player, boolean reset) {
        player.stop(reset);
        return true;
    }
}
