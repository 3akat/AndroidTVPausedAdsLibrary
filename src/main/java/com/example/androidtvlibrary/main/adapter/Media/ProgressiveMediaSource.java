package com.example.androidtvlibrary.main.adapter.Media;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.DataSource;
import com.example.androidtvlibrary.main.adapter.Timeline;
import com.example.androidtvlibrary.main.adapter.TransferListener;
import com.example.androidtvlibrary.main.adapter.mp3.ExtractorsFactory;
import com.example.androidtvlibrary.main.adapter.wow.Allocator;
import com.example.androidtvlibrary.main.adapter.wow.DrmSession;
import com.example.androidtvlibrary.main.adapter.wow.MediaPeriod;
import com.example.androidtvlibrary.main.adapter.wow.SequenceableLoader;

import java.io.IOException;

public final class ProgressiveMediaSource extends BaseMediaSource
        implements ProgressiveMediaPeriod.Listener {

    /** Factory for {@link ProgressiveMediaSource}s. */
    public static final class Factory implements MediaSourceFactory {

        private final DataSource.Factory dataSourceFactory;

        private ExtractorsFactory extractorsFactory;
        @Nullable
        private String customCacheKey;
        @Nullable private Object tag;
        private DrmSessionManager<?> drmSessionManager;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
        private int continueLoadingCheckIntervalBytes;
        private boolean isCreateCalled;

        /**
         * Creates a new factory for {@link ProgressiveMediaSource}s, using the extractors provided by
         * {@link DefaultExtractorsFactory}.
         *
         * @param dataSourceFactory A factory for {@link DataSource}s to read the media.
         */
        public Factory(DataSource.Factory dataSourceFactory) {
            this(dataSourceFactory, new DefaultExtractorsFactory());
        }

        /**
         * Creates a new factory for {@link ProgressiveMediaSource}s.
         *
         * @param dataSourceFactory A factory for {@link DataSource}s to read the media.
         * @param extractorsFactory A factory for extractors used to extract media from its container.
         */
        public Factory(DataSource.Factory dataSourceFactory, ExtractorsFactory extractorsFactory) {
            this.dataSourceFactory = dataSourceFactory;
            this.extractorsFactory = extractorsFactory;
            drmSessionManager = DrmSessionManager.getDummyDrmSessionManager();
            loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
            continueLoadingCheckIntervalBytes = DEFAULT_LOADING_CHECK_INTERVAL_BYTES;
        }

        /**
         * Sets the factory for {@link Extractor}s to process the media stream. The default value is an
         * instance of {@link DefaultExtractorsFactory}.
         *
         * @param extractorsFactory A factory for {@link Extractor}s to process the media stream. If the
         *     possible formats are known, pass a factory that instantiates extractors for those
         *     formats.
         * @return This factory, for convenience.
         * @throws IllegalStateException If {@link #createMediaSource(Uri)} has already been called.
         * @deprecated Pass the {@link ExtractorsFactory} via {@link #Factory(DataSource.Factory,
         *     ExtractorsFactory)}. This is necessary so that proguard can treat the default extractors
         *     factory as unused.
         */
        @Deprecated
        public Factory setExtractorsFactory(ExtractorsFactory extractorsFactory) {
            Assertions.checkState(!isCreateCalled);
            this.extractorsFactory = extractorsFactory;
            return this;
        }

        /**
         * Sets the custom key that uniquely identifies the original stream. Used for cache indexing.
         * The default value is {@code null}.
         *
         * @param customCacheKey A custom key that uniquely identifies the original stream. Used for
         *     cache indexing.
         * @return This factory, for convenience.
         * @throws IllegalStateException If {@link #createMediaSource(Uri)} has already been called.
         */
        public Factory setCustomCacheKey(@Nullable String customCacheKey) {
            Assertions.checkState(!isCreateCalled);
            this.customCacheKey = customCacheKey;
            return this;
        }

        /**
         * Sets a tag for the media source which will be published in the {@link
         * Timeline} of the source as {@link
         * Timeline.Window#tag}.
         *
         * @param tag A tag for the media source.
         * @return This factory, for convenience.
         * @throws IllegalStateException If {@link #createMediaSource(Uri)} has already been called.
         */
        public Factory setTag(Object tag) {
            Assertions.checkState(!isCreateCalled);
            this.tag = tag;
            return this;
        }

        public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
            Assertions.checkState(!isCreateCalled);
            this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
            return this;
        }

        /**
         * Sets the number of bytes that should be loaded between each invocation of {@link
         * MediaPeriod.Callback#onContinueLoadingRequested(SequenceableLoader)}. The default value is
         * {@link #DEFAULT_LOADING_CHECK_INTERVAL_BYTES}.
         *
         * @param continueLoadingCheckIntervalBytes The number of bytes that should be loaded between
         *     each invocation of {@link
         *     MediaPeriod.Callback#onContinueLoadingRequested(SequenceableLoader)}.
         * @return This factory, for convenience.
         * @throws IllegalStateException If {@link #createMediaSource(Uri)} has already been called.
         */
        public Factory setContinueLoadingCheckIntervalBytes(int continueLoadingCheckIntervalBytes) {
            Assertions.checkState(!isCreateCalled);
            this.continueLoadingCheckIntervalBytes = continueLoadingCheckIntervalBytes;
            return this;
        }

        /**
         * Sets the {@link DrmSessionManager} to use for acquiring {@link DrmSession DrmSessions}. The
         * default value is {@link DrmSessionManager#DUMMY}.
         *
         * @param drmSessionManager The {@link DrmSessionManager}.
         * @return This factory, for convenience.
         * @throws IllegalStateException If one of the {@code create} methods has already been called.
         */
        @Override
        public Factory setDrmSessionManager(DrmSessionManager<?> drmSessionManager) {
            Assertions.checkState(!isCreateCalled);
            this.drmSessionManager =
                    drmSessionManager != null
                            ? drmSessionManager
                            : DrmSessionManager.getDummyDrmSessionManager();
            return this;
        }

        /**
         * Returns a new {@link ProgressiveMediaSource} using the current parameters.
         *
         * @param uri The {@link Uri}.
         * @return The new {@link ProgressiveMediaSource}.
         */
        @Override
        public ProgressiveMediaSource createMediaSource(Uri uri) {
            isCreateCalled = true;
            return new ProgressiveMediaSource(
                    uri,
                    dataSourceFactory,
                    extractorsFactory,
                    drmSessionManager,
                    loadErrorHandlingPolicy,
                    customCacheKey,
                    continueLoadingCheckIntervalBytes,
                    tag);
        }

        @Override
        public int[] getSupportedTypes() {
            return new int[] {C.TYPE_OTHER};
        }
    }

    /**
     * The default number of bytes that should be loaded between each each invocation of {@link
     * MediaPeriod.Callback#onContinueLoadingRequested(SequenceableLoader)}.
     */
    public static final int DEFAULT_LOADING_CHECK_INTERVAL_BYTES = 1024 * 1024;

    private final Uri uri;
    private final DataSource.Factory dataSourceFactory;
    private final ExtractorsFactory extractorsFactory;
    private final DrmSessionManager<?> drmSessionManager;
    private final LoadErrorHandlingPolicy loadableLoadErrorHandlingPolicy;
    @Nullable private final String customCacheKey;
    private final int continueLoadingCheckIntervalBytes;
    @Nullable private final Object tag;

    private long timelineDurationUs;
    private boolean timelineIsSeekable;
    private boolean timelineIsLive;
    @Nullable private TransferListener transferListener;

    // TODO: Make private when ExtractorMediaSource is deleted.
    /* package */ ProgressiveMediaSource(
            Uri uri,
            DataSource.Factory dataSourceFactory,
            ExtractorsFactory extractorsFactory,
            DrmSessionManager<?> drmSessionManager,
            LoadErrorHandlingPolicy loadableLoadErrorHandlingPolicy,
            @Nullable String customCacheKey,
            int continueLoadingCheckIntervalBytes,
            @Nullable Object tag) {
        this.uri = uri;
        this.dataSourceFactory = dataSourceFactory;
        this.extractorsFactory = extractorsFactory;
        this.drmSessionManager = drmSessionManager;
        this.loadableLoadErrorHandlingPolicy = loadableLoadErrorHandlingPolicy;
        this.customCacheKey = customCacheKey;
        this.continueLoadingCheckIntervalBytes = continueLoadingCheckIntervalBytes;
        this.timelineDurationUs = C.TIME_UNSET;
        this.tag = tag;
    }

    @Override
    @Nullable
    public Object getTag() {
        return tag;
    }

    @Override
    protected void prepareSourceInternal(@Nullable TransferListener mediaTransferListener) {
        transferListener = mediaTransferListener;
        drmSessionManager.prepare();
        notifySourceInfoRefreshed(timelineDurationUs, timelineIsSeekable, timelineIsLive);
    }

    @Override
    public void maybeThrowSourceInfoRefreshError() throws IOException {
        // Do nothing.
    }

    @Override
    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator, long startPositionUs) {
        DataSource dataSource = dataSourceFactory.createDataSource();
        if (transferListener != null) {
            dataSource.addTransferListener(transferListener);
        }
        return new ProgressiveMediaPeriod(
                uri,
                dataSource,
                extractorsFactory.createExtractors(),
                drmSessionManager,
                loadableLoadErrorHandlingPolicy,
                createEventDispatcher(id),
                this,
                allocator,
                customCacheKey,
                continueLoadingCheckIntervalBytes);
    }

    @Override
    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((ProgressiveMediaPeriod) mediaPeriod).release();
    }

    @Override
    protected void releaseSourceInternal() {
        drmSessionManager.release();
    }

    // ProgressiveMediaPeriod.Listener implementation.

    @Override
    public void onSourceInfoRefreshed(long durationUs, boolean isSeekable, boolean isLive) {
        // If we already have the duration from a previous source info refresh, use it.
        durationUs = durationUs == C.TIME_UNSET ? timelineDurationUs : durationUs;
        if (timelineDurationUs == durationUs
                && timelineIsSeekable == isSeekable
                && timelineIsLive == isLive) {
            // Suppress no-op source info changes.
            return;
        }
        notifySourceInfoRefreshed(durationUs, isSeekable, isLive);
    }

    // Internal methods.

    private void notifySourceInfoRefreshed(long durationUs, boolean isSeekable, boolean isLive) {
        timelineDurationUs = durationUs;
        timelineIsSeekable = isSeekable;
        timelineIsLive = isLive;
        // TODO: Split up isDynamic into multiple fields to indicate which values may change. Then
        // indicate that the duration may change until it's known. See [internal: b/69703223].
        refreshSourceInfo(
                new SinglePeriodTimeline(
                        timelineDurationUs,
                        timelineIsSeekable,
                        /* isDynamic= */ false,
                        /* isLive= */ timelineIsLive,
                        /* manifest= */ null,
                        tag));
    }
}
