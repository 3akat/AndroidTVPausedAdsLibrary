package com.example.androidtvlibrary.main.adapter.player;

import com.example.androidtvlibrary.main.adapter.Util;
import com.example.androidtvlibrary.main.adapter.wow.DrmSession;
import com.example.androidtvlibrary.main.adapter.wow.ExoMediaCrypto;

import java.util.UUID;

public final class FrameworkMediaCrypto implements ExoMediaCrypto {

    /**
     * Whether the device needs keys to have been loaded into the {@link DrmSession} before codec
     * configuration.
     */
    public static final boolean WORKAROUND_DEVICE_NEEDS_KEYS_TO_CONFIGURE_CODEC =
            "Amazon".equals(Util.MANUFACTURER)
                    && ("AFTM".equals(Util.MODEL) // Fire TV Stick Gen 1
                    || "AFTB".equals(Util.MODEL)); // Fire TV Gen 1

    /** The DRM scheme UUID. */
    public final UUID uuid;
    /** The DRM session id. */
    public final byte[] sessionId;
    /**
     * Whether to allow use of insecure decoder components even if the underlying platform says
     * otherwise.
     */
    public final boolean forceAllowInsecureDecoderComponents;

    /**
     * @param uuid The DRM scheme UUID.
     * @param sessionId The DRM session id.
     * @param forceAllowInsecureDecoderComponents Whether to allow use of insecure decoder components
     *     even if the underlying platform says otherwise.
     */
    public FrameworkMediaCrypto(
            UUID uuid, byte[] sessionId, boolean forceAllowInsecureDecoderComponents) {
        this.uuid = uuid;
        this.sessionId = sessionId;
        this.forceAllowInsecureDecoderComponents = forceAllowInsecureDecoderComponents;
    }
}
