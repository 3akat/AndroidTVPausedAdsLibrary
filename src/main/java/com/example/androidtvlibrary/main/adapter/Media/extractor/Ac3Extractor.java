package com.example.androidtvlibrary.main.adapter.Media.extractor;

import static com.example.androidtvlibrary.main.adapter.Media.extractor.TsPayloadReader.FLAG_DATA_ALIGNMENT_INDICATOR;
import static com.example.androidtvlibrary.main.adapter.mp3.Id3Decoder.ID3_HEADER_LENGTH;
import static com.example.androidtvlibrary.main.adapter.mp3.Id3Decoder.ID3_TAG;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Media.Extractor;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorInput;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorOutput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.PositionHolder;
import com.example.androidtvlibrary.main.adapter.Media.SeekMap;
import com.example.androidtvlibrary.main.adapter.mp3.ExtractorsFactory;

import java.io.IOException;

public final class Ac3Extractor implements Extractor {

    /** Factory for {@link Ac3Extractor} instances. */
    public static final ExtractorsFactory FACTORY = () -> new Extractor[] {new Ac3Extractor()};

    /**
     * The maximum number of bytes to search when sniffing, excluding ID3 information, before giving
     * up.
     */
    private static final int MAX_SNIFF_BYTES = 8 * 1024;
    private static final int AC3_SYNC_WORD = 0x0B77;
    private static final int MAX_SYNC_FRAME_SIZE = 2786;

    private final Ac3Reader reader;
    private final ParsableByteArray sampleData;

    private boolean startedPacket;

    /** Creates a new extractor for AC-3 bitstreams. */
    public Ac3Extractor() {
        reader = new Ac3Reader();
        sampleData = new ParsableByteArray(MAX_SYNC_FRAME_SIZE);
    }

    // Extractor implementation.

    @Override
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        // Skip any ID3 headers.
        ParsableByteArray scratch = new ParsableByteArray(ID3_HEADER_LENGTH);
        int startPosition = 0;
        while (true) {
            input.peekFully(scratch.data, /* offset= */ 0, ID3_HEADER_LENGTH);
            scratch.setPosition(0);
            if (scratch.readUnsignedInt24() != ID3_TAG) {
                break;
            }
            scratch.skipBytes(3); // version, flags
            int length = scratch.readSynchSafeInt();
            startPosition += 10 + length;
            input.advancePeekPosition(length);
        }
        input.resetPeekPosition();
        input.advancePeekPosition(startPosition);

        int headerPosition = startPosition;
        int validFramesCount = 0;
        while (true) {
            input.peekFully(scratch.data, 0, 6);
            scratch.setPosition(0);
            int syncBytes = scratch.readUnsignedShort();
            if (syncBytes != AC3_SYNC_WORD) {
                validFramesCount = 0;
                input.resetPeekPosition();
                if (++headerPosition - startPosition >= MAX_SNIFF_BYTES) {
                    return false;
                }
                input.advancePeekPosition(headerPosition);
            } else {
                if (++validFramesCount >= 4) {
                    return true;
                }
                int frameSize = Ac3Util.parseAc3SyncframeSize(scratch.data);
                if (frameSize == C.LENGTH_UNSET) {
                    return false;
                }
                input.advancePeekPosition(frameSize - 6);
            }
        }
    }

    @Override
    public void init(ExtractorOutput output) {
        reader.createTracks(output, new TsPayloadReader.TrackIdGenerator(0, 1));
        output.endTracks();
        output.seekMap(new SeekMap.Unseekable(C.TIME_UNSET));
    }

    @Override
    public void seek(long position, long timeUs) {
        startedPacket = false;
        reader.seek();
    }

    @Override
    public void release() {
        // Do nothing.
    }

    @Override
    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException,
            InterruptedException {
        int bytesRead = input.read(sampleData.data, 0, MAX_SYNC_FRAME_SIZE);
        if (bytesRead == C.RESULT_END_OF_INPUT) {
            return RESULT_END_OF_INPUT;
        }

        // Feed whatever data we have to the reader, regardless of whether the read finished or not.
        sampleData.setPosition(0);
        sampleData.setLimit(bytesRead);

        if (!startedPacket) {
            // Pass data to the reader as though it's contained within a single infinitely long packet.
            reader.packetStarted(/* pesTimeUs= */ 0, FLAG_DATA_ALIGNMENT_INDICATOR);
            startedPacket = true;
        }
        // TODO: Make it possible for the reader to consume the dataSource directly, so that it becomes
        // unnecessary to copy the data through packetBuffer.
        reader.consume(sampleData);
        return RESULT_CONTINUE;
    }

}

