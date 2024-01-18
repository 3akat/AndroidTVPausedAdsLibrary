package com.example.androidtvlibrary.main.adapter.Media.extractor;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.ParserException;

import java.util.Collections;
import java.util.List;

public final class HevcConfig {

    @Nullable
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;

    /**
     * Parses HEVC configuration data.
     *
     * @param data A {@link ParsableByteArray}, whose position is set to the start of the HEVC
     *     configuration data to parse.
     * @return A parsed representation of the HEVC configuration data.
     * @throws ParserException If an error occurred parsing the data.
     */
    public static HevcConfig parse(ParsableByteArray data) throws ParserException {
        try {
            data.skipBytes(21); // Skip to the NAL unit length size field.
            int lengthSizeMinusOne = data.readUnsignedByte() & 0x03;

            // Calculate the combined size of all VPS/SPS/PPS bitstreams.
            int numberOfArrays = data.readUnsignedByte();
            int csdLength = 0;
            int csdStartPosition = data.getPosition();
            for (int i = 0; i < numberOfArrays; i++) {
                data.skipBytes(1); // completeness (1), nal_unit_type (7)
                int numberOfNalUnits = data.readUnsignedShort();
                for (int j = 0; j < numberOfNalUnits; j++) {
                    int nalUnitLength = data.readUnsignedShort();
                    csdLength += 4 + nalUnitLength; // Start code and NAL unit.
                    data.skipBytes(nalUnitLength);
                }
            }

            // Concatenate the codec-specific data into a single buffer.
            data.setPosition(csdStartPosition);
            byte[] buffer = new byte[csdLength];
            int bufferPosition = 0;
            for (int i = 0; i < numberOfArrays; i++) {
                data.skipBytes(1); // completeness (1), nal_unit_type (7)
                int numberOfNalUnits = data.readUnsignedShort();
                for (int j = 0; j < numberOfNalUnits; j++) {
                    int nalUnitLength = data.readUnsignedShort();
                    System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, buffer, bufferPosition,
                            NalUnitUtil.NAL_START_CODE.length);
                    bufferPosition += NalUnitUtil.NAL_START_CODE.length;
                    System
                            .arraycopy(data.data, data.getPosition(), buffer, bufferPosition, nalUnitLength);
                    bufferPosition += nalUnitLength;
                    data.skipBytes(nalUnitLength);
                }
            }

            List<byte[]> initializationData = csdLength == 0 ? null : Collections.singletonList(buffer);
            return new HevcConfig(initializationData, lengthSizeMinusOne + 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParserException("Error parsing HEVC config", e);
        }
    }

    private HevcConfig(@Nullable List<byte[]> initializationData, int nalUnitLengthFieldLength) {
        this.initializationData = initializationData;
        this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
    }

}
