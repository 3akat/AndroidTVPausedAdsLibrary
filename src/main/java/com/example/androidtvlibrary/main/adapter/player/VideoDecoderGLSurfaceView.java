package com.example.androidtvlibrary.main.adapter.player;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.VideoDecoderOutputBufferRenderer;

public class VideoDecoderGLSurfaceView extends GLSurfaceView {

    private final VideoDecoderRenderer renderer;

    /** @param context A {@link Context}. */
    public VideoDecoderGLSurfaceView(Context context) {
        this(context, /* attrs= */ null);
    }

    /**
     * @param context A {@link Context}.
     * @param attrs Custom attributes.
     */
    public VideoDecoderGLSurfaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        renderer = new VideoDecoderRenderer(this);
        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /** Returns the {@link VideoDecoderOutputBufferRenderer} that will render frames in this view. */
    public VideoDecoderOutputBufferRenderer getVideoDecoderOutputBufferRenderer() {
        return renderer;
    }
}
