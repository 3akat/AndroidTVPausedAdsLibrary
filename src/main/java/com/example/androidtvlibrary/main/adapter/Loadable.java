package com.example.androidtvlibrary.main.adapter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Loadable {

    /**
     * Cancels the load.
     *
     * <p>Loadable implementations should ensure that a currently executing {@link #load()} call
     * will exit reasonably quickly after this method is called. The {@link #load()} call may exit
     * either by returning or by throwing an {@link IOException}.
     *
     * <p>If there is a currently executing {@link #load()} call, then the thread on which that call
     * is being made will be interrupted immediately after the call to this method. Hence
     * implementations do not need to (and should not attempt to) interrupt the loading thread
     * themselves.
     *
     * <p>Although the loading thread will be interrupted, Loadable implementations should not use
     * the interrupted status of the loading thread in {@link #load()} to determine whether the load
     * has been canceled. This approach is not robust [Internal ref: b/79223737]. Instead,
     * implementations should use their own flag to signal cancelation (for example, using {@link
     * AtomicBoolean}).
     */
    void cancelLoad();

    /**
     * Performs the load, returning on completion or cancellation.
     *
     * @throws IOException If the input could not be loaded.
     * @throws InterruptedException If the thread was interrupted.
     */
    void load() throws IOException, InterruptedException;

}
