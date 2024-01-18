package com.example.androidtvlibrary.main.adapter.player;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.MimeTypes;
import com.example.androidtvlibrary.main.adapter.mp3.Id3Decoder;
import com.example.androidtvlibrary.main.adapter.mp3.MetadataDecoder;
import com.example.androidtvlibrary.main.adapter.wow.EventMessageDecoder;
import com.example.androidtvlibrary.main.adapter.wow.IcyDecoder;
import com.example.androidtvlibrary.main.adapter.wow.SpliceInfoDecoder;

public interface MetadataDecoderFactory {

    /**
     * Returns whether the factory is able to instantiate a {@link MetadataDecoder} for the given
     * {@link Format}.
     *
     * @param format The {@link Format}.
     * @return Whether the factory can instantiate a suitable {@link MetadataDecoder}.
     */
    boolean supportsFormat(Format format);

    /**
     * Creates a {@link MetadataDecoder} for the given {@link Format}.
     *
     * @param format The {@link Format}.
     * @return A new {@link MetadataDecoder}.
     * @throws IllegalArgumentException If the {@link Format} is not supported.
     */
    MetadataDecoder createDecoder(Format format);

    /**
     * Default {@link MetadataDecoder} implementation.
     *
     * <p>The formats supported by this factory are:
     *
     * <ul>
     *   <li>ID3 ({@link Id3Decoder})
     *   <li>EMSG ({@link EventMessageDecoder})
     *   <li>SCTE-35 ({@link SpliceInfoDecoder})
     *   <li>ICY ({@link IcyDecoder})
     * </ul>
     */
    MetadataDecoderFactory DEFAULT =
            new MetadataDecoderFactory() {

                @Override
                public boolean supportsFormat(Format format) {
                    @Nullable String mimeType = format.sampleMimeType;
                    return MimeTypes.APPLICATION_ID3.equals(mimeType)
                            || MimeTypes.APPLICATION_EMSG.equals(mimeType)
                            || MimeTypes.APPLICATION_SCTE35.equals(mimeType)
                            || MimeTypes.APPLICATION_ICY.equals(mimeType);
                }

                @Override
                public MetadataDecoder createDecoder(Format format) {
                    @Nullable String mimeType = format.sampleMimeType;
                    if (mimeType != null) {
                        switch (mimeType) {
                            case MimeTypes.APPLICATION_ID3:
                                return new Id3Decoder();
                            case MimeTypes.APPLICATION_EMSG:
                                return new EventMessageDecoder();
                            case MimeTypes.APPLICATION_SCTE35:
                                return new SpliceInfoDecoder();
                            case MimeTypes.APPLICATION_ICY:
                                return new IcyDecoder();
                            default:
                                break;
                        }
                    }
                    throw new IllegalArgumentException(
                            "Attempted to create decoder for unsupported MIME type: " + mimeType);
                }
            };
}
