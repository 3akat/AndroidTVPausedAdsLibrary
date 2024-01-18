package com.example.androidtvlibrary.main.adapter;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface DataSource {

    /**
     * A factory for {@link DataSource} instances.
     */
    interface Factory {

        /**
         * Creates a {@link DataSource} instance.
         */
        DataSource createDataSource();
    }

    /**
     * Adds a {@link TransferListener} to listen to data transfers. This method is not thread-safe.
     *
     * @param transferListener A {@link TransferListener}.
     */
    void addTransferListener(TransferListener transferListener);


    long open(DataSpecTest dataSpec) throws IOException;

    /**
     * Reads up to {@code readLength} bytes of data and stores them into {@code buffer}, starting at
     * index {@code offset}.
     *
     * <p>If {@code readLength} is zero then 0 is returned. Otherwise, if no data is available because
     * the end of the opened range has been reached, then {@link C#RESULT_END_OF_INPUT} is returned.
     * Otherwise, the call will block until at least one byte of data has been read and the number of
     * bytes read is returned.
     *
     * @param buffer The buffer into which the read data should be stored.
     * @param offset The start offset into {@code buffer} at which data should be written.
     * @param readLength The maximum number of bytes to read.
     * @return The number of bytes read, or {@link C#RESULT_END_OF_INPUT} if no data is available
     *     because the end of the opened range has been reached.
     * @throws IOException If an error occurs reading from the source.
     */
    int read(byte[] buffer, int offset, int readLength) throws IOException;

    /**
     * When the source is open, returns the {@link Uri} from which data is being read. The returned
     * {@link Uri} will be identical to the one passed {@link #open(DataSpecTest)} in the {@link DataSpec}
     * unless redirection has occurred. If redirection has occurred, the {@link Uri} after redirection
     * is returned.
     *
     * @return The {@link Uri} from which data is being read, or null if the source is not open.
     */
    @Nullable
    Uri getUri();

    /**
     * When the source is open, returns the response headers associated with the last {@link #open}
     * call. Otherwise, returns an empty map.
     */
    default Map<String, List<String>> getResponseHeaders() {
        return Collections.emptyMap();
    }

    /**
     * Closes the source.
     * <p>
     * Note: This method must be called even if the corresponding call to {@link #open(DataSpecTest)}
     * threw an {@link IOException}. See {@link #open(DataSpecTest)} for more details.
     *
     * @throws IOException If an error occurs closing the source.
     */
    void close() throws IOException;
}
