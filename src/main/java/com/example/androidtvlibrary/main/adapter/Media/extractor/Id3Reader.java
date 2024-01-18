package com.example.androidtvlibrary.main.adapter.Media.extractor;

import static com.example.androidtvlibrary.main.adapter.Media.extractor.TsPayloadReader.FLAG_DATA_ALIGNMENT_INDICATOR;
import static com.example.androidtvlibrary.main.adapter.mp3.Id3Decoder.ID3_HEADER_LENGTH;

import android.util.Log;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorOutput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.TrackOutput;
import com.example.androidtvlibrary.main.adapter.MimeTypes;

public final class Id3Reader implements ElementaryStreamReader {

    private static final String TAG = "Id3Reader";

    private final ParsableByteArray id3Header;

    private TrackOutput output;

    // State that should be reset on seek.
    private boolean writingSample;

    // Per sample state that gets reset at the start of each sample.
    private long sampleTimeUs;
    private int sampleSize;
    private int sampleBytesRead;

    public Id3Reader() {
        id3Header = new ParsableByteArray(ID3_HEADER_LENGTH);
    }

    @Override
    public void seek() {
        writingSample = false;
    }

    @Override
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        output = extractorOutput.track(idGenerator.getTrackId(), C.TRACK_TYPE_METADATA);
        output.format(Format.createSampleFormat(idGenerator.getFormatId(), MimeTypes.APPLICATION_ID3,
                null, Format.NO_VALUE, null));
    }

    @Override
    public void packetStarted(long pesTimeUs, @TsPayloadReader.Flags int flags) {
        if ((flags & FLAG_DATA_ALIGNMENT_INDICATOR) == 0) {
            return;
        }
        writingSample = true;
        sampleTimeUs = pesTimeUs;
        sampleSize = 0;
        sampleBytesRead = 0;
    }

    @Override
    public void consume(ParsableByteArray data) {
        if (!writingSample) {
            return;
        }
        int bytesAvailable = data.bytesLeft();
        if (sampleBytesRead < ID3_HEADER_LENGTH) {
            // We're still reading the ID3 header.
            int headerBytesAvailable = Math.min(bytesAvailable, ID3_HEADER_LENGTH - sampleBytesRead);
            System.arraycopy(data.data, data.getPosition(), id3Header.data, sampleBytesRead,
                    headerBytesAvailable);
            if (sampleBytesRead + headerBytesAvailable == ID3_HEADER_LENGTH) {
                // We've finished reading the ID3 header. Extract the sample size.
                id3Header.setPosition(0);
                if ('I' != id3Header.readUnsignedByte() || 'D' != id3Header.readUnsignedByte()
                        || '3' != id3Header.readUnsignedByte()) {
                    Log.w(TAG, "Discarding invalid ID3 tag");
                    writingSample = false;
                    return;
                }
                id3Header.skipBytes(3); // version (2) + flags (1)
                sampleSize = ID3_HEADER_LENGTH + id3Header.readSynchSafeInt();
            }
        }
        // Write data to the output.
        int bytesToWrite = Math.min(bytesAvailable, sampleSize - sampleBytesRead);
        output.sampleData(data, bytesToWrite);
        sampleBytesRead += bytesToWrite;
    }

    @Override
    public void packetFinished() {
        if (!writingSample || sampleSize == 0 || sampleBytesRead != sampleSize) {
            return;
        }
        output.sampleMetadata(sampleTimeUs, C.BUFFER_FLAG_KEY_FRAME, sampleSize, 0, null);
        writingSample = false;
    }

}
