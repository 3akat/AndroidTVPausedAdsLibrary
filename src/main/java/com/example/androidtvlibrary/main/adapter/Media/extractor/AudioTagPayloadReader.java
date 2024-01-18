package com.example.androidtvlibrary.main.adapter.Media.extractor;

import android.util.Pair;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;
import com.example.androidtvlibrary.main.adapter.Media.ParserException;
import com.example.androidtvlibrary.main.adapter.Media.TrackOutput;
import com.example.androidtvlibrary.main.adapter.MimeTypes;

import java.util.Collections;

public final class AudioTagPayloadReader extends TagPayloadReader {

    private static final int AUDIO_FORMAT_MP3 = 2;
    private static final int AUDIO_FORMAT_ALAW = 7;
    private static final int AUDIO_FORMAT_ULAW = 8;
    private static final int AUDIO_FORMAT_AAC = 10;

    private static final int AAC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int AAC_PACKET_TYPE_AAC_RAW = 1;

    private static final int[] AUDIO_SAMPLING_RATE_TABLE = new int[] {5512, 11025, 22050, 44100};

    // State variables
    private boolean hasParsedAudioDataHeader;
    private boolean hasOutputFormat;
    private int audioFormat;

    public AudioTagPayloadReader(TrackOutput output) {
        super(output);
    }

    @Override
    public void seek() {
        // Do nothing.
    }

    @Override
    protected boolean parseHeader(ParsableByteArray data) throws UnsupportedFormatException {
        if (!hasParsedAudioDataHeader) {
            int header = data.readUnsignedByte();
            audioFormat = (header >> 4) & 0x0F;
            if (audioFormat == AUDIO_FORMAT_MP3) {
                int sampleRateIndex = (header >> 2) & 0x03;
                int sampleRate = AUDIO_SAMPLING_RATE_TABLE[sampleRateIndex];
                Format format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_MPEG, null,
                        Format.NO_VALUE, Format.NO_VALUE, 1, sampleRate, null, null, 0, null);
                output.format(format);
                hasOutputFormat = true;
            } else if (audioFormat == AUDIO_FORMAT_ALAW || audioFormat == AUDIO_FORMAT_ULAW) {
                String type = audioFormat == AUDIO_FORMAT_ALAW ? MimeTypes.AUDIO_ALAW
                        : MimeTypes.AUDIO_MLAW;
                Format format =
                        Format.createAudioSampleFormat(
                                /* id= */ null,
                                /* sampleMimeType= */ type,
                                /* codecs= */ null,
                                /* bitrate= */ Format.NO_VALUE,
                                /* maxInputSize= */ Format.NO_VALUE,
                                /* channelCount= */ 1,
                                /* sampleRate= */ 8000,
                                /* pcmEncoding= */ Format.NO_VALUE,
                                /* initializationData= */ null,
                                /* drmInitData= */ null,
                                /* selectionFlags= */ 0,
                                /* language= */ null);
                output.format(format);
                hasOutputFormat = true;
            } else if (audioFormat != AUDIO_FORMAT_AAC) {
                throw new UnsupportedFormatException("Audio format not supported: " + audioFormat);
            }
            hasParsedAudioDataHeader = true;
        } else {
            // Skip header if it was parsed previously.
            data.skipBytes(1);
        }
        return true;
    }

    @Override
    protected boolean parsePayload(ParsableByteArray data, long timeUs) throws ParserException {
        if (audioFormat == AUDIO_FORMAT_MP3) {
            int sampleSize = data.bytesLeft();
            output.sampleData(data, sampleSize);
            output.sampleMetadata(timeUs, C.BUFFER_FLAG_KEY_FRAME, sampleSize, 0, null);
            return true;
        } else {
            int packetType = data.readUnsignedByte();
            if (packetType == AAC_PACKET_TYPE_SEQUENCE_HEADER && !hasOutputFormat) {
                // Parse the sequence header.
                byte[] audioSpecificConfig = new byte[data.bytesLeft()];
                data.readBytes(audioSpecificConfig, 0, audioSpecificConfig.length);
                Pair<Integer, Integer> audioParams = CodecSpecificDataUtil.parseAacAudioSpecificConfig(
                        audioSpecificConfig);
                Format format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_AAC, null,
                        Format.NO_VALUE, Format.NO_VALUE, audioParams.second, audioParams.first,
                        Collections.singletonList(audioSpecificConfig), null, 0, null);
                output.format(format);
                hasOutputFormat = true;
                return false;
            } else if (audioFormat != AUDIO_FORMAT_AAC || packetType == AAC_PACKET_TYPE_AAC_RAW) {
                int sampleSize = data.bytesLeft();
                output.sampleData(data, sampleSize);
                output.sampleMetadata(timeUs, C.BUFFER_FLAG_KEY_FRAME, sampleSize, 0, null);
                return true;
            } else {
                return false;
            }
        }
    }
}
