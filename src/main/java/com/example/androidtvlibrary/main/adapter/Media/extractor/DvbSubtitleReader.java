package com.example.androidtvlibrary.main.adapter.Media.extractor;

import static com.example.androidtvlibrary.main.adapter.Media.extractor.TsPayloadReader.FLAG_DATA_ALIGNMENT_INDICATOR;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorOutput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.TrackOutput;
import com.example.androidtvlibrary.main.adapter.MimeTypes;

import java.util.Collections;
import java.util.List;

public final class DvbSubtitleReader implements ElementaryStreamReader {

    private final List<TsPayloadReader.DvbSubtitleInfo> subtitleInfos;
    private final TrackOutput[] outputs;

    private boolean writingSample;
    private int bytesToCheck;
    private int sampleBytesWritten;
    private long sampleTimeUs;

    /**
     * @param subtitleInfos Information about the DVB subtitles associated to the stream.
     */
    public DvbSubtitleReader(List<TsPayloadReader.DvbSubtitleInfo> subtitleInfos) {
        this.subtitleInfos = subtitleInfos;
        outputs = new TrackOutput[subtitleInfos.size()];
    }

    @Override
    public void seek() {
        writingSample = false;
    }

    @Override
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
        for (int i = 0; i < outputs.length; i++) {
            TsPayloadReader.DvbSubtitleInfo subtitleInfo = subtitleInfos.get(i);
            idGenerator.generateNewId();
            TrackOutput output = extractorOutput.track(idGenerator.getTrackId(), C.TRACK_TYPE_TEXT);
            output.format(
                    Format.createImageSampleFormat(
                            idGenerator.getFormatId(),
                            MimeTypes.APPLICATION_DVBSUBS,
                            null,
                            Format.NO_VALUE,
                            0,
                            Collections.singletonList(subtitleInfo.initializationData),
                            subtitleInfo.language,
                            null));
            outputs[i] = output;
        }
    }

    @Override
    public void packetStarted(long pesTimeUs, @TsPayloadReader.Flags int flags) {
        if ((flags & FLAG_DATA_ALIGNMENT_INDICATOR) == 0) {
            return;
        }
        writingSample = true;
        sampleTimeUs = pesTimeUs;
        sampleBytesWritten = 0;
        bytesToCheck = 2;
    }

    @Override
    public void packetFinished() {
        if (writingSample) {
            for (TrackOutput output : outputs) {
                output.sampleMetadata(sampleTimeUs, C.BUFFER_FLAG_KEY_FRAME, sampleBytesWritten, 0, null);
            }
            writingSample = false;
        }
    }

    @Override
    public void consume(ParsableByteArray data) {
        if (writingSample) {
            if (bytesToCheck == 2 && !checkNextByte(data, 0x20)) {
                // Failed to check data_identifier
                return;
            }
            if (bytesToCheck == 1 && !checkNextByte(data, 0x00)) {
                // Check and discard the subtitle_stream_id
                return;
            }
            int dataPosition = data.getPosition();
            int bytesAvailable = data.bytesLeft();
            for (TrackOutput output : outputs) {
                data.setPosition(dataPosition);
                output.sampleData(data, bytesAvailable);
            }
            sampleBytesWritten += bytesAvailable;
        }
    }

    private boolean checkNextByte(ParsableByteArray data, int expectedValue) {
        if (data.bytesLeft() == 0) {
            return false;
        }
        if (data.readUnsignedByte() != expectedValue) {
            writingSample = false;
        }
        bytesToCheck--;
        return writingSample;
    }

}
