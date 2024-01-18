package com.example.androidtvlibrary.main.adapter.player;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.BinderThread;
import androidx.annotation.Nullable;

class TouchTracker extends GestureDetector.SimpleOnGestureListener
        implements View.OnTouchListener, OrientationListener.Listener {

    /* package */ interface Listener {
        void onScrollChange(PointF scrollOffsetDegrees);
    }

    // Touch input won't change the pitch beyond +/- 45 degrees. This reduces awkward situations
    // where the touch-based pitch and gyro-based pitch interact badly near the poles.
    /* package */ static final float MAX_PITCH_DEGREES = 45;

    // With every touch event, update the accumulated degrees offset by the new pixel amount.
    private final PointF previousTouchPointPx = new PointF();
    private final PointF accumulatedTouchOffsetDegrees = new PointF();

    private final Listener listener;
    private final float pxPerDegrees;
    private final GestureDetector gestureDetector;
    // The conversion from touch to yaw & pitch requires compensating for device roll. This is set
    // on the sensor thread and read on the UI thread.
    private volatile float roll;
    @Nullable
    private SingleTapListener singleTapListener;

    @SuppressWarnings({
            "nullness:assignment.type.incompatible",
            "nullness:argument.type.incompatible"
    })
    public TouchTracker(Context context, Listener listener, float pxPerDegrees) {
        this.listener = listener;
        this.pxPerDegrees = pxPerDegrees;
        gestureDetector = new GestureDetector(context, this);
        roll = SphericalGLSurfaceView.UPRIGHT_ROLL;
    }

    public void setSingleTapListener(@Nullable SingleTapListener listener) {
        singleTapListener = listener;
    }

    /**
     * Converts ACTION_MOVE events to pitch & yaw events while compensating for device roll.
     *
     * @return true if we handled the event
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Initialize drag gesture.
        previousTouchPointPx.set(e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Calculate the touch delta in screen space.
        float touchX = (e2.getX() - previousTouchPointPx.x) / pxPerDegrees;
        float touchY = (e2.getY() - previousTouchPointPx.y) / pxPerDegrees;
        previousTouchPointPx.set(e2.getX(), e2.getY());

        float r = roll; // Copy volatile state.
        float cr = (float) Math.cos(r);
        float sr = (float) Math.sin(r);
        // To convert from screen space to the 3D space, we need to adjust the drag vector based
        // on the roll of the phone. This is standard rotationMatrix(roll) * vector math but has
        // an inverted y-axis due to the screen-space coordinates vs GL coordinates.
        // Handle yaw.
        accumulatedTouchOffsetDegrees.x -= cr * touchX - sr * touchY;
        // Handle pitch and limit it to 45 degrees.
        accumulatedTouchOffsetDegrees.y += sr * touchX + cr * touchY;
        accumulatedTouchOffsetDegrees.y =
                Math.max(-MAX_PITCH_DEGREES, Math.min(MAX_PITCH_DEGREES, accumulatedTouchOffsetDegrees.y));

        listener.onScrollChange(accumulatedTouchOffsetDegrees);
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (singleTapListener != null) {
            return singleTapListener.onSingleTapUp(e);
        }
        return false;
    }

    @Override
    @BinderThread
    public void onOrientationChange(float[] deviceOrientationMatrix, float roll) {
        // We compensate for roll by rotating in the opposite direction.
        this.roll = -roll;
    }
}