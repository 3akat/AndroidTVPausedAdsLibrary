package com.example.androidtvlibrary.main.adapter.player;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.media3.common.text.Cue;

import com.example.androidtvlibrary.main.adapter.Assertions;
import com.example.androidtvlibrary.main.adapter.C;
import com.example.androidtvlibrary.main.adapter.Format;
import com.example.androidtvlibrary.main.adapter.MimeTypes;
import com.example.androidtvlibrary.main.adapter.TestPlayer;
import com.example.androidtvlibrary.main.adapter.Util;
import com.example.androidtvlibrary.main.adapter.wow.FormatHolder;
import com.example.androidtvlibrary.main.adapter.wow.RendererCapabilities;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

public final class TextRenderer extends BaseRenderer implements Handler.Callback {

    private static final String TAG = "TextRenderer";

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            REPLACEMENT_STATE_NONE,
            REPLACEMENT_STATE_SIGNAL_END_OF_STREAM,
            REPLACEMENT_STATE_WAIT_END_OF_STREAM
    })
    private @interface ReplacementState {}
    /**
     * The decoder does not need to be replaced.
     */
    private static final int REPLACEMENT_STATE_NONE = 0;
    /**
     * The decoder needs to be replaced, but we haven't yet signaled an end of stream to the existing
     * decoder. We need to do so in order to ensure that it outputs any remaining buffers before we
     * release it.
     */
    private static final int REPLACEMENT_STATE_SIGNAL_END_OF_STREAM = 1;
    /**
     * The decoder needs to be replaced, and we've signaled an end of stream to the existing decoder.
     * We're waiting for the decoder to output an end of stream signal to indicate that it has output
     * any remaining buffers before we release it.
     */
    private static final int REPLACEMENT_STATE_WAIT_END_OF_STREAM = 2;

    private static final int MSG_UPDATE_OUTPUT = 0;

    @Nullable
    private final Handler outputHandler;
    private final TestPlayer.TextOutput output;
//    private final SubtitleDecoderFactory decoderFactory;
    private final FormatHolder formatHolder;

    private boolean inputStreamEnded;
    private boolean outputStreamEnded;
    @ReplacementState private int decoderReplacementState;
    @Nullable private Format streamFormat;
//    @Nullable private SubtitleDecoder decoder;
//    @Nullable private SubtitleInputBuffer nextInputBuffer;
//    @Nullable private SubtitleOutputBuffer subtitle;
//    @Nullable private SubtitleOutputBuffer nextSubtitle;
    private int nextSubtitleEventIndex;

    /**
     * @param output The output.
     * @param outputLooper The looper associated with the thread on which the output should be called.
     *     If the output makes use of standard Android UI components, then this should normally be the
     *     looper associated with the application's main thread, which can be obtained using {@link
     *     android.app.Activity#getMainLooper()}. Null may be passed if the output should be called
     *     directly on the player's internal rendering thread.
     */

    public TextRenderer(TestPlayer.TextOutput output, @Nullable Looper outputLooper) {
        super(C.TRACK_TYPE_TEXT);
        this.output = Assertions.checkNotNull(output);
        this.outputHandler =
                outputLooper == null ? null : Util.createHandler(outputLooper, /* callback= */ this);
        formatHolder = new FormatHolder();
    }

    @Override
    @Capabilities
    public int supportsFormat(Format format) {
        if (MimeTypes.isText(format.sampleMimeType)) {
            return RendererCapabilities.create(FORMAT_UNSUPPORTED_SUBTYPE);
        } else {
            return RendererCapabilities.create(FORMAT_UNSUPPORTED_TYPE);
        }
    }

    @Override
    protected void onStreamChanged(Format[] formats, long offsetUs) {
        streamFormat = formats[0];
            decoderReplacementState = REPLACEMENT_STATE_SIGNAL_END_OF_STREAM;
    }

    @Override
    protected void onPositionReset(long positionUs, boolean joining) {
        inputStreamEnded = false;
        outputStreamEnded = false;
        resetOutputAndDecoder();
    }

    @Override
    public void render(long positionUs, long elapsedRealtimeUs) {
        if (outputStreamEnded) {
            return;
        }

        if (getState() != STATE_STARTED) {
            return;
        }

        boolean textRendererNeedsUpdate = false;

        if (decoderReplacementState == REPLACEMENT_STATE_WAIT_END_OF_STREAM) {
            return;
        }

    }

    @Override
    protected void onDisabled() {
        streamFormat = null;
        clearOutput();
        releaseDecoder();
    }

    @Override
    public boolean isEnded() {
        return outputStreamEnded;
    }

    @Override
    public boolean isReady() {
        // Don't block playback whilst subtitles are loading.
        // Note: To change this behavior, it will be necessary to consider [Internal: b/12949941].
        return true;
    }

    private void releaseBuffers() {
        nextSubtitleEventIndex = C.INDEX_UNSET;

    }

    private void releaseDecoder() {
        releaseBuffers();
        decoderReplacementState = REPLACEMENT_STATE_NONE;
    }

    private void replaceDecoder() {
        releaseDecoder();
    }

    private long getNextEventTime() {
        return Long.MAX_VALUE ;
    }

    private void updateOutput(List<Cue> cues) {
        if (outputHandler != null) {
            outputHandler.obtainMessage(MSG_UPDATE_OUTPUT, cues).sendToTarget();
        } else {
            invokeUpdateOutputInternal(cues);
        }
    }

    private void clearOutput() {
        updateOutput(Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_OUTPUT:
                invokeUpdateOutputInternal((List<Cue>) msg.obj);
                return true;
            default:
                throw new IllegalStateException();
        }
    }

    private void invokeUpdateOutputInternal(List<Cue> cues) {
        output.onCues(cues);
    }


    private void handleDecoderError(Exception e) {
        Log.e(TAG, "Subtitle decoding failed. streamFormat=" + streamFormat, e);
        resetOutputAndDecoder();
    }

    private void resetOutputAndDecoder() {
        clearOutput();
        if (decoderReplacementState != REPLACEMENT_STATE_NONE) {
            replaceDecoder();
        } else {
            releaseBuffers();
        }
    }
}
