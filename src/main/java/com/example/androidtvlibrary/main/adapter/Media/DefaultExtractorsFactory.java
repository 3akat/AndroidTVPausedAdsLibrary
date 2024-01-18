package com.example.androidtvlibrary.main.adapter.Media;

import androidx.media3.common.util.TimestampAdjuster;

import com.example.androidtvlibrary.main.adapter.Media.extractor.Ac3Extractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.Ac4Extractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.AdtsExtractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.AmrExtractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.DefaultTsPayloadReaderFactory;
import com.example.androidtvlibrary.main.adapter.Media.extractor.FlacExtractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.FlvExtractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.FragmentedMp4Extractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.MatroskaExtractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.Mp4Extractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.OggExtractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.PsExtractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.TsExtractor;
import com.example.androidtvlibrary.main.adapter.Media.extractor.TsPayloadReader;
import com.example.androidtvlibrary.main.adapter.Media.extractor.WavExtractor;
import com.example.androidtvlibrary.main.adapter.mp3.ExtractorsFactory;

import java.lang.reflect.Constructor;

public final class DefaultExtractorsFactory implements ExtractorsFactory {

    private static final Constructor<? extends Extractor> FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR;

    static {
        Constructor<? extends Extractor> flacExtensionExtractorConstructor = null;
        try {
            // LINT.IfChange
            @SuppressWarnings("nullness:argument.type.incompatible")
            boolean isFlacNativeLibraryAvailable =
                    Boolean.TRUE.equals(
                            Class.forName("com.google.android.exoplayer2.ext.flac.FlacLibrary")
                                    .getMethod("isAvailable")
                                    .invoke(/* obj= */ null));
            if (isFlacNativeLibraryAvailable) {
                flacExtensionExtractorConstructor =
                        Class.forName("com.google.android.exoplayer2.ext.flac.FlacExtractor")
                                .asSubclass(Extractor.class)
                                .getConstructor();
            }
            // LINT.ThenChange(../../../../../../../../proguard-rules.txt)
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the FLAC extension.
        } catch (Exception e) {
            // The FLAC extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating FLAC extension", e);
        }
        FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR = flacExtensionExtractorConstructor;
    }

    private boolean constantBitrateSeekingEnabled;
    private @AdtsExtractor.Flags int adtsFlags;
    private @AmrExtractor.Flags int amrFlags;
    private @MatroskaExtractor.Flags int matroskaFlags;
    private @Mp4Extractor.Flags int mp4Flags;
    private @FragmentedMp4Extractor.Flags int fragmentedMp4Flags;
    private @Mp3Extractor.Flags int mp3Flags;
    private @TsExtractor.Mode int tsMode;
    private @DefaultTsPayloadReaderFactory.Flags int tsFlags;

    public DefaultExtractorsFactory() {
        tsMode = TsExtractor.MODE_SINGLE_PMT;
    }

    /**
     * Convenience method to set whether approximate seeking using constant bitrate assumptions should
     * be enabled for all extractors that support it. If set to true, the flags required to enable
     * this functionality will be OR'd with those passed to the setters when creating extractor
     * instances. If set to false then the flags passed to the setters will be used without
     * modification.
     *
     * @param constantBitrateSeekingEnabled Whether approximate seeking using a constant bitrate
     *     assumption should be enabled for all extractors that support it.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setConstantBitrateSeekingEnabled(
            boolean constantBitrateSeekingEnabled) {
        this.constantBitrateSeekingEnabled = constantBitrateSeekingEnabled;
        return this;
    }

    /**
     * Sets flags for {@link AdtsExtractor} instances created by the factory.
     *
     * @see AdtsExtractor#AdtsExtractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setAdtsExtractorFlags(
            @AdtsExtractor.Flags int flags) {
        this.adtsFlags = flags;
        return this;
    }

    /**
     * Sets flags for {@link AmrExtractor} instances created by the factory.
     *
     * @see AmrExtractor#AmrExtractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setAmrExtractorFlags(@AmrExtractor.Flags int flags) {
        this.amrFlags = flags;
        return this;
    }

    /**
     * Sets flags for {@link MatroskaExtractor} instances created by the factory.
     *
     * @see MatroskaExtractor#MatroskaExtractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setMatroskaExtractorFlags(
            @MatroskaExtractor.Flags int flags) {
        this.matroskaFlags = flags;
        return this;
    }

    /**
     * Sets flags for {@link Mp4Extractor} instances created by the factory.
     *
     * @see Mp4Extractor#Mp4Extractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setMp4ExtractorFlags(@Mp4Extractor.Flags int flags) {
        this.mp4Flags = flags;
        return this;
    }

    /**
     * Sets flags for {@link FragmentedMp4Extractor} instances created by the factory.
     *
     * @see FragmentedMp4Extractor#FragmentedMp4Extractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setFragmentedMp4ExtractorFlags(
            @FragmentedMp4Extractor.Flags int flags) {
        this.fragmentedMp4Flags = flags;
        return this;
    }

    /**
     * Sets flags for {@link Mp3Extractor} instances created by the factory.
     *
     * @see Mp3Extractor#Mp3Extractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setMp3ExtractorFlags(@Mp3Extractor.Flags int flags) {
        mp3Flags = flags;
        return this;
    }

    /**
     * Sets the mode for {@link TsExtractor} instances created by the factory.
     *
     * @see TsExtractor(int, TimestampAdjuster, TsPayloadReader.Factory)
     * @param mode The mode to use.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setTsExtractorMode(@TsExtractor.Mode int mode) {
        tsMode = mode;
        return this;
    }

    /**
     * Sets flags for {@link DefaultTsPayloadReaderFactory}s used by {@link TsExtractor} instances
     * created by the factory.
     *
     * @see TsExtractor#TsExtractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized DefaultExtractorsFactory setTsExtractorFlags(
            @DefaultTsPayloadReaderFactory.Flags int flags) {
        tsFlags = flags;
        return this;
    }

    @Override
    public synchronized Extractor[] createExtractors() {
        Extractor[] extractors = new Extractor[14];
        extractors[0] = new MatroskaExtractor(matroskaFlags);
        extractors[1] = new FragmentedMp4Extractor(fragmentedMp4Flags);
        extractors[2] = new Mp4Extractor(mp4Flags);
        extractors[3] =
                new Mp3Extractor(
                        mp3Flags
                                | (constantBitrateSeekingEnabled
                                ? Mp3Extractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING
                                : 0));
        extractors[4] =
                new AdtsExtractor(
                        adtsFlags
                                | (constantBitrateSeekingEnabled
                                ? AdtsExtractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING
                                : 0));
        extractors[5] = new Ac3Extractor();
        extractors[6] = new TsExtractor(tsMode, tsFlags);
        extractors[7] = new FlvExtractor();
        extractors[8] = new OggExtractor();
        extractors[9] = new PsExtractor();
        extractors[10] = new WavExtractor();
        extractors[11] =
                new AmrExtractor(
                        amrFlags
                                | (constantBitrateSeekingEnabled
                                ? AmrExtractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING
                                : 0));
        extractors[12] = new Ac4Extractor();
        if (FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR != null) {
            try {
                extractors[13] = FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR.newInstance();
            } catch (Exception e) {
                // Should never happen.
                throw new IllegalStateException("Unexpected error creating FLAC extractor", e);
            }
        } else {
            extractors[13] = new FlacExtractor();
        }
        return extractors;
    }

}
