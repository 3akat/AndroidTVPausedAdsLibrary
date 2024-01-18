package com.example.androidtvlibrary.main.adapter;/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.ads.interactivemedia.v3.api.AdsLoader;

import java.io.IOException;


public interface AdsLoaderTest {

    /**
     * Listener for ads loader events. All methods are called on the main thread.
     */
    interface EventListener {

        /**
         * Called when the ad playback state has been updated.
         *
         * @param adPlaybackState The new ad playback state.
         */
        default void onAdPlaybackState(AdPlaybackStateTest adPlaybackState) {
        }

        /**
         * Called when there was an error loading ads.
         *
         * @param error    The error.
         * @param dataSpec The data spec associated with the load error.
         */
        default void onAdLoadError(TestException error, DataSpecTest dataSpec) {
        }

        /**
         * Called when the user clicks through an ad (for example, following a 'learn more' link).
         */
        default void onAdClicked() {
        }

        /**
         * Called when the user taps a non-clickthrough part of an ad.
         */
        default void onAdTapped() {
        }
    }

    /**
     * Provides views for the ad UI.
     */
    interface AdViewProvider {

        /**
         * Returns the {@link ViewGroup} on top of the player that will show any ad UI.
         */
        ViewGroup getAdViewGroup();

        /**
         * Returns an array of views that are shown on top of the ad view group, but that are essential
         * for controlling playback and should be excluded from ad viewability measurements by the
         * {@link AdsLoader} (if it supports this).
         *
         * <p>Each view must be either a fully transparent overlay (for capturing touch events), or a
         * small piece of transient UI that is essential to the user experience of playback (such as a
         * button to pause/resume playback or a transient full-screen or cast button). For more
         * information see the documentation for your ads loader.
         */
        View[] getAdOverlayViews();
    }

    // Methods called by the application.

    /**
     * Sets the player that will play the loaded ads.
     *
     * <p>This method must be called before the player is prepared with media using this ads loader.
     *
     * <p>This method must also be called on the main thread and only players which are accessed on
     * the main thread are supported ({@code player.getApplicationLooper() ==
     * Looper.getMainLooper()}).
     *
     * @param player The player instance that will play the loaded ads. May be null to delete the
     *               reference to a previously set player.
     */
    void setPlayer(@Nullable TestPlayer player);

    /**
     * Releases the loader. Must be called by the application on the main thread when the instance is
     * no longer needed.
     */
    void release();

    // Methods called by AdsMediaSource.


    void setSupportedContentTypes(@C.ContentType int... contentTypes);


    void start(EventListener eventListener, AdViewProvider adViewProvider);

    void stop();


    void handlePrepareError(int adGroupIndex, int adIndexInAdGroup, IOException exception);
}
