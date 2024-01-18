package com.example.androidtvlibrary.main.adapter.wow;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.Media.ParsableBitArray;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.extractor.TimestampAdjuster;
import com.example.androidtvlibrary.main.adapter.Metadata;
import com.example.androidtvlibrary.main.adapter.mp3.MetadataDecoder;
import com.example.androidtvlibrary.main.adapter.mp3.MetadataInputBuffer;
import com.example.androidtvlibrary.main.adapter.player.comand.PrivateCommand;
import com.example.androidtvlibrary.main.adapter.player.comand.SpliceCommand;
import com.example.androidtvlibrary.main.adapter.player.comand.SpliceInsertCommand;
import com.example.androidtvlibrary.main.adapter.player.comand.SpliceNullCommand;
import com.example.androidtvlibrary.main.adapter.player.comand.SpliceScheduleCommand;
import com.example.androidtvlibrary.main.adapter.player.comand.TimeSignalCommand;

import java.nio.ByteBuffer;

public final class SpliceInfoDecoder implements MetadataDecoder {

    private static final int TYPE_SPLICE_NULL = 0x00;
    private static final int TYPE_SPLICE_SCHEDULE = 0x04;
    private static final int TYPE_SPLICE_INSERT = 0x05;
    private static final int TYPE_TIME_SIGNAL = 0x06;
    private static final int TYPE_PRIVATE_COMMAND = 0xFF;

    private final ParsableByteArray sectionData;
    private final ParsableBitArray sectionHeader;

     private TimestampAdjuster timestampAdjuster;

    public SpliceInfoDecoder() {
        sectionData = new ParsableByteArray();
        sectionHeader = new ParsableBitArray();
    }

    @SuppressWarnings("ByteBufferBackingArray")
    @Override
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = Assertions.checkNotNull(inputBuffer.data);

        // Internal timestamps adjustment.
        if (timestampAdjuster == null
                || inputBuffer.subsampleOffsetUs != timestampAdjuster.getTimestampOffsetUs()) {
            timestampAdjuster = new TimestampAdjuster(inputBuffer.timeUs);
            timestampAdjuster.adjustSampleTimestamp(inputBuffer.timeUs - inputBuffer.subsampleOffsetUs);
        }

        byte[] data = buffer.array();
        int size = buffer.limit();
        sectionData.reset(data, size);
        sectionHeader.reset(data, size);
        // table_id(8), section_syntax_indicator(1), private_indicator(1), reserved(2),
        // section_length(12), protocol_version(8), encrypted_packet(1), encryption_algorithm(6).
        sectionHeader.skipBits(39);
        long ptsAdjustment = sectionHeader.readBits(1);
        ptsAdjustment = (ptsAdjustment << 32) | sectionHeader.readBits(32);
        // cw_index(8), tier(12).
        sectionHeader.skipBits(20);
        int spliceCommandLength = sectionHeader.readBits(12);
        int spliceCommandType = sectionHeader.readBits(8);
        @Nullable SpliceCommand command = null;
        // Go to the start of the command by skipping all fields up to command_type.
        sectionData.skipBytes(14);
        switch (spliceCommandType) {
            case TYPE_SPLICE_NULL:
                command = new SpliceNullCommand();
                break;
            case TYPE_SPLICE_SCHEDULE:
                command = SpliceScheduleCommand.parseFromSection(sectionData);
                break;
            case TYPE_SPLICE_INSERT:
                command = SpliceInsertCommand.parseFromSection(sectionData, ptsAdjustment,
                        timestampAdjuster);
                break;
            case TYPE_TIME_SIGNAL:
                command = TimeSignalCommand.parseFromSection(sectionData, ptsAdjustment, timestampAdjuster);
                break;
            case TYPE_PRIVATE_COMMAND:
                command = PrivateCommand.parseFromSection(sectionData, spliceCommandLength, ptsAdjustment);
                break;
            default:
                // Do nothing.
                break;
        }
        return command == null ? new Metadata() : new Metadata(command);
    }

}
