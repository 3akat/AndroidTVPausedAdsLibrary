package com.example.androidtvlibrary.main.adapter.wow;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

public interface HandlerWrapper {

    /** @see Handler#getLooper() */
    Looper getLooper();

    /** @see Handler#obtainMessage(int) */
    Message obtainMessage(int what);

    /** @see Handler#obtainMessage(int, Object) */
    Message obtainMessage(int what, @Nullable Object obj);

    /** @see Handler#obtainMessage(int, int, int) */
    Message obtainMessage(int what, int arg1, int arg2);

    /** @see Handler#obtainMessage(int, int, int, Object) */
    Message obtainMessage(int what, int arg1, int arg2, @Nullable Object obj);

    /** @see Handler#sendEmptyMessage(int) */
    boolean sendEmptyMessage(int what);

    /** @see Handler#sendEmptyMessageAtTime(int, long) */
    boolean sendEmptyMessageAtTime(int what, long uptimeMs);

    /** @see Handler#removeMessages(int) */
    void removeMessages(int what);

    /** @see Handler#removeCallbacksAndMessages(Object) */
    void removeCallbacksAndMessages(@Nullable Object token);

    /** @see Handler#post(Runnable) */
    boolean post(Runnable runnable);

    /** @see Handler#postDelayed(Runnable, long) */
    boolean postDelayed(Runnable runnable, long delayMs);
}
