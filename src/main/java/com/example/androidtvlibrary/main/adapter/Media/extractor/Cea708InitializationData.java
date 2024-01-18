package com.example.androidtvlibrary.main.adapter.Media.extractor;

import java.util.Collections;
import java.util.List;

public final class Cea708InitializationData {

    /**
     * Whether the closed caption service is formatted for displays with 16:9 aspect ratio. If false,
     * the closed caption service is formatted for 4:3 displays.
     */
    public final boolean isWideAspectRatio;

    private Cea708InitializationData(List<byte[]> initializationData) {
        isWideAspectRatio = initializationData.get(0)[0] != 0;
    }

    /**
     * Returns an object representation of CEA-708 initialization data
     *
     * @param initializationData Binary CEA-708 initialization data.
     * @return The object representation.
     */
    public static Cea708InitializationData fromData(List<byte[]> initializationData) {
        return new Cea708InitializationData(initializationData);
    }

    /**
     * Builds binary CEA-708 initialization data.
     *
     * @param isWideAspectRatio Whether the closed caption service is formatted for displays with 16:9
     *     aspect ratio.
     * @return Binary CEA-708 initializaton data.
     */
    public static List<byte[]> buildData(boolean isWideAspectRatio) {
        return Collections.singletonList(new byte[] {(byte) (isWideAspectRatio ? 1 : 0)});
    }
}
