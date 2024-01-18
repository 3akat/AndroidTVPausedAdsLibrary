package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Media.Extractor;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorInput;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorOutput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.ParserException;
import com.example.androidtvlibrary.main.adapter.Media.PositionHolder;
import com.example.androidtvlibrary.main.adapter.Media.TrackOutput;
import com.example.androidtvlibrary.main.adapter.mp3.ExtractorsFactory;

import java.io.IOException;

public class OggExtractor implements Extractor {

    /** Factory for {@link OggExtractor} instances. */
    public static final ExtractorsFactory FACTORY = () -> new Extractor[] {new OggExtractor()};

    private static final int MAX_VERIFICATION_BYTES = 8;

    private ExtractorOutput output;
    private StreamReader streamReader;
    private boolean streamReaderInitialized;

    @Override
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        try {
            return sniffInternal(input);
        } catch (ParserException e) {
            return false;
        }
    }

    @Override
    public void init(ExtractorOutput output) {
        this.output = output;
    }

    @Override
    public void seek(long position, long timeUs) {
        if (streamReader != null) {
            streamReader.seek(position, timeUs);
        }
    }

    @Override
    public void release() {
        // Do nothing
    }

    @Override
    public int read(ExtractorInput input, PositionHolder seekPosition)
            throws IOException, InterruptedException {
        if (streamReader == null) {
            if (!sniffInternal(input)) {
                throw new ParserException("Failed to determine bitstream type");
            }
            input.resetPeekPosition();
        }
        if (!streamReaderInitialized) {
            TrackOutput trackOutput = output.track(0, C.TRACK_TYPE_AUDIO);
            output.endTracks();
            streamReader.init(output, trackOutput);
            streamReaderInitialized = true;
        }
        return streamReader.read(input, seekPosition);
    }

    private boolean sniffInternal(ExtractorInput input) throws IOException, InterruptedException {
        OggPageHeader header = new OggPageHeader();
        if (!header.populate(input, true) || (header.type & 0x02) != 0x02) {
            return false;
        }

        int length = Math.min(header.bodySize, MAX_VERIFICATION_BYTES);
        ParsableByteArray scratch = new ParsableByteArray(length);
        input.peekFully(scratch.data, 0, length);

        if (FlacReader.verifyBitstreamType(resetPosition(scratch))) {
            streamReader = new FlacReader();
        } else if (VorbisReader.verifyBitstreamType(resetPosition(scratch))) {
            streamReader = new VorbisReader();
        } else if (OpusReader.verifyBitstreamType(resetPosition(scratch))) {
            streamReader = new OpusReader();
        } else {
            return false;
        }
        return true;
    }

    private static ParsableByteArray resetPosition(ParsableByteArray scratch) {
        scratch.setPosition(0);
        return scratch;
    }

}
