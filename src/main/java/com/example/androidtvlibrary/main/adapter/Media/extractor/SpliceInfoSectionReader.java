package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorOutput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.TrackOutput;
import com.example.androidtvlibrary.main.adapter.MimeTypes;

public final class SpliceInfoSectionReader implements SectionPayloadReader {

    private TimestampAdjuster timestampAdjuster;
    private TrackOutput output;
    private boolean formatDeclared;

    @Override
    public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput,
                     TsPayloadReader.TrackIdGenerator idGenerator) {
        this.timestampAdjuster = timestampAdjuster;
        idGenerator.generateNewId();
        output = extractorOutput.track(idGenerator.getTrackId(), C.TRACK_TYPE_METADATA);
        output.format(Format.createSampleFormat(idGenerator.getFormatId(), MimeTypes.APPLICATION_SCTE35,
                null, Format.NO_VALUE, null));
    }

    @Override
    public void consume(ParsableByteArray sectionData) {
        if (!formatDeclared) {
            if (timestampAdjuster.getTimestampOffsetUs() == C.TIME_UNSET) {
                // There is not enough information to initialize the timestamp adjuster.
                return;
            }
            output.format(Format.createSampleFormat(null, MimeTypes.APPLICATION_SCTE35,
                    timestampAdjuster.getTimestampOffsetUs()));
            formatDeclared = true;
        }
        int sampleSize = sectionData.bytesLeft();
        output.sampleData(sectionData, sampleSize);
        output.sampleMetadata(timestampAdjuster.getLastAdjustedTimestampUs(), C.BUFFER_FLAG_KEY_FRAME,
                sampleSize, 0, null);
    }

}
