package com.example.androidtvlibrary.main.adapter.player;


import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.TestPlayer;

public interface ControlDispatcher {

    /**
     * Dispatches a {@link TestPlayer#setPlayWhenReady(boolean)} operation.
     *
     * @param player The {@link TestPlayer} to which the operation should be dispatched.
     * @param playWhenReady Whether playback should proceed when ready.
     * @return True if the operation was dispatched. False if suppressed.
     */
    boolean dispatchSetPlayWhenReady(TestPlayer player, boolean playWhenReady);

    /**
     * Dispatches a {@link TestPlayer#seekTo(int, long)} operation.
     *
     * @param player The {@link TestPlayer} to which the operation should be dispatched.
     * @param windowIndex The index of the window.
     * @param positionMs The seek position in the specified window, or {@link C#TIME_UNSET} to seek to
     *     the window's default position.
     * @return True if the operation was dispatched. False if suppressed.
     */
    boolean dispatchSeekTo(TestPlayer player, int windowIndex, long positionMs);

    /**
     * Dispatches a {@link TestPlayer#setRepeatMode(int)} operation.
     *
     * @param player The {@link TestPlayer} to which the operation should be dispatched.
     * @param repeatMode The repeat mode.
     * @return True if the operation was dispatched. False if suppressed.
     */
    boolean dispatchSetRepeatMode(TestPlayer player, @TestPlayer.RepeatMode int repeatMode);

    /**
     * Dispatches a {@link TestPlayer#setShuffleModeEnabled(boolean)} operation.
     *
     * @param player The {@link TestPlayer} to which the operation should be dispatched.
     * @param shuffleModeEnabled Whether shuffling is enabled.
     * @return True if the operation was dispatched. False if suppressed.
     */
    boolean dispatchSetShuffleModeEnabled(TestPlayer player, boolean shuffleModeEnabled);

    /**
     * Dispatches a {@link TestPlayer#stop()} operation.
     *
     * @param player The {@link TestPlayer} to which the operation should be dispatched.
     * @param reset Whether the player should be reset.
     * @return True if the operation was dispatched. False if suppressed.
     */
    boolean dispatchStop(TestPlayer player, boolean reset);
}
