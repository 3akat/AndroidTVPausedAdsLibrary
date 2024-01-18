package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorOutput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.TrackOutput;
import com.example.androidtvlibrary.main.adapter.MimeTypes;

import java.util.List;

public final class UserDataReader {

    private static final int USER_DATA_START_CODE = 0x0001B2;

    private final List<Format> closedCaptionFormats;
    private final TrackOutput[] outputs;

    public UserDataReader(List<Format> closedCaptionFormats) {
        this.closedCaptionFormats = closedCaptionFormats;
        outputs = new TrackOutput[closedCaptionFormats.size()];
    }

    public void createTracks(
            ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
        for (int i = 0; i < outputs.length; i++) {
            idGenerator.generateNewId();
            TrackOutput output = extractorOutput.track(idGenerator.getTrackId(), C.TRACK_TYPE_TEXT);
            Format channelFormat = closedCaptionFormats.get(i);
            String channelMimeType = channelFormat.sampleMimeType;
            Assertions.checkArgument(
                    MimeTypes.APPLICATION_CEA608.equals(channelMimeType)
                            || MimeTypes.APPLICATION_CEA708.equals(channelMimeType),
                    "Invalid closed caption mime type provided: " + channelMimeType);
            output.format(
                    Format.createTextSampleFormat(
                            idGenerator.getFormatId(),
                            channelMimeType,
                            /* codecs= */ null,
                            /* bitrate= */ Format.NO_VALUE,
                            channelFormat.selectionFlags,
                            channelFormat.language,
                            channelFormat.accessibilityChannel,
                            /* drmInitData= */ null,
                            Format.OFFSET_SAMPLE_RELATIVE,
                            channelFormat.initializationData));
            outputs[i] = output;
        }
    }

    public void consume(long pesTimeUs, ParsableByteArray userDataPayload) {
        if (userDataPayload.bytesLeft() < 9) {
            return;
        }
        int userDataStartCode = userDataPayload.readInt();
        int userDataIdentifier = userDataPayload.readInt();
        int userDataTypeCode = userDataPayload.readUnsignedByte();
//        if (userDataStartCode == USER_DATA_START_CODE
//                && userDataIdentifier == CeaUtil.USER_DATA_IDENTIFIER_GA94
//                && userDataTypeCode == CeaUtil.USER_DATA_TYPE_CODE_MPEG_CC) {
//            CeaUtil.consumeCcData(pesTimeUs, userDataPayload, outputs);
//        }
    }
}
