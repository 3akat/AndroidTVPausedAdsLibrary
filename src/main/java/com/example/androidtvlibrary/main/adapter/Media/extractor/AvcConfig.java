package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.ParserException;

import java.util.ArrayList;
import java.util.List;
public final class AvcConfig {

    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;
    public final int width;
    public final int height;
    public final float pixelWidthAspectRatio;

    /**
     * Parses AVC configuration data.
     *
     * @param data A {@link ParsableByteArray}, whose position is set to the start of the AVC
     *     configuration data to parse.
     * @return A parsed representation of the HEVC configuration data.
     * @throws ParserException If an error occurred parsing the data.
     */
    public static AvcConfig parse(ParsableByteArray data) throws ParserException {
        try {
            data.skipBytes(4); // Skip to the AVCDecoderConfigurationRecord (defined in 14496-15)
            int nalUnitLengthFieldLength = (data.readUnsignedByte() & 0x3) + 1;
            if (nalUnitLengthFieldLength == 3) {
                throw new IllegalStateException();
            }
            List<byte[]> initializationData = new ArrayList<>();
            int numSequenceParameterSets = data.readUnsignedByte() & 0x1F;
            for (int j = 0; j < numSequenceParameterSets; j++) {
                initializationData.add(buildNalUnitForChild(data));
            }
            int numPictureParameterSets = data.readUnsignedByte();
            for (int j = 0; j < numPictureParameterSets; j++) {
                initializationData.add(buildNalUnitForChild(data));
            }

            int width = Format.NO_VALUE;
            int height = Format.NO_VALUE;
            float pixelWidthAspectRatio = 1;
            if (numSequenceParameterSets > 0) {
                byte[] sps = initializationData.get(0);
                NalUnitUtil.SpsData spsData = NalUnitUtil.parseSpsNalUnit(initializationData.get(0),
                        nalUnitLengthFieldLength, sps.length);
                width = spsData.width;
                height = spsData.height;
                pixelWidthAspectRatio = spsData.pixelWidthAspectRatio;
            }
            return new AvcConfig(initializationData, nalUnitLengthFieldLength, width, height,
                    pixelWidthAspectRatio);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParserException("Error parsing AVC config", e);
        }
    }

    private AvcConfig(List<byte[]> initializationData, int nalUnitLengthFieldLength,
                      int width, int height, float pixelWidthAspectRatio) {
        this.initializationData = initializationData;
        this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
        this.width = width;
        this.height = height;
        this.pixelWidthAspectRatio = pixelWidthAspectRatio;
    }

    private static byte[] buildNalUnitForChild(ParsableByteArray data) {
        int length = data.readUnsignedShort();
        int offset = data.getPosition();
        data.skipBytes(length);
        return CodecSpecificDataUtil.buildNalUnit(data.data, offset, length);
    }

}