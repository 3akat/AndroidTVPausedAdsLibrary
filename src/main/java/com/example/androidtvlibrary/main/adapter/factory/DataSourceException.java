package com.example.androidtvlibrary.main.adapter.factory;

import java.io.IOException;

public final class DataSourceException extends IOException {

    public static final int POSITION_OUT_OF_RANGE = 0;

    /**
     * The reason of this {@link DataSourceException}. It can only be {@link #POSITION_OUT_OF_RANGE}.
     */
    public final int reason;

    /**
     * Constructs a DataSourceException.
     *
     * @param reason Reason of the error. It can only be {@link #POSITION_OUT_OF_RANGE}.
     */
    public DataSourceException(int reason) {
        this.reason = reason;
    }

}
