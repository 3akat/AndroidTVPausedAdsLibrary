package com.example.androidtvlibrary.main.adapter.wow;

public final class Allocation {

    /**
     * The array containing the allocated space. The allocated space might not be at the start of the
     * array, and so {@link #offset} must be used when indexing into it.
     */
    public final byte[] data;

    /**
     * The offset of the allocated space in {@link #data}.
     */
    public final int offset;

    /**
     * @param data The array containing the allocated space.
     * @param offset The offset of the allocated space in {@code data}.
     */
    public Allocation(byte[] data, int offset) {
        this.data = data;
        this.offset = offset;
    }

}
