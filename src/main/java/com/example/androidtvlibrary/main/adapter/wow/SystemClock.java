package com.example.androidtvlibrary.main.adapter.wow;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

public class SystemClock implements Clock {

    protected SystemClock() {}

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long elapsedRealtime() {
        return android.os.SystemClock.elapsedRealtime();
    }

    @Override
    public long uptimeMillis() {
        return android.os.SystemClock.uptimeMillis();
    }

    @Override
    public void sleep(long sleepTimeMs) {
        android.os.SystemClock.sleep(sleepTimeMs);
    }

    @Override
    public HandlerWrapper createHandler(Looper looper, @Nullable Handler.Callback callback) {
        return new SystemHandlerWrapper(new Handler(looper, callback));
    }
}
