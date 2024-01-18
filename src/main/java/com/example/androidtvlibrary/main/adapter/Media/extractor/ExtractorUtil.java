package com.example.androidtvlibrary.main.adapter.Media.extractor;

import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Media.ExtractorInput;

import java.io.IOException;

final class ExtractorUtil {

    /**
     * Peeks {@code length} bytes from the input peek position, or all the bytes to the end of the
     * input if there was less than {@code length} bytes left.
     *
     * <p>If an exception is thrown, there is no guarantee on the peek position.
     *
     * @param input The stream input to peek the data from.
     * @param target A target array into which data should be written.
     * @param offset The offset into the target array at which to write.
     * @param length The maximum number of bytes to peek from the input.
     * @return The number of bytes peeked.
     * @throws IOException If an error occurs peeking from the input.
     * @throws InterruptedException If the thread has been interrupted.
     */
    public static int peekToLength(ExtractorInput input, byte[] target, int offset, int length)
            throws IOException, InterruptedException {
        int totalBytesPeeked = 0;
        while (totalBytesPeeked < length) {
            int bytesPeeked = input.peek(target, offset + totalBytesPeeked, length - totalBytesPeeked);
            if (bytesPeeked == C.RESULT_END_OF_INPUT) {
                break;
            }
            totalBytesPeeked += bytesPeeked;
        }
        return totalBytesPeeked;
    }

    private ExtractorUtil() {}
}
