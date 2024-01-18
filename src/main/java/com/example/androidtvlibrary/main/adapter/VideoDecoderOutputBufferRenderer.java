package com.example.androidtvlibrary.main.adapter;

public interface VideoDecoderOutputBufferRenderer {

    /**
     * Sets the output buffer to be rendered. The renderer is responsible for releasing the buffer.
     *
     * @param outputBuffer The output buffer to be rendered.
     */
    void setOutputBuffer(VideoDecoderOutputBuffer outputBuffer);
}
