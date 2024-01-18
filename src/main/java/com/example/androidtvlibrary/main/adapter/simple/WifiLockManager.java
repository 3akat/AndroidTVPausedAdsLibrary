package com.example.androidtvlibrary.main.adapter.simple;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.Nullable;

public final class WifiLockManager {

    private static final String TAG = "WifiLockManager";
    private static final String WIFI_LOCK_TAG = "ExoPlayer:WifiLockManager";

    @Nullable
    private final WifiManager wifiManager;
    @Nullable private WifiManager.WifiLock wifiLock;
    private boolean enabled;
    private boolean stayAwake;

    public WifiLockManager(Context context) {
        wifiManager =
                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Sets whether to enable the usage of a {@link WifiManager.WifiLock}.
     *
     * <p>By default, wifi lock handling is not enabled. Enabling will acquire the wifi lock if
     * necessary. Disabling will release the wifi lock if held.
     *
     * <p>Enabling {@link WifiManager.WifiLock} requires the {@link android.Manifest.permission#WAKE_LOCK}.
     *
     * @param enabled True if the player should handle a {@link WifiManager.WifiLock}.
     */
    public void setEnabled(boolean enabled) {
        if (enabled && wifiLock == null) {
            if (wifiManager == null) {
                Log.w(TAG, "WifiManager is null, therefore not creating the WifiLock.");
                return;
            }
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, WIFI_LOCK_TAG);
            wifiLock.setReferenceCounted(false);
        }

        this.enabled = enabled;
        updateWifiLock();
    }

    /**
     * Sets whether to acquire or release the {@link WifiManager.WifiLock}.
     *
     * <p>The wifi lock will not be acquired unless handling has been enabled through {@link
     * #setEnabled(boolean)}.
     *
     * @param stayAwake True if the player should acquire the {@link WifiManager.WifiLock}. False if it should
     *     release.
     */
    public void setStayAwake(boolean stayAwake) {
        this.stayAwake = stayAwake;
        updateWifiLock();
    }

    private void updateWifiLock() {
        if (wifiLock == null) {
            return;
        }

        if (enabled && stayAwake) {
            wifiLock.acquire();
        } else {
            wifiLock.release();
        }
    }
}
