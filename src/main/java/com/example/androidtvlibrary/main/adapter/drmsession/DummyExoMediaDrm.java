package com.example.androidtvlibrary.main.adapter.drmsession;

import android.media.MediaDrmException;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.androidtvlibrary.main.adapter.DrmInitData;
import com.example.androidtvlibrary.main.adapter.Util;
import com.example.androidtvlibrary.main.adapter.wow.ExoMediaCrypto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(18)
public final class DummyExoMediaDrm<T extends ExoMediaCrypto> implements ExoMediaDrm<T> {

    /** Returns a new instance. */
    @SuppressWarnings("unchecked")
    public static <T extends ExoMediaCrypto> DummyExoMediaDrm<T> getInstance() {
        return (DummyExoMediaDrm<T>) new DummyExoMediaDrm<>();
    }

    @Override
    public void setOnEventListener(OnEventListener<? super T> listener) {
        // Do nothing.
    }

    @Override
    public void setOnKeyStatusChangeListener(OnKeyStatusChangeListener<? super T> listener) {
        // Do nothing.
    }

    @Override
    public byte[] openSession() throws MediaDrmException {
        throw new MediaDrmException("Attempting to open a session using a dummy ExoMediaDrm.");
    }

    @Override
    public void closeSession(byte[] sessionId) {
        // Do nothing.
    }

    @Override
    public KeyRequest getKeyRequest(
            byte[] scope,
            @Nullable List<DrmInitData.SchemeData> schemeDatas,
            int keyType,
            @Nullable HashMap<String, String> optionalParameters) {
        // Should not be invoked. No session should exist.
        throw new IllegalStateException();
    }

    @Nullable
    @Override
    public byte[] provideKeyResponse(byte[] scope, byte[] response) {
        // Should not be invoked. No session should exist.
        throw new IllegalStateException();
    }

    @Override
    public ProvisionRequest getProvisionRequest() {
        // Should not be invoked. No provision should be required.
        throw new IllegalStateException();
    }

    @Override
    public void provideProvisionResponse(byte[] response) {
        // Should not be invoked. No provision should be required.
        throw new IllegalStateException();
    }

    @Override
    public Map<String, String> queryKeyStatus(byte[] sessionId) {
        // Should not be invoked. No session should exist.
        throw new IllegalStateException();
    }

    @Override
    public void acquire() {
        // Do nothing.
    }

    @Override
    public void release() {
        // Do nothing.
    }

    @Override
    public void restoreKeys(byte[] sessionId, byte[] keySetId) {
        // Should not be invoked. No session should exist.
        throw new IllegalStateException();
    }

    @Override
    @Nullable
    public PersistableBundle getMetrics() {
        return null;
    }

    @Override
    public String getPropertyString(String propertyName) {
        return "";
    }

    @Override
    public byte[] getPropertyByteArray(String propertyName) {
        return Util.EMPTY_BYTE_ARRAY;
    }

    @Override
    public void setPropertyString(String propertyName, String value) {
        // Do nothing.
    }

    @Override
    public void setPropertyByteArray(String propertyName, byte[] value) {
        // Do nothing.
    }

    @Override
    public T createMediaCrypto(byte[] sessionId) {
        // Should not be invoked. No session should exist.
        throw new IllegalStateException();
    }

    @Override
    @Nullable
    public Class<T> getExoMediaCryptoType() {
        // No ExoMediaCrypto type is supported.
        return null;
    }
}
