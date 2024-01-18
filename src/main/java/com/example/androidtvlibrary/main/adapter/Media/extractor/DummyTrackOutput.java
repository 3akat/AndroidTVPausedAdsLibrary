package com.example.androidtvlibrary.main.adapter.Media.extractor;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorInput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.TrackOutput;

import java.io.EOFException;
import java.io.IOException;

public final class DummyTrackOutput implements TrackOutput {

    @Override
    public void format(Format format) {
        // Do nothing.
    }

    @Override
    public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput)
            throws IOException, InterruptedException {
        int bytesSkipped = input.skip(length);
        if (bytesSkipped == C.RESULT_END_OF_INPUT) {
            if (allowEndOfInput) {
                return C.RESULT_END_OF_INPUT;
            }
            throw new EOFException();
        }
        return bytesSkipped;
    }

    @Override
    public void sampleData(ParsableByteArray data, int length) {
        data.skipBytes(length);
    }

    @Override
    public void sampleMetadata(
            long timeUs,
            @C.BufferFlags int flags,
            int size,
            int offset,
            @Nullable CryptoData cryptoData) {
        // Do nothing.
    }
}

