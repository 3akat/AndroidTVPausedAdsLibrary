package com.example.androidtvlibrary.main.adapter.wow;

public interface Allocator {

    /**
     * Obtain an {@link Allocation}.
     * <p>
     * When the caller has finished with the {@link Allocation}, it should be returned by calling
     * {@link #release(Allocation)}.
     *
     * @return The {@link Allocation}.
     */
    Allocation allocate();

    /**
     * Releases an {@link Allocation} back to the allocator.
     *
     * @param allocation The {@link Allocation} being released.
     */
    void release(Allocation allocation);

    /**
     * Releases an array of {@link Allocation}s back to the allocator.
     *
     * @param allocations The array of {@link Allocation}s being released.
     */
    void release(Allocation[] allocations);

    /**
     * Hints to the allocator that it should make a best effort to release any excess
     * {@link Allocation}s.
     */
    void trim();

    /**
     * Returns the total number of bytes currently allocated.
     */
    int getTotalBytesAllocated();

    /**
     * Returns the length of each individual {@link Allocation}.
     */
    int getIndividualAllocationLength();

}
