package com.example.androidtvlibrary.main.adapter.wow;

import com.example.androidtvlibrary.main.adapter.C;

import java.io.IOException;

public final class EmptySampleStream implements SampleStream {

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void maybeThrowError() throws IOException {
        // Do nothing.
    }

    @Override
    public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer,
                        boolean formatRequired) {
        buffer.setFlags(C.BUFFER_FLAG_END_OF_STREAM);
        return C.RESULT_BUFFER_READ;
    }

    @Override
    public int skipData(long positionUs) {
        return 0;
    }

}
