package com.example.androidtvlibrary.main.adapter.Media;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Loadable;

import java.io.IOException;

public interface LoadErrorHandlingPolicy {

    /**
     * Returns the number of milliseconds for which a resource associated to a provided load error
     * should be blacklisted, or {@link C#TIME_UNSET} if the resource should not be blacklisted.
     *
     * @param dataType One of the {@link C C.DATA_TYPE_*} constants indicating the type of data to
     *     load.
     * @param loadDurationMs The duration in milliseconds of the load from the start of the first load
     *     attempt up to the point at which the error occurred.
     * @param exception The load error.
     * @param errorCount The number of errors this load has encountered, including this one.
     * @return The blacklist duration in milliseconds, or {@link C#TIME_UNSET} if the resource should
     *     not be blacklisted.
     */
    long getBlacklistDurationMsFor(
            int dataType, long loadDurationMs, IOException exception, int errorCount);

    /**
     * Returns the number of milliseconds to wait before attempting the load again, or {@link
     * C#TIME_UNSET} if the error is fatal and should not be retried.
     *
     * <p>{@link Loader} clients may ignore the retry delay returned by this method in order to wait
     * for a specific event before retrying. However, the load is retried if and only if this method
     * does not return {@link C#TIME_UNSET}.
     *
     * @param dataType One of the {@link C C.DATA_TYPE_*} constants indicating the type of data to
     *     load.
     * @param loadDurationMs The duration in milliseconds of the load from the start of the first load
     *     attempt up to the point at which the error occurred.
     * @param exception The load error.
     * @param errorCount The number of errors this load has encountered, including this one.
     * @return The number of milliseconds to wait before attempting the load again, or {@link
     *     C#TIME_UNSET} if the error is fatal and should not be retried.
     */
    long getRetryDelayMsFor(int dataType, long loadDurationMs, IOException exception, int errorCount);


    int getMinimumLoadableRetryCount(int dataType);
}
