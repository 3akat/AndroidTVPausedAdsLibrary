package com.example.androidtvlibrary.main.adapter.factory;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.DataSource;
import com.example.androidtvlibrary.main.adapter.DataSpec;
import com.example.androidtvlibrary.main.adapter.DataSpecTest;
import com.example.androidtvlibrary.main.adapter.TransferListener;
import com.example.androidtvlibrary.main.adapter.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DefaultDataSource implements DataSource {

    private static final String TAG = "DefaultDataSource";

    private static final String SCHEME_ASSET = "asset";
    private static final String SCHEME_CONTENT = "content";
    private static final String SCHEME_RTMP = "rtmp";
    private static final String SCHEME_UDP = "udp";
    private static final String SCHEME_RAW = RawResourceDataSource.RAW_RESOURCE_SCHEME;

    private final Context context;
    private final List<TransferListener> transferListeners;
    private final DataSource baseDataSource;

    // Lazily initialized.
    @Nullable
    private DataSource fileDataSource;
    @Nullable private DataSource assetDataSource;
    @Nullable private DataSource contentDataSource;
    @Nullable private DataSource rtmpDataSource;
    @Nullable private DataSource udpDataSource;
    @Nullable private DataSource dataSchemeDataSource;
    @Nullable private DataSource rawResourceDataSource;

    @Nullable private DataSource dataSource;

    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context A context.
     * @param userAgent The User-Agent to use when requesting remote data.
     * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP
     *     to HTTPS and vice versa) are enabled when fetching remote data.
     */
    public DefaultDataSource(Context context, String userAgent, boolean allowCrossProtocolRedirects) {
        this(
                context,
                userAgent,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                allowCrossProtocolRedirects);
    }

    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context A context.
     * @param userAgent The User-Agent to use when requesting remote data.
     * @param connectTimeoutMillis The connection timeout that should be used when requesting remote
     *     data, in milliseconds. A timeout of zero is interpreted as an infinite timeout.
     * @param readTimeoutMillis The read timeout that should be used when requesting remote data, in
     *     milliseconds. A timeout of zero is interpreted as an infinite timeout.
     * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP
     *     to HTTPS and vice versa) are enabled when fetching remote data.
     */
    public DefaultDataSource(
            Context context,
            String userAgent,
            int connectTimeoutMillis,
            int readTimeoutMillis,
            boolean allowCrossProtocolRedirects) {
        this(
                context,
                new DefaultHttpDataSource(
                        userAgent,
                        connectTimeoutMillis,
                        readTimeoutMillis,
                        allowCrossProtocolRedirects,
                        /* defaultRequestProperties= */ null));
    }

    /**
     * Constructs a new instance that delegates to a provided {@link DataSource} for URI schemes other
     * than file, asset and content.
     *
     * @param context A context.
     * @param baseDataSource A {@link DataSource} to use for URI schemes other than file, asset and
     *     content. This {@link DataSource} should normally support at least http(s).
     */
    public DefaultDataSource(Context context, DataSource baseDataSource) {
        this.context = context.getApplicationContext();
        this.baseDataSource = Assertions.checkNotNull(baseDataSource);
        transferListeners = new ArrayList<>();
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        baseDataSource.addTransferListener(transferListener);
        transferListeners.add(transferListener);
        maybeAddListenerToDataSource(fileDataSource, transferListener);
        maybeAddListenerToDataSource(assetDataSource, transferListener);
        maybeAddListenerToDataSource(contentDataSource, transferListener);
        maybeAddListenerToDataSource(rtmpDataSource, transferListener);
        maybeAddListenerToDataSource(udpDataSource, transferListener);
        maybeAddListenerToDataSource(dataSchemeDataSource, transferListener);
        maybeAddListenerToDataSource(rawResourceDataSource, transferListener);
    }

    @Override
    public long open(DataSpecTest dataSpec) throws IOException {
        Assertions.checkState(dataSource == null);
        // Choose the correct source for the scheme.
        String scheme = dataSpec.uri.getScheme();
        if (Util.isLocalFileUri(dataSpec.uri)) {
            String uriPath = dataSpec.uri.getPath();
            if (uriPath != null && uriPath.startsWith("/android_asset/")) {
                dataSource = getAssetDataSource();
            } else {
                dataSource = getFileDataSource();
            }
        } else if (SCHEME_ASSET.equals(scheme)) {
            dataSource = getAssetDataSource();
        } else if (SCHEME_CONTENT.equals(scheme)) {
            dataSource = getContentDataSource();
        } else if (SCHEME_RTMP.equals(scheme)) {
            dataSource = getRtmpDataSource();
        } else if (SCHEME_UDP.equals(scheme)) {
            dataSource = getUdpDataSource();
        } else if (DataSchemeDataSource.SCHEME_DATA.equals(scheme)) {
            dataSource = getDataSchemeDataSource();
        } else if (SCHEME_RAW.equals(scheme)) {
            dataSource = getRawResourceDataSource();
        } else {
            dataSource = baseDataSource;
        }
        // Open the source and return.
        return dataSource.open(dataSpec);
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        return Assertions.checkNotNull(dataSource).read(buffer, offset, readLength);
    }

    @Override
    @Nullable
    public Uri getUri() {
        return dataSource == null ? null : dataSource.getUri();
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return dataSource == null ? Collections.emptyMap() : dataSource.getResponseHeaders();
    }

    @Override
    public void close() throws IOException {
        if (dataSource != null) {
            try {
                dataSource.close();
            } finally {
                dataSource = null;
            }
        }
    }

    private DataSource getUdpDataSource() {
        if (udpDataSource == null) {
            udpDataSource = new UdpDataSource();
            addListenersToDataSource(udpDataSource);
        }
        return udpDataSource;
    }

    private DataSource getFileDataSource() {
        if (fileDataSource == null) {
            fileDataSource = new FileDataSource();
            addListenersToDataSource(fileDataSource);
        }
        return fileDataSource;
    }

    private DataSource getAssetDataSource() {
        if (assetDataSource == null) {
            assetDataSource = new AssetDataSource(context);
            addListenersToDataSource(assetDataSource);
        }
        return assetDataSource;
    }

    private DataSource getContentDataSource() {
        if (contentDataSource == null) {
            contentDataSource = new ContentDataSource(context);
            addListenersToDataSource(contentDataSource);
        }
        return contentDataSource;
    }

    private DataSource getRtmpDataSource() {
        if (rtmpDataSource == null) {
            try {
                // LINT.IfChange
                Class<?> clazz = Class.forName("RtmpDataSource");
                rtmpDataSource = (DataSource) clazz.getConstructor().newInstance();
                // LINT.ThenChange(../../../../../../../../proguard-rules.txt)
                addListenersToDataSource(rtmpDataSource);
            } catch (ClassNotFoundException e) {
                // Expected if the app was built without the RTMP extension.
                Log.w(TAG, "Attempting to play RTMP stream without depending on the RTMP extension");
            } catch (Exception e) {
                // The RTMP extension is present, but instantiation failed.
                throw new RuntimeException("Error instantiating RTMP extension", e);
            }
            if (rtmpDataSource == null) {
                rtmpDataSource = baseDataSource;
            }
        }
        return rtmpDataSource;
    }

    private DataSource getDataSchemeDataSource() {
        if (dataSchemeDataSource == null) {
            dataSchemeDataSource = new DataSchemeDataSource();
            addListenersToDataSource(dataSchemeDataSource);
        }
        return dataSchemeDataSource;
    }

    private DataSource getRawResourceDataSource() {
        if (rawResourceDataSource == null) {
            rawResourceDataSource = new RawResourceDataSource(context);
            addListenersToDataSource(rawResourceDataSource);
        }
        return rawResourceDataSource;
    }

    private void addListenersToDataSource(DataSource dataSource) {
        for (int i = 0; i < transferListeners.size(); i++) {
            dataSource.addTransferListener(transferListeners.get(i));
        }
    }

    private void maybeAddListenerToDataSource(
            @Nullable DataSource dataSource, TransferListener listener) {
        if (dataSource != null) {
            dataSource.addTransferListener(listener);
        }
    }
}
