package com.example.androidtvlibrary.main.adapter.Media.extractor;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;

public final class DolbyVisionConfig {

    /**
     * Parses Dolby Vision configuration data.
     *
     * @param data A {@link ParsableByteArray}, whose position is set to the start of the Dolby Vision
     *     configuration data to parse.
     * @return The {@link DolbyVisionConfig} corresponding to the configuration, or {@code null} if
     *     the configuration isn't supported.
     */
    @Nullable
    public static DolbyVisionConfig parse(ParsableByteArray data) {
        data.skipBytes(2); // dv_version_major, dv_version_minor
        int profileData = data.readUnsignedByte();
        int dvProfile = (profileData >> 1);
        int dvLevel = ((profileData & 0x1) << 5) | ((data.readUnsignedByte() >> 3) & 0x1F);
        String codecsPrefix;
        if (dvProfile == 4 || dvProfile == 5 || dvProfile == 7) {
            codecsPrefix = "dvhe";
        } else if (dvProfile == 8) {
            codecsPrefix = "hev1";
        } else if (dvProfile == 9) {
            codecsPrefix = "avc3";
        } else {
            return null;
        }
        String codecs = codecsPrefix + ".0" + dvProfile + ".0" + dvLevel;
        return new DolbyVisionConfig(dvProfile, dvLevel, codecs);
    }

    /** The profile number. */
    public final int profile;
    /** The level number. */
    public final int level;
    /** The RFC 6381 codecs string. */
    public final String codecs;

    private DolbyVisionConfig(int profile, int level, String codecs) {
        this.profile = profile;
        this.level = level;
        this.codecs = codecs;
    }
}
