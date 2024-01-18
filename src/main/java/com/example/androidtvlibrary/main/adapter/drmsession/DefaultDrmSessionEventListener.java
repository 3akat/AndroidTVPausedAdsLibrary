package com.example.androidtvlibrary.main.adapter.drmsession;

public interface DefaultDrmSessionEventListener {

    /** Called each time a drm session is acquired. */
    default void onDrmSessionAcquired() {}

    /** Called each time keys are loaded. */
    default void onDrmKeysLoaded() {}

    default void onDrmSessionManagerError(Exception error) {}

    /** Called each time offline keys are restored. */
    default void onDrmKeysRestored() {}

    /** Called each time offline keys are removed. */
    default void onDrmKeysRemoved() {}

    /** Called each time a drm session is released. */
    default void onDrmSessionReleased() {}
}
