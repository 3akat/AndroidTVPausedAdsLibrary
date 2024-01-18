package com.example.androidtvlibrary.main.adapter;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.util.Assertions;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class StatsDataSource implements DataSource {
    public static final int RESULT_END_OF_INPUT = -1;
    private final DataSource dataSource;

    private long bytesRead;
    private Uri lastOpenedUri;
    private Map<String, List<String>> lastResponseHeaders;

    /**
     * Creates the stats data source.
     *
     * @param dataSource The wrapped {@link DataSource}.
     */
    public StatsDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        lastOpenedUri = Uri.EMPTY;
        lastResponseHeaders = Collections.emptyMap();
    }

    /** Resets the number of bytes read as returned from {@link #getBytesRead()} to zero. */
    public void resetBytesRead() {
        bytesRead = 0;
    }

    /** Returns the total number of bytes that have been read from the data source. */
    public long getBytesRead() {
        return bytesRead;
    }

    /**
     * Returns the {@link Uri} associated with the last {@link #open(DataSpecTest)} call. If redirection
     * occurred, this is the redirected uri.
     */
    public Uri getLastOpenedUri() {
        return lastOpenedUri;
    }

    /** Returns the response headers associated with the last {@link #open(DataSpecTest)} call. */
    public Map<String, List<String>> getLastResponseHeaders() {
        return lastResponseHeaders;
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        dataSource.addTransferListener(transferListener);
    }

    @Override
    public long open(DataSpecTest dataSpec) throws IOException {
        // Reassign defaults in case dataSource.open throws an exception.
        lastOpenedUri = dataSpec.uri;
        lastResponseHeaders = Collections.emptyMap();
        long availableBytes = dataSource.open(dataSpec);
        lastOpenedUri = getUri();
        lastResponseHeaders = getResponseHeaders();
        return availableBytes;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        int bytesRead = dataSource.read(buffer, offset, readLength);
        if (bytesRead != RESULT_END_OF_INPUT) {
            this.bytesRead += bytesRead;
        }
        return bytesRead;
    }

    @Override
    @Nullable
    public Uri getUri() {
        return dataSource.getUri();
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return dataSource.getResponseHeaders();
    }

    @Override
    public void close() throws IOException {
        dataSource.close();
    }
}
