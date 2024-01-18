package com.example.androidtvlibrary.main.adapter.Media;

import android.os.Looper;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.DrmInitData;
import com.example.androidtvlibrary.main.adapter.wow.DrmSession;
import com.example.androidtvlibrary.main.adapter.wow.ExoMediaCrypto;

public interface DrmSessionManager<T extends ExoMediaCrypto> {

    /** Returns {@link #DUMMY}. */
    @SuppressWarnings("unchecked")
    static <T extends ExoMediaCrypto> DrmSessionManager<T> getDummyDrmSessionManager() {
        return (DrmSessionManager<T>) DUMMY;
    }

    /** {@link DrmSessionManager} that supports no DRM schemes. */
    DrmSessionManager<ExoMediaCrypto> DUMMY =
            new DrmSessionManager<ExoMediaCrypto>() {

                @Override
                public boolean canAcquireSession(DrmInitData drmInitData) {
                    return false;
                }

                @Override
                public DrmSession<ExoMediaCrypto> acquireSession(
                        Looper playbackLooper, DrmInitData drmInitData) {
                    return new ErrorStateDrmSession<>(
                            new DrmSession.DrmSessionException(
                                    new UnsupportedDrmException(UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME)));
                }

                @Override
                @Nullable
                public Class<ExoMediaCrypto> getExoMediaCryptoType(DrmInitData drmInitData) {
                    return null;
                }
            };

    /**
     * Acquires any required resources.
     *
     * <p>{@link #release()} must be called to ensure the acquired resources are released. After
     * releasing, an instance may be re-prepared.
     */
    default void prepare() {
        // Do nothing.
    }

    /** Releases any acquired resources. */
    default void release() {
        // Do nothing.
    }

    /**
     * Returns whether the manager is capable of acquiring a session for the given
     * {@link DrmInitData}.
     *
     * @param drmInitData DRM initialization data.
     * @return Whether the manager is capable of acquiring a session for the given
     *     {@link DrmInitData}.
     */
    boolean canAcquireSession(DrmInitData drmInitData);

    /**
     * Returns a {@link DrmSession} that does not execute key requests, with an incremented reference
     * count. When the caller no longer needs to use the instance, it must call {@link
     * DrmSession#release()} to decrement the reference count.
     *
     * <p>Placeholder {@link DrmSession DrmSessions} may be used to configure secure decoders for
     * playback of clear content periods. This can reduce the cost of transitioning between clear and
     * encrypted content periods.
     *
     * @param playbackLooper The looper associated with the media playback thread.
     * @param trackType The type of the track to acquire a placeholder session for. Must be one of the
     *     {@link C}{@code .TRACK_TYPE_*} constants.
     * @return The placeholder DRM session, or null if this DRM session manager does not support
     *     placeholder sessions.
     */
    @Nullable
    default DrmSession<T> acquirePlaceholderSession(Looper playbackLooper, int trackType) {
        return null;
    }

    /**
     * Returns a {@link DrmSession} for the specified {@link DrmInitData}, with an incremented
     * reference count. When the caller no longer needs to use the instance, it must call {@link
     * DrmSession#release()} to decrement the reference count.
     *
     * @param playbackLooper The looper associated with the media playback thread.
     * @param drmInitData DRM initialization data. All contained {@link DrmInitData.SchemeData}s must contain
     *     non-null {@link DrmInitData.SchemeData#data}.
     * @return The DRM session.
     */
    DrmSession<T> acquireSession(Looper playbackLooper, DrmInitData drmInitData);

    /**
     * Returns the {@link ExoMediaCrypto} type returned by sessions acquired using the given {@link
     * DrmInitData}, or null if a session cannot be acquired with the given {@link DrmInitData}.
     */
    @Nullable
    Class<? extends ExoMediaCrypto> getExoMediaCryptoType(DrmInitData drmInitData);
}
