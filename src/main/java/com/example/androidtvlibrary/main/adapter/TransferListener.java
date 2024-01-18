package com.example.androidtvlibrary.main.adapter;

public interface TransferListener {

    /**
     * Called when a transfer is being initialized.
     *
     * @param source The source performing the transfer.
     * @param dataSpec Describes the data for which the transfer is initialized.
     * @param isNetwork Whether the data is transferred through a network.
     */
    void onTransferInitializing(DataSource source, DataSpecTest dataSpec, boolean isNetwork);

    /**
     * Called when a transfer starts.
     *
     * @param source The source performing the transfer.
     * @param dataSpec Describes the data being transferred.
     * @param isNetwork Whether the data is transferred through a network.
     */
    void onTransferStart(DataSource source, DataSpecTest dataSpec, boolean isNetwork);

    /**
     * Called incrementally during a transfer.
     *
     * @param source The source performing the transfer.
     * @param dataSpec Describes the data being transferred.
     * @param isNetwork Whether the data is transferred through a network.
     * @param bytesTransferred The number of bytes transferred since the previous call to this method
     */
    void onBytesTransferred(DataSource source, DataSpecTest dataSpec, boolean isNetwork, int bytesTransferred);

    /**
     * Called when a transfer ends.
     *
     * @param source The source performing the transfer.
     * @param dataSpec Describes the data being transferred.
     * @param isNetwork Whether the data is transferred through a network.
     */
    void onTransferEnd(DataSource source, DataSpecTest dataSpec, boolean isNetwork);
}
