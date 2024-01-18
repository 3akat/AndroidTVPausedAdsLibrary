package com.example.androidtvlibrary.main.adapter.player;

import androidx.annotation.IntDef;

import com.example.androidtvlibrary.main.adapter.TestPlayer;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RepeatModeUtil {

    // LINT.IfChange
    /**
     * Set of repeat toggle modes. Can be combined using bit-wise operations. Possible flag values are
     * {@link #REPEAT_TOGGLE_MODE_NONE}, {@link #REPEAT_TOGGLE_MODE_ONE} and {@link
     * #REPEAT_TOGGLE_MODE_ALL}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
            flag = true,
            value = {REPEAT_TOGGLE_MODE_NONE, REPEAT_TOGGLE_MODE_ONE, REPEAT_TOGGLE_MODE_ALL})
    public @interface RepeatToggleModes {}
    /**
     * All repeat mode buttons disabled.
     */
    public static final int REPEAT_TOGGLE_MODE_NONE = 0;
    /**
     * "Repeat One" button enabled.
     */
    public static final int REPEAT_TOGGLE_MODE_ONE = 1;
    /** "Repeat All" button enabled. */
    public static final int REPEAT_TOGGLE_MODE_ALL = 1 << 1; // 2
    // LINT.ThenChange(../../../../../../../../../ui/src/main/res/values/attrs.xml)

    private RepeatModeUtil() {
        // Prevent instantiation.
    }

    /**
     * Gets the next repeat mode out of {@code enabledModes} starting from {@code currentMode}.
     *
     * @param currentMode The current repeat mode.
     * @param enabledModes Bitmask of enabled modes.
     * @return The next repeat mode.
     */
    public static @TestPlayer.RepeatMode int getNextRepeatMode(@TestPlayer.RepeatMode int currentMode,
                                                           int enabledModes) {
        for (int offset = 1; offset <= 2; offset++) {
            @TestPlayer.RepeatMode int proposedMode = (currentMode + offset) % 3;
            if (isRepeatModeEnabled(proposedMode, enabledModes)) {
                return proposedMode;
            }
        }
        return currentMode;
    }

    /**
     * Verifies whether a given {@code repeatMode} is enabled in the bitmask {@code enabledModes}.
     *
     * @param repeatMode The mode to check.
     * @param enabledModes The bitmask representing the enabled modes.
     * @return {@code true} if enabled.
     */
    public static boolean isRepeatModeEnabled(@TestPlayer.RepeatMode int repeatMode, int enabledModes) {
        switch (repeatMode) {
            case TestPlayer.REPEAT_MODE_OFF:
                return true;
            case TestPlayer.REPEAT_MODE_ONE:
                return (enabledModes & REPEAT_TOGGLE_MODE_ONE) != 0;
            case TestPlayer.REPEAT_MODE_ALL:
                return (enabledModes & REPEAT_TOGGLE_MODE_ALL) != 0;
            default:
                return false;
        }
    }

}
