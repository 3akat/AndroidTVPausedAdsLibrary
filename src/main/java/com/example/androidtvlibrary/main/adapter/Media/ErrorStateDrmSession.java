package com.example.androidtvlibrary.main.adapter.Media;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.wow.DrmSession;
import com.example.androidtvlibrary.main.adapter.wow.ExoMediaCrypto;

import java.util.Map;

public final class ErrorStateDrmSession<T extends ExoMediaCrypto> implements DrmSession<T> {

    private final DrmSessionException error;

    public ErrorStateDrmSession(DrmSessionException error) {
        this.error = Assertions.checkNotNull(error);
    }

    @Override
    public int getState() {
        return STATE_ERROR;
    }

    @Override
    public boolean playClearSamplesWithoutKeys() {
        return false;
    }

    @Override
    @Nullable
    public DrmSessionException getError() {
        return error;
    }

    @Override
    @Nullable
    public T getMediaCrypto() {
        return null;
    }

    @Override
    @Nullable
    public Map<String, String> queryKeyStatus() {
        return null;
    }

    @Override
    @Nullable
    public byte[] getOfflineLicenseKeySetId() {
        return null;
    }

    @Override
    public void acquire() {
        // Do nothing.
    }

    @Override
    public void release() {
        // Do nothing.
    }
}
