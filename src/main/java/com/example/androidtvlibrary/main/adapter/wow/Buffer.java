package com.example.androidtvlibrary.main.adapter.wow;

import com.example.androidtvlibrary.main.adapter.C;

public abstract class Buffer {

    @C.BufferFlags
    private int flags;

    /**
     * Clears the buffer.
     */
    public void clear() {
        flags = 0;
    }

    /**
     * Returns whether the {@link C#BUFFER_FLAG_DECODE_ONLY} flag is set.
     */
    public final boolean isDecodeOnly() {
        return getFlag(C.BUFFER_FLAG_DECODE_ONLY);
    }

    /**
     * Returns whether the {@link C#BUFFER_FLAG_END_OF_STREAM} flag is set.
     */
    public final boolean isEndOfStream() {
        return getFlag(C.BUFFER_FLAG_END_OF_STREAM);
    }

    /**
     * Returns whether the {@link C#BUFFER_FLAG_KEY_FRAME} flag is set.
     */
    public final boolean isKeyFrame() {
        return getFlag(C.BUFFER_FLAG_KEY_FRAME);
    }

    /** Returns whether the {@link C#BUFFER_FLAG_HAS_SUPPLEMENTAL_DATA} flag is set. */
    public final boolean hasSupplementalData() {
        return getFlag(C.BUFFER_FLAG_HAS_SUPPLEMENTAL_DATA);
    }

    /**
     * Replaces this buffer's flags with {@code flags}.
     *
     * @param flags The flags to set, which should be a combination of the {@code C.BUFFER_FLAG_*}
     *     constants.
     */
    public final void setFlags(@C.BufferFlags int flags) {
        this.flags = flags;
    }

    /**
     * Adds the {@code flag} to this buffer's flags.
     *
     * @param flag The flag to add to this buffer's flags, which should be one of the
     *     {@code C.BUFFER_FLAG_*} constants.
     */
    public final void addFlag(@C.BufferFlags int flag) {
        flags |= flag;
    }

    /**
     * Removes the {@code flag} from this buffer's flags, if it is set.
     *
     * @param flag The flag to remove.
     */
    public final void clearFlag(@C.BufferFlags int flag) {
        flags &= ~flag;
    }

    /**
     * Returns whether the specified flag has been set on this buffer.
     *
     * @param flag The flag to check.
     * @return Whether the flag is set.
     */
    protected final boolean getFlag(@C.BufferFlags int flag) {
        return (flags & flag) == flag;
    }

}

