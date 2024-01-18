package com.example.androidtvlibrary.main.adapter.factory;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.DataSource;
import com.example.androidtvlibrary.main.adapter.TransferListener;

public final class DefaultDataSourceFactory implements DataSource.Factory {

    private final Context context;
    @Nullable
    private final TransferListener listener;
    private final DataSource.Factory baseDataSourceFactory;

    /**
     * @param context A context.
     * @param userAgent The User-Agent string that should be used.
     */
    public DefaultDataSourceFactory(Context context, String userAgent) {
        this(context, userAgent, /* listener= */ null);
    }

    /**
     * @param context A context.
     * @param userAgent The User-Agent string that should be used.
     * @param listener An optional listener.
     */
    public DefaultDataSourceFactory(
            Context context, String userAgent, @Nullable TransferListener listener) {
        this(context, listener, new DefaultHttpDataSourceFactory(userAgent, listener));
    }

    /**
     * @param context A context.
     * @param baseDataSourceFactory A {@link DataSource.Factory} to be used to create a base {@link DataSource}
     *     for {@link DefaultDataSource}.
     * @see DefaultDataSource#DefaultDataSource(Context, DataSource)
     */
    public DefaultDataSourceFactory(Context context, DataSource.Factory baseDataSourceFactory) {
        this(context, /* listener= */ null, baseDataSourceFactory);
    }

    /**
     * @param context A context.
     * @param listener An optional listener.
     * @param baseDataSourceFactory A {@link DataSource.Factory} to be used to create a base {@link DataSource}
     *     for {@link DefaultDataSource}.
     * @see DefaultDataSource#DefaultDataSource(Context, DataSource)
     */
    public DefaultDataSourceFactory(
            Context context,
            @Nullable TransferListener listener,
            DataSource.Factory baseDataSourceFactory) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.baseDataSourceFactory = baseDataSourceFactory;
    }

    @Override
    public DefaultDataSource createDataSource() {
        DefaultDataSource dataSource =
                new DefaultDataSource(context, baseDataSourceFactory.createDataSource());
        if (listener != null) {
            dataSource.addTransferListener(listener);
        }
        return dataSource;
    }
}
