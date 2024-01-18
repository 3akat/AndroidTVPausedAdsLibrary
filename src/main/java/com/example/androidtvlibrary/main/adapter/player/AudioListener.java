package com.example.androidtvlibrary.main.adapter.player;

public interface AudioListener {

    /**
     * Called when the audio session is set.
     *
     * @param audioSessionId The audio session id.
     */
    default void onAudioSessionId(int audioSessionId) {}

    /**
     * Called when the audio attributes change.
     *
     * @param audioAttributes The audio attributes.
     */
    default void onAudioAttributesChanged(AudioAttributes audioAttributes) {}

    /**
     * Called when the volume changes.
     *
     * @param volume The new volume, with 0 being silence and 1 being unity gain.
     */
    default void onVolumeChanged(float volume) {}
}