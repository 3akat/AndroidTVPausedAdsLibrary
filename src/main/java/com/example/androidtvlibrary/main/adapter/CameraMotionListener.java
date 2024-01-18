package com.example.androidtvlibrary.main.adapter;

public interface CameraMotionListener {

    /**
     * Called when a new camera motion is read. This method is called on the playback thread.
     *
     * @param timeUs The presentation time of the data.
     * @param rotation Angle axis orientation in radians representing the rotation from camera
     *     coordinate system to world coordinate system.
     */
    void onCameraMotion(long timeUs, float[] rotation);

    /** Called when the camera motion track position is reset or the track is disabled. */
    void onCameraMotionReset();
}
