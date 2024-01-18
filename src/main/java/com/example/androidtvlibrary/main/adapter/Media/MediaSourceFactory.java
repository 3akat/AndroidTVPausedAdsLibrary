package com.example.androidtvlibrary.main.adapter.Media;

import android.net.Uri;

import androidx.media3.common.StreamKey;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.wow.DrmSession;
import com.example.androidtvlibrary.main.adapter.wow.MediaSource;

import java.util.List;

public interface MediaSourceFactory {

    /**
     * Sets a list of {@link StreamKey StreamKeys} by which the manifest is filtered.
     *
     * @param streamKeys A list of {@link StreamKey StreamKeys}.
     * @return This factory, for convenience.
     * @throws IllegalStateException If {@link #createMediaSource(Uri)} has already been called.
     */
    default MediaSourceFactory setStreamKeys(List<StreamKey> streamKeys) {
        return this;
    }

    /**
     * Sets the {@link DrmSessionManager} to use for acquiring {@link DrmSession DrmSessions}.
     *
     * @param drmSessionManager The {@link DrmSessionManager}.
     * @return This factory, for convenience.
     * @throws IllegalStateException If one of the {@code create} methods has already been called.
     */
    MediaSourceFactory setDrmSessionManager(DrmSessionManager<?> drmSessionManager);

    /**
     * Creates a new {@link MediaSource} with the specified {@code uri}.
     *
     * @param uri The URI to play.
     * @return The new {@link MediaSource media source}.
     */
    MediaSource createMediaSource(Uri uri);

    /**
     * Returns the {@link C.ContentType content types} supported by media sources created by this
     * factory.
     */
    @C.ContentType
    int[] getSupportedTypes();
}
