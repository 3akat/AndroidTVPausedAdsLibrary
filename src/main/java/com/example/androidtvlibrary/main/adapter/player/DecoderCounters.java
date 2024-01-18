package com.example.androidtvlibrary.main.adapter.player;

public final class DecoderCounters {

    /**
     * The number of times a decoder has been initialized.
     */
    public int decoderInitCount;
    /**
     * The number of times a decoder has been released.
     */
    public int decoderReleaseCount;
    /**
     * The number of queued input buffers.
     */
    public int inputBufferCount;
    /**
     * The number of skipped input buffers.
     * <p>
     * A skipped input buffer is an input buffer that was deliberately not sent to the decoder.
     */
    public int skippedInputBufferCount;
    /**
     * The number of rendered output buffers.
     */
    public int renderedOutputBufferCount;
    /**
     * The number of skipped output buffers.
     * <p>
     * A skipped output buffer is an output buffer that was deliberately not rendered.
     */
    public int skippedOutputBufferCount;
    /**
     * The number of dropped buffers.
     * <p>
     * A dropped buffer is an buffer that was supposed to be decoded/rendered, but was instead
     * dropped because it could not be rendered in time.
     */
    public int droppedBufferCount;
    /**
     * The maximum number of dropped buffers without an interleaving rendered output buffer.
     * <p>
     * Skipped output buffers are ignored for the purposes of calculating this value.
     */
    public int maxConsecutiveDroppedBufferCount;
    /**
     * The number of times all buffers to a keyframe were dropped.
     * <p>
     * Each time buffers to a keyframe are dropped, this counter is increased by one, and the dropped
     * buffer counters are increased by one (for the current output buffer) plus the number of buffers
     * dropped from the source to advance to the keyframe.
     */
    public int droppedToKeyframeCount;

    /**
     * Should be called to ensure counter values are made visible across threads. The playback thread
     * should call this method after updating the counter values. Any other thread should call this
     * method before reading the counters.
     */
    public synchronized void ensureUpdated() {
        // Do nothing. The use of synchronized ensures a memory barrier should another thread also
        // call this method.
    }

    /**
     * Merges the counts from {@code other} into this instance.
     *
     * @param other The {@link DecoderCounters} to merge into this instance.
     */
    public void merge(DecoderCounters other) {
        decoderInitCount += other.decoderInitCount;
        decoderReleaseCount += other.decoderReleaseCount;
        inputBufferCount += other.inputBufferCount;
        skippedInputBufferCount += other.skippedInputBufferCount;
        renderedOutputBufferCount += other.renderedOutputBufferCount;
        skippedOutputBufferCount += other.skippedOutputBufferCount;
        droppedBufferCount += other.droppedBufferCount;
        maxConsecutiveDroppedBufferCount = Math.max(maxConsecutiveDroppedBufferCount,
                other.maxConsecutiveDroppedBufferCount);
        droppedToKeyframeCount += other.droppedToKeyframeCount;
    }

}
