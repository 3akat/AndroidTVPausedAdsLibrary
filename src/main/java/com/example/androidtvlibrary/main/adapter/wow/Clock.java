package com.example.androidtvlibrary.main.adapter.wow;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.media3.common.util.HandlerWrapper;

public interface Clock {

    /**
     * Default {@link Clock} to use for all non-test cases.
     */
    Clock DEFAULT = new SystemClock();

    /**
     * Returns the current time in milliseconds since the Unix Epoch.
     *
     * @see System#currentTimeMillis()
     */
    long currentTimeMillis();

    /** @see android.os.SystemClock#elapsedRealtime() */
    long elapsedRealtime();

    /** @see android.os.SystemClock#uptimeMillis() */
    long uptimeMillis();

    /** @see android.os.SystemClock#sleep(long) */
    void sleep(long sleepTimeMs);

    /**
     * Creates a {@link HandlerWrapper} using a specified looper and a specified callback for handling
     * messages.
     *
     * @see Handler#Handler(Looper, Handler.Callback)
     */
    com.example.androidtvlibrary.main.adapter.wow.HandlerWrapper createHandler(Looper looper, @Nullable Handler.Callback callback);
}
