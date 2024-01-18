package com.example.androidtvlibrary.main.adapter.player;

import android.os.Handler;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Media.DrmSessionManager;
import com.example.androidtvlibrary.main.adapter.TestPlayer;
import com.example.androidtvlibrary.main.adapter.wow.Renderer;
import com.example.androidtvlibrary.main.adapter.wow.SimpleWowPlayer;

public interface RenderersFactory {

    /**
     * Builds the {@link Renderer} instances for a {@link SimpleWowPlayer}.
     *
     * @param eventHandler A handler to use when invoking event listeners and outputs.
     * @param videoRendererEventListener An event listener for video renderers.
     * @param audioRendererEventListener An event listener for audio renderers.
     * @param textRendererOutput An output for text renderers.
     * @param metadataRendererOutput An output for metadata renderers.
     * @param drmSessionManager A drm session manager used by renderers.
     * @return The {@link Renderer instances}.
     */
    Renderer[] createRenderers(
            Handler eventHandler,
            VideoRendererEventListener videoRendererEventListener,
            AudioRendererEventListener audioRendererEventListener,
            TestPlayer.TextOutput textRendererOutput,
            TestPlayer.MetadataOutput metadataRendererOutput,
            @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager);
}
