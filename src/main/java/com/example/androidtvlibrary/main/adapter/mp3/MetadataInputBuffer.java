package com.example.androidtvlibrary.main.adapter.mp3;

import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.wow.DecoderInputBuffer;

public final class MetadataInputBuffer extends DecoderInputBuffer {

    /**
     * An offset that must be added to the metadata's timestamps after it's been decoded, or
     * {@link Format#OFFSET_SAMPLE_RELATIVE} if {@link #timeUs} should be added.
     */
    public long subsampleOffsetUs;

    public MetadataInputBuffer() {
        super(DecoderInputBuffer.BUFFER_REPLACEMENT_MODE_NORMAL);
    }

}
