package com.example.androidtvlibrary.main.adapter.drmsession;

import java.util.UUID;

public interface MediaDrmCallback {

    /**
     * Executes a provisioning request.
     *
     * @param uuid The UUID of the content protection scheme.
     * @param request The request.
     * @return The response data.
     * @throws Exception If an error occurred executing the request.
     */
    byte[] executeProvisionRequest(UUID uuid, ExoMediaDrm.ProvisionRequest request) throws Exception;

    /**
     * Executes a key request.
     *
     * @param uuid The UUID of the content protection scheme.
     * @param request The request.
     * @return The response data.
     * @throws Exception If an error occurred executing the request.
     */
    byte[] executeKeyRequest(UUID uuid, ExoMediaDrm.KeyRequest request) throws Exception;
}
