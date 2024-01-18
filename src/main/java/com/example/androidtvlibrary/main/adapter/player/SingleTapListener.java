package com.example.androidtvlibrary.main.adapter.player;

import android.view.MotionEvent;

public interface SingleTapListener {
    /**
     * Notified when a tap occurs with the up {@link MotionEvent} that triggered it.
     *
     * @param e The up motion event that completed the first tap.
     * @return Whether the event is consumed.
     */
    boolean onSingleTapUp(MotionEvent e);
}
