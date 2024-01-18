package com.example.androidtvlibrary.main.adapter.Media.extractor;

import java.util.Arrays;

public final class LongArray {

    private static final int DEFAULT_INITIAL_CAPACITY = 32;

    private int size;
    private long[] values;

    public LongArray() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * @param initialCapacity The initial capacity of the array.
     */
    public LongArray(int initialCapacity) {
        values = new long[initialCapacity];
    }

    /**
     * Appends a value.
     *
     * @param value The value to append.
     */
    public void add(long value) {
        if (size == values.length) {
            values = Arrays.copyOf(values, size * 2);
        }
        values[size++] = value;
    }

    /**
     * Returns the value at a specified index.
     *
     * @param index The index.
     * @return The corresponding value.
     * @throws IndexOutOfBoundsException If the index is less than zero, or greater than or equal to
     *     {@link #size()}.
     */
    public long get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index " + index + ", size is " + size);
        }
        return values[index];
    }

    /**
     * Returns the current size of the array.
     */
    public int size() {
        return size;
    }

    /**
     * Copies the current values into a newly allocated primitive array.
     *
     * @return The primitive array containing the copied values.
     */
    public long[] toArray() {
        return Arrays.copyOf(values, size);
    }

}
