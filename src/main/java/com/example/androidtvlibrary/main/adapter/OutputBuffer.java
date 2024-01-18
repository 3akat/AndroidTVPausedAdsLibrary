package com.example.androidtvlibrary.main.adapter;

import android.media.MediaCodec;

import androidx.annotation.IntDef;
import androidx.media3.common.C;

public abstract class OutputBuffer {

    @IntDef(
            flag = true,
            value = {
                    BUFFER_FLAG_KEY_FRAME,
                    BUFFER_FLAG_END_OF_STREAM,
                    BUFFER_FLAG_HAS_SUPPLEMENTAL_DATA,
                    BUFFER_FLAG_LAST_SAMPLE,
                    BUFFER_FLAG_ENCRYPTED,
                    BUFFER_FLAG_DECODE_ONLY
            })
    public @interface BufferFlags {}
    /**
     * Indicates that a buffer holds a synchronization sample.
     */
    public static final int BUFFER_FLAG_KEY_FRAME = MediaCodec.BUFFER_FLAG_KEY_FRAME;
    /**
     * Flag for empty buffers that signal that the end of the stream was reached.
     */
    public static final int BUFFER_FLAG_END_OF_STREAM = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
    public static final int BUFFER_FLAG_HAS_SUPPLEMENTAL_DATA = 1 << 28; // 0x10000000
    /** Indicates that a buffer is known to contain the last media sample of the stream. */
    public static final int BUFFER_FLAG_LAST_SAMPLE = 1 << 29; // 0x20000000
    /** Indicates that a buffer is (at least partially) encrypted. */
    public static final int BUFFER_FLAG_ENCRYPTED = 1 << 30; // 0x40000000
    /** Indicates that a buffer should be decoded but not rendered. */
    public static final int BUFFER_FLAG_DECODE_ONLY = 1 << 31;
    @C.BufferFlags
    private int flags;

    /**
     * Clears the buffer.
     */
    public void clear() {
        flags = 0;
    }


    public final boolean isDecodeOnly() {
        return getFlag(BUFFER_FLAG_DECODE_ONLY);
    }


    public final boolean isEndOfStream() {
        return getFlag(BUFFER_FLAG_END_OF_STREAM);
    }


    public final boolean isKeyFrame() {
        return getFlag(BUFFER_FLAG_KEY_FRAME);
    }

    public final boolean hasSupplementalData() {
        return getFlag(BUFFER_FLAG_HAS_SUPPLEMENTAL_DATA);
    }

    /**
     * Replaces this buffer's flags with {@code flags}.
     *
     * @param flags The flags to set, which should be a combination of the {@code C.BUFFER_FLAG_*}
     *     constants.
     */
    public final void setFlags(@BufferFlags int flags) {
        this.flags = flags;
    }

    /**
     * Adds the {@code flag} to this buffer's flags.
     *
     * @param flag The flag to add to this buffer's flags, which should be one of the
     *     {@code C.BUFFER_FLAG_*} constants.
     */
    public final void addFlag(@BufferFlags int flag) {
        flags |= flag;
    }

    /**
     * Removes the {@code flag} from this buffer's flags, if it is set.
     *
     * @param flag The flag to remove.
     */
    public final void clearFlag(@BufferFlags int flag) {
        flags &= ~flag;
    }

    /**
     * Returns whether the specified flag has been set on this buffer.
     *
     * @param flag The flag to check.
     * @return Whether the flag is set.
     */
    protected final boolean getFlag(@BufferFlags int flag) {
        return (flags & flag) == flag;
    }

    /**
     * The presentation timestamp for the buffer, in microseconds.
     */
    public long timeUs;


    public int skippedOutputBufferCount;

    /**
     * Releases the output buffer for reuse. Must be called when the buffer is no longer needed.
     */
    public abstract void release();
}
