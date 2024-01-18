package com.example.androidtvlibrary.main.adapter.Media;

import com.example.androidtvlibrary.main.adapter.wow.Clock;

public final class ConditionVariable {

    private final Clock clock;
    private boolean isOpen;

    /** Creates an instance using {@link Clock#DEFAULT}. */
    public ConditionVariable() {
        this(Clock.DEFAULT);
    }

    /**
     * Creates an instance, which starts closed.
     *
     * @param clock The {@link Clock} whose {@link Clock#elapsedRealtime()} method is used to
     *     determine when {@link #block(long)} should time out.
     */
    public ConditionVariable(Clock clock) {
        this.clock = clock;
    }

    /**
     * Opens the condition and releases all threads that are blocked.
     *
     * @return True if the condition variable was opened. False if it was already open.
     */
    public synchronized boolean open() {
        if (isOpen) {
            return false;
        }
        isOpen = true;
        notifyAll();
        return true;
    }

    /**
     * Closes the condition.
     *
     * @return True if the condition variable was closed. False if it was already closed.
     */
    public synchronized boolean close() {
        boolean wasOpen = isOpen;
        isOpen = false;
        return wasOpen;
    }

    /**
     * Blocks until the condition is opened.
     *
     * @throws InterruptedException If the thread is interrupted.
     */
    public synchronized void block() throws InterruptedException {
        while (!isOpen) {
            wait();
        }
    }

    /**
     * Blocks until the condition is opened or until {@code timeoutMs} have passed.
     *
     * @param timeoutMs The maximum time to wait in milliseconds. If {@code timeoutMs <= 0} then the
     *     call will return immediately without blocking.
     * @return True if the condition was opened, false if the call returns because of the timeout.
     * @throws InterruptedException If the thread is interrupted.
     */
    public synchronized boolean block(long timeoutMs) throws InterruptedException {
        if (timeoutMs <= 0) {
            return isOpen;
        }
        long nowMs = clock.elapsedRealtime();
        long endMs = nowMs + timeoutMs;
        if (endMs < nowMs) {
            // timeoutMs is large enough for (nowMs + timeoutMs) to rollover. Block indefinitely.
            block();
        } else {
            while (!isOpen && nowMs < endMs) {
                wait(endMs - nowMs);
                nowMs = clock.elapsedRealtime();
            }
        }
        return isOpen;
    }

    /** Returns whether the condition is opened. */
    public synchronized boolean isOpen() {
        return isOpen;
    }
}

