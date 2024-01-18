package com.example.androidtvlibrary.main.adapter.mp3;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Media.ExtractorInput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Metadata;

import java.io.EOFException;
import java.io.IOException;

public final class Id3Peeker {

    private final ParsableByteArray scratch;

    public Id3Peeker() {
        scratch = new ParsableByteArray(Id3Decoder.ID3_HEADER_LENGTH);
    }

    /**
     * Peeks ID3 data from the input and parses the first ID3 tag.
     *
     * @param input The {@link ExtractorInput} from which data should be peeked.
     * @param id3FramePredicate Determines which ID3 frames are decoded. May be null to decode all
     *     frames.
     * @return The first ID3 tag decoded into a {@link Metadata} object. May be null if ID3 tag is not
     *     present in the input.
     * @throws IOException If an error occurred peeking from the input.
     * @throws InterruptedException If the thread was interrupted.
     */
    @Nullable
    public Metadata peekId3Data(
            ExtractorInput input, @Nullable Id3Decoder.FramePredicate id3FramePredicate)
            throws IOException, InterruptedException {
        int peekedId3Bytes = 0;
        Metadata metadata = null;
        while (true) {
            try {
                input.peekFully(scratch.data, /* offset= */ 0, Id3Decoder.ID3_HEADER_LENGTH);
            } catch (EOFException e) {
                // If input has less than ID3_HEADER_LENGTH, ignore the rest.
                break;
            }
            scratch.setPosition(0);
            if (scratch.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
                // Not an ID3 tag.
                break;
            }
            scratch.skipBytes(3); // Skip major version, minor version and flags.
            int framesLength = scratch.readSynchSafeInt();
            int tagLength = Id3Decoder.ID3_HEADER_LENGTH + framesLength;

            if (metadata == null) {
                byte[] id3Data = new byte[tagLength];
                System.arraycopy(scratch.data, 0, id3Data, 0, Id3Decoder.ID3_HEADER_LENGTH);
                input.peekFully(id3Data, Id3Decoder.ID3_HEADER_LENGTH, framesLength);

                metadata = new Id3Decoder(id3FramePredicate).decode(id3Data, tagLength);
            } else {
                input.advancePeekPosition(framesLength);
            }

            peekedId3Bytes += tagLength;
        }

        input.resetPeekPosition();
        input.advancePeekPosition(peekedId3Bytes);
        return metadata;
    }
}
