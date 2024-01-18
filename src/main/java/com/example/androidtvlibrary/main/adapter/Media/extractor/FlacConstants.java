package com.example.androidtvlibrary.main.adapter.Media.extractor;

public final class FlacConstants {

    /** Size of the FLAC stream marker in bytes. */
    public static final int STREAM_MARKER_SIZE = 4;
    /** Size of the header of a FLAC metadata block in bytes. */
    public static final int METADATA_BLOCK_HEADER_SIZE = 4;
    /** Size of the FLAC stream info block (header included) in bytes. */
    public static final int STREAM_INFO_BLOCK_SIZE = 38;
    /** Minimum size of a FLAC frame header in bytes. */
    public static final int MIN_FRAME_HEADER_SIZE = 6;
    /** Maximum size of a FLAC frame header in bytes. */
    public static final int MAX_FRAME_HEADER_SIZE = 16;

    /** Stream info metadata block type. */
    public static final int METADATA_TYPE_STREAM_INFO = 0;
    /** Seek table metadata block type. */
    public static final int METADATA_TYPE_SEEK_TABLE = 3;
    /** Vorbis comment metadata block type. */
    public static final int METADATA_TYPE_VORBIS_COMMENT = 4;
    /** Picture metadata block type. */
    public static final int METADATA_TYPE_PICTURE = 6;

    private FlacConstants() {}
}
