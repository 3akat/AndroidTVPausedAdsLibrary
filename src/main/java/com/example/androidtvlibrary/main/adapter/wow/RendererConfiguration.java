package com.example.androidtvlibrary.main.adapter.wow;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.C;

public final class RendererConfiguration {

    /**
     * The default configuration.
     */
    public static final RendererConfiguration DEFAULT =
            new RendererConfiguration(C.AUDIO_SESSION_ID_UNSET);

    public final int tunnelingAudioSessionId;


    public RendererConfiguration(int tunnelingAudioSessionId) {
        this.tunnelingAudioSessionId = tunnelingAudioSessionId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RendererConfiguration other = (RendererConfiguration) obj;
        return tunnelingAudioSessionId == other.tunnelingAudioSessionId;
    }

    @Override
    public int hashCode() {
        return tunnelingAudioSessionId;
    }

}
