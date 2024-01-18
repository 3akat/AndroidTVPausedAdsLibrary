package com.example.androidtvlibrary.main.adapter.Media;

import com.example.androidtvlibrary.main.adapter.Loadable;

import java.io.IOException;

public interface LoaderErrorThrower {

    /**
     * Throws a fatal error, or a non-fatal error if loading is currently backed off and the current
     * {@link Loadable} has incurred a number of errors greater than the {@link Loader}s default
     * minimum number of retries. Else does nothing.
     *
     * @throws IOException The error.
     */
    void maybeThrowError() throws IOException;

    /**
     * Throws a fatal error, or a non-fatal error if loading is currently backed off and the current
     * {@link Loadable} has incurred a number of errors greater than the specified minimum number
     * of retries. Else does nothing.
     *
     * @param minRetryCount A minimum retry count that must be exceeded for a non-fatal error to be
     *     thrown. Should be non-negative.
     * @throws IOException The error.
     */
    void maybeThrowError(int minRetryCount) throws IOException;

    /**
     * A {@link LoaderErrorThrower} that never throws.
     */
    final class Dummy implements LoaderErrorThrower {

        @Override
        public void maybeThrowError() throws IOException {
            // Do nothing.
        }

        @Override
        public void maybeThrowError(int minRetryCount) throws IOException {
            // Do nothing.
        }

    }

}
