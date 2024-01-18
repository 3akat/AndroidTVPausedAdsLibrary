package com.example.androidtvlibrary.main.adapter.factory;

import static com.example.androidtvlibrary.main.adapter.Util.castNonNull;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.DataSource;
import com.example.androidtvlibrary.main.adapter.DataSpec;
import com.example.androidtvlibrary.main.adapter.DataSpecTest;
import com.example.androidtvlibrary.main.adapter.TransferListener;

import java.util.ArrayList;

public abstract class BaseDataSource implements DataSource {

    private final boolean isNetwork;
    private final ArrayList<TransferListener> listeners;

    private int listenerCount;
    @Nullable
    private DataSpecTest dataSpec;

    /**
     * Creates base data source.
     *
     * @param isNetwork Whether the data source loads data through a network.
     */
    protected BaseDataSource(boolean isNetwork) {
        this.isNetwork = isNetwork;
        this.listeners = new ArrayList<>(/* initialCapacity= */ 1);
    }

    @Override
    public final void addTransferListener(TransferListener transferListener) {
        if (!listeners.contains(transferListener)) {
            listeners.add(transferListener);
            listenerCount++;
        }
    }

    /**
     * Notifies listeners that data transfer for the specified {@link DataSpec} is being initialized.
     *
     * @param dataSpec {@link DataSpec} describing the data for initializing transfer.
     */
    protected final void transferInitializing(DataSpecTest dataSpec) {
        for (int i = 0; i < listenerCount; i++) {
            listeners.get(i).onTransferInitializing(/* source= */ this, dataSpec, isNetwork);
        }
    }

    /**
     * Notifies listeners that data transfer for the specified {@link DataSpec} started.
     *
     * @param dataSpec {@link DataSpec} describing the data being transferred.
     */
    protected final void transferStarted(DataSpecTest dataSpec) {
        this.dataSpec = dataSpec;
        for (int i = 0; i < listenerCount; i++) {
            listeners.get(i).onTransferStart(/* source= */ this, dataSpec, isNetwork);
        }
    }

    /**
     * Notifies listeners that bytes were transferred.
     *
     * @param bytesTransferred The number of bytes transferred since the previous call to this method
     *     (or if the first call, since the transfer was started).
     */
    protected final void bytesTransferred(int bytesTransferred) {
        DataSpecTest dataSpec = castNonNull(this.dataSpec);
        for (int i = 0; i < listenerCount; i++) {
            listeners
                    .get(i)
                    .onBytesTransferred(/* source= */ this, dataSpec, isNetwork, bytesTransferred);
        }
    }

    /** Notifies listeners that a transfer ended. */
    protected final void transferEnded() {
        DataSpecTest dataSpec = castNonNull(this.dataSpec);
        for (int i = 0; i < listenerCount; i++) {
            listeners.get(i).onTransferEnd(/* source= */  this, dataSpec, isNetwork);
        }
        this.dataSpec = null;
    }
}
