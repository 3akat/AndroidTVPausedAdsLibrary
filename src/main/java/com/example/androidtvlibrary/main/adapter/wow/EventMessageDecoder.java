package com.example.androidtvlibrary.main.adapter.wow;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.extractor.EventMessage;
import com.example.androidtvlibrary.main.adapter.Metadata;
import com.example.androidtvlibrary.main.adapter.mp3.MetadataDecoder;
import com.example.androidtvlibrary.main.adapter.mp3.MetadataInputBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class EventMessageDecoder implements MetadataDecoder {

    @SuppressWarnings("ByteBufferBackingArray")
    @Override
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = Assertions.checkNotNull(inputBuffer.data);
        byte[] data = buffer.array();
        int size = buffer.limit();
        return new Metadata(decode(new ParsableByteArray(data, size)));
    }

    public EventMessage decode(ParsableByteArray emsgData) {
        String schemeIdUri = Assertions.checkNotNull(emsgData.readNullTerminatedString());
        String value = Assertions.checkNotNull(emsgData.readNullTerminatedString());
        long durationMs = emsgData.readUnsignedInt();
        long id = emsgData.readUnsignedInt();
        byte[] messageData =
                Arrays.copyOfRange(emsgData.data, emsgData.getPosition(), emsgData.limit());
        return new EventMessage(schemeIdUri, value, durationMs, id, messageData);
    }
}
