package com.example.androidtvlibrary.main.adapter.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

public final class WakeLockManager {

    private static final String TAG = "WakeLockManager";
    private static final String WAKE_LOCK_TAG = "ExoPlayer:WakeLockManager";

    @Nullable
    private final PowerManager powerManager;
    @Nullable private PowerManager.WakeLock wakeLock;
    private boolean enabled;
    private boolean stayAwake;

    public WakeLockManager(Context context) {
        powerManager =
                (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
    }

    /**
     * Sets whether to enable the acquiring and releasing of the {@link PowerManager.WakeLock}.
     *
     * <p>By default, wake lock handling is not enabled. Enabling this will acquire the wake lock if
     * necessary. Disabling this will release the wake lock if it is held.
     *
     * <p>Enabling {@link PowerManager.WakeLock} requires the {@link android.Manifest.permission#WAKE_LOCK}.
     *
     * @param enabled True if the player should handle a {@link PowerManager.WakeLock}, false otherwise.
     */
    public void setEnabled(boolean enabled) {
        if (enabled) {
            if (wakeLock == null) {
                if (powerManager == null) {
                    Log.w(TAG, "PowerManager is null, therefore not creating the WakeLock.");
                    return;
                }
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
                wakeLock.setReferenceCounted(false);
            }
        }

        this.enabled = enabled;
        updateWakeLock();
    }

    /**
     * Sets whether to acquire or release the {@link PowerManager.WakeLock}.
     *
     * <p>Please note this method requires wake lock handling to be enabled through setEnabled(boolean
     * enable) to actually have an impact on the {@link PowerManager.WakeLock}.
     *
     * @param stayAwake True if the player should acquire the {@link PowerManager.WakeLock}. False if the player
     *     should release.
     */
    public void setStayAwake(boolean stayAwake) {
        this.stayAwake = stayAwake;
        updateWakeLock();
    }

    // WakelockTimeout suppressed because the time the wake lock is needed for is unknown (could be
    // listening to radio with screen off for multiple hours), therefore we can not determine a
    // reasonable timeout that would not affect the user.
    @SuppressLint("WakelockTimeout")
    private void updateWakeLock() {
        if (wakeLock == null) {
            return;
        }

        if (enabled && stayAwake) {
            wakeLock.acquire();
        } else {
            wakeLock.release();
        }
    }
}
