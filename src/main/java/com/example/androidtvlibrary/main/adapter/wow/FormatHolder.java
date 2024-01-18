package com.example.androidtvlibrary.main.adapter.wow;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Format;

public final class FormatHolder {

    /** Whether the {@link #format} setter also sets the {@link #drmSession} field. */
    // TODO: Remove once all Renderers and MediaSources have migrated to the new DRM model [Internal
    // ref: b/129764794].
    public boolean includesDrmSession;

    /** An accompanying context for decrypting samples in the format. */
    @Nullable
    public DrmSession<?> drmSession;

    /** The held {@link Format}. */
    @Nullable public Format format;

    /** Clears the holder. */
    public void clear() {
        includesDrmSession = false;
        drmSession = null;
        format = null;
    }
}
