package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorInput;
import com.example.androidtvlibrary.main.adapter.Media.ParsableByteArray;

import java.io.IOException;

public final class Sniffer {

    private static final int SEARCH_LENGTH = 1024;
    private static final int ID_EBML = 0x1A45DFA3;

    private final ParsableByteArray scratch;
    private int peekLength;

    public Sniffer() {
        scratch = new ParsableByteArray(8);
    }


    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        long inputLength = input.getLength();
        int bytesToSearch = (int) (inputLength == C.LENGTH_UNSET || inputLength > SEARCH_LENGTH
                ? SEARCH_LENGTH : inputLength);
        // Find four bytes equal to ID_EBML near the start of the input.
        input.peekFully(scratch.data, 0, 4);
        long tag = scratch.readUnsignedInt();
        peekLength = 4;
        while (tag != ID_EBML) {
            if (++peekLength == bytesToSearch) {
                return false;
            }
            input.peekFully(scratch.data, 0, 1);
            tag = (tag << 8) & 0xFFFFFF00;
            tag |= scratch.data[0] & 0xFF;
        }

        // Read the size of the EBML header and make sure it is within the stream.
        long headerSize = readUint(input);
        long headerStart = peekLength;
        if (headerSize == Long.MIN_VALUE
                || (inputLength != C.LENGTH_UNSET && headerStart + headerSize >= inputLength)) {
            return false;
        }

        // Read the payload elements in the EBML header.
        while (peekLength < headerStart + headerSize) {
            long id = readUint(input);
            if (id == Long.MIN_VALUE) {
                return false;
            }
            long size = readUint(input);
            if (size < 0 || size > Integer.MAX_VALUE) {
                return false;
            }
            if (size != 0) {
                int sizeInt = (int) size;
                input.advancePeekPosition(sizeInt);
                peekLength += sizeInt;
            }
        }
        return peekLength == headerStart + headerSize;
    }

    /**
     * Peeks a variable-length unsigned EBML integer from the input.
     */
    private long readUint(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(scratch.data, 0, 1);
        int value = scratch.data[0] & 0xFF;
        if (value == 0) {
            return Long.MIN_VALUE;
        }
        int mask = 0x80;
        int length = 0;
        while ((value & mask) == 0) {
            mask >>= 1;
            length++;
        }
        value &= ~mask;
        input.peekFully(scratch.data, 1, length);
        for (int i = 0; i < length; i++) {
            value <<= 8;
            value += scratch.data[i + 1] & 0xFF;
        }
        peekLength += length + 1;
        return value;
    }


    private static final int[] COMPATIBLE_BRANDS =
            new int[] {
                    0x69736f6d, // isom
                    0x69736f32, // iso2
                    0x69736f33, // iso3
                    0x69736f34, // iso4
                    0x69736f35, // iso5
                    0x69736f36, // iso6
                    0x61766331, // avc1
                    0x68766331, // hvc1
                    0x68657631, // hev1
                    0x61763031, // av01
                    0x6d703431, // mp41
                    0x6d703432, // mp42
                    0x33673261, // 3g2a
                    0x33673262, // 3g2b
                    0x33677236, // 3gr6
                    0x33677336, // 3gs6
                    0x33676536, // 3ge6
                    0x33676736, // 3gg6
                    0x4d345620, // M4V[space]
                    0x4d344120, // M4A[space]
                    0x66347620, // f4v[space]
                    0x6b646469, // kddi
                    0x4d345650, // M4VP
                    0x71742020, // qt[space][space], Apple QuickTime
                    0x4d534e56, // MSNV, Sony PSP
                    0x64627931, // dby1, Dolby Vision
                    0x69736d6c, // isml
                    0x70696666, // piff
            };

    /**
     * Returns whether data peeked from the current position in {@code input} is consistent with the
     * input being a fragmented MP4 file.
     *
     * @param input The extractor input from which to peek data. The peek position will be modified.
     * @return Whether the input appears to be in the fragmented MP4 format.
     * @throws IOException If an error occurs reading from the input.
     * @throws InterruptedException If the thread has been interrupted.
     */
    public static boolean sniffFragmented(ExtractorInput input)
            throws IOException, InterruptedException {
        return sniffInternal(input, true);
    }

    /**
     * Returns whether data peeked from the current position in {@code input} is consistent with the
     * input being an unfragmented MP4 file.
     *
     * @param input The extractor input from which to peek data. The peek position will be modified.
     * @return Whether the input appears to be in the unfragmented MP4 format.
     * @throws IOException If an error occurs reading from the input.
     * @throws InterruptedException If the thread has been interrupted.
     */
    public static boolean sniffUnfragmented(ExtractorInput input)
            throws IOException, InterruptedException {
        return sniffInternal(input, false);
    }

    private static boolean sniffInternal(ExtractorInput input, boolean fragmented)
            throws IOException, InterruptedException {
        long inputLength = input.getLength();
        int bytesToSearch = (int) (inputLength == C.LENGTH_UNSET || inputLength > SEARCH_LENGTH
                ? SEARCH_LENGTH : inputLength);

        ParsableByteArray buffer = new ParsableByteArray(64);
        int bytesSearched = 0;
        boolean foundGoodFileType = false;
        boolean isFragmented = false;
        while (bytesSearched < bytesToSearch) {
            // Read an atom header.
            int headerSize = Atom.HEADER_SIZE;
            buffer.reset(headerSize);
            boolean success =
                    input.peekFully(buffer.data, 0, headerSize, /* allowEndOfInput= */ true);
            if (!success) {
                // We've reached the end of the file.
                break;
            }
            long atomSize = buffer.readUnsignedInt();
            int atomType = buffer.readInt();
            if (atomSize == Atom.DEFINES_LARGE_SIZE) {
                // Read the large atom size.
                headerSize = Atom.LONG_HEADER_SIZE;
                input.peekFully(buffer.data, Atom.HEADER_SIZE, Atom.LONG_HEADER_SIZE - Atom.HEADER_SIZE);
                buffer.setLimit(Atom.LONG_HEADER_SIZE);
                atomSize = buffer.readLong();
            } else if (atomSize == Atom.EXTENDS_TO_END_SIZE) {
                // The atom extends to the end of the file.
                long fileEndPosition = input.getLength();
                if (fileEndPosition != C.LENGTH_UNSET) {
                    atomSize = fileEndPosition - input.getPeekPosition() + headerSize;
                }
            }

            if (atomSize < headerSize) {
                // The file is invalid because the atom size is too small for its header.
                return false;
            }
            bytesSearched += headerSize;

            if (atomType == Atom.TYPE_moov) {
                // We have seen the moov atom. We increase the search size to make sure we don't miss an
                // mvex atom because the moov's size exceeds the search length.
                bytesToSearch += (int) atomSize;
                if (inputLength != C.LENGTH_UNSET && bytesToSearch > inputLength) {
                    // Make sure we don't exceed the file size.
                    bytesToSearch = (int) inputLength;
                }
                // Check for an mvex atom inside the moov atom to identify whether the file is fragmented.
                continue;
            }

            if (atomType == Atom.TYPE_moof || atomType == Atom.TYPE_mvex) {
                // The movie is fragmented. Stop searching as we must have read any ftyp atom already.
                isFragmented = true;
                break;
            }

            if (bytesSearched + atomSize - headerSize >= bytesToSearch) {
                // Stop searching as peeking this atom would exceed the search limit.
                break;
            }

            int atomDataSize = (int) (atomSize - headerSize);
            bytesSearched += atomDataSize;
            if (atomType == Atom.TYPE_ftyp) {
                // Parse the atom and check the file type/brand is compatible with the extractors.
                if (atomDataSize < 8) {
                    return false;
                }
                buffer.reset(atomDataSize);
                input.peekFully(buffer.data, 0, atomDataSize);
                int brandsCount = atomDataSize / 4;
                for (int i = 0; i < brandsCount; i++) {
                    if (i == 1) {
                        // This index refers to the minorVersion, not a brand, so skip it.
                        buffer.skipBytes(4);
                    } else if (isCompatibleBrand(buffer.readInt())) {
                        foundGoodFileType = true;
                        break;
                    }
                }
                if (!foundGoodFileType) {
                    // The types were not compatible and there is only one ftyp atom, so reject the file.
                    return false;
                }
            } else if (atomDataSize != 0) {
                // Skip the atom.
                input.advancePeekPosition(atomDataSize);
            }
        }
        return foundGoodFileType && fragmented == isFragmented;
    }

    /**
     * Returns whether {@code brand} is an ftyp atom brand that is compatible with the MP4 extractors.
     */
    private static boolean isCompatibleBrand(int brand) {
        // Accept all brands starting '3gp'.
        if (brand >>> 8 == 0x00336770) {
            return true;
        }
        for (int compatibleBrand : COMPATIBLE_BRANDS) {
            if (compatibleBrand == brand) {
                return true;
            }
        }
        return false;
    }


}
