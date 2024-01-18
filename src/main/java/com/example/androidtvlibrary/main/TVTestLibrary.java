package com.example.androidtvlibrary.main;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.androidtvlibrary.main.adapter.AdsLoaderTest;
import com.example.androidtvlibrary.main.adapter.DataSource;
import com.example.androidtvlibrary.main.adapter.ImaAdsLoaderTest;
import com.example.androidtvlibrary.main.adapter.Media.ProgressiveMediaSource;
import com.example.androidtvlibrary.main.adapter.PlayerView;
import com.example.androidtvlibrary.main.adapter.TrackSelection;
import com.example.androidtvlibrary.main.adapter.Util;
import com.example.androidtvlibrary.main.adapter.ads.AdsMediaSource;
import com.example.androidtvlibrary.main.adapter.factory.DefaultDataSourceFactory;
import com.example.androidtvlibrary.main.adapter.wow.AdaptiveTrackSelection;
import com.example.androidtvlibrary.main.adapter.wow.DefaultTrackSelector;
import com.example.androidtvlibrary.main.adapter.wow.MediaSource;
import com.example.androidtvlibrary.main.adapter.wow.SimpleWowPlayer;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRenderingSettings;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.Arrays;

public class TVTestLibrary {

    /**
     * IMA sample tag for a single skippable inline video ad. See more IMA sample tags at
     * https://developers.google.com/interactive-media-ads/docs/sdks/html5/client-side/tags
     */

    private static String SAMPLE_VAST_TAG_URL =
//            "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_preroll_skippable&sz=640x480&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=";
//    "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_vertical_ad_samples&sz=360x640&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=";
//            "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/vmap_ad_samples&sz=640x480&cust_params=sar%3Da0f2&ciu_szs=300x250&ad_rule=1&gdfp_req=1&output=vmap&unviewed_position_start=5&env=vp&impl=s&correlator=";
//            "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/vmap_ad_samples&sz=640x480&cust_params=sample_ar%3Dpremidpost&ciu_szs=300x250&gdfp_req=1&ad_rule=1&output=vmap&unviewed_position_start=1&env=vp&impl=s&cmsid=496&vid=short_onecue&correlator=";
            "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_preroll_skippable&sz=640x480&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=";

    private String SAMPLE_VIDEO_URL = "https://storage.googleapis.com/gvabox/media/samples/stock.mp4";

    private AdsManager adsManager;
    private AdsLoader basicAdsLoader;
    private PlayerView playerView;
    private SimpleWowPlayer player;
    private ResumeCallback resumeCallback;
    private AdsLoader.AdsLoadedListener adsLoadedListener;
    private AdEvent.AdEventListener adEventListener;

    private boolean adsPaused;

    public TVTestLibrary getInstance() {
        return new TVTestLibrary();
    }

    public void adAdsLoader(
            PlayerView playerView,
            Context context,
            PauseCallback pauseCallback,
            ResumeCallback resumeCallback,
            boolean skipped
    ) {
        this.playerView = playerView;
        this.resumeCallback = resumeCallback;

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(context, videoTrackSelectionFactory);
        player = new SimpleWowPlayer.Builder(context).setTrackSelector(trackSelector).build();
        AdsLoaderTest adsLoader = new ImaAdsLoaderTest(context, Uri.parse(SAMPLE_VAST_TAG_URL));
        playerView.setPlayer(player);
        adsLoader.setPlayer(player);
        DataSource.Factory sourceFactory = new DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "appname")
        );
        MediaSource contentSource;

        ProgressiveMediaSource.Factory contentSourceFactory = new ProgressiveMediaSource.Factory(sourceFactory);
        contentSource = contentSourceFactory.createMediaSource(Uri.parse(SAMPLE_VIDEO_URL));

        contentSource = new
                AdsMediaSource(contentSource, sourceFactory, adsLoader, playerView);

        player.prepare(contentSource);
        player.setPlayWhenReady(true);
        if (skipped) {
            playerView.setOnTouchCallback(() -> closeAdsManager());
        }

        // Add listeners for when ads are loaded and for errors.
        // The AdsLoader instance exposes the requestAds method.
        basicAdsLoader = ((ImaAdsLoaderTest) adsLoader).getAdsLoader();
        basicAdsLoader.addAdErrorListener(adErrorEvent -> {
            /** An event raised when there is an error loading or playing ads.  */
            Log.i("TVTestLibrary", "Ad Error: " + adErrorEvent.getError().getMessage());
            resumeCallback.onResumeCall();
        });
        adsLoadedListener = adsManagerLoadedEvent ->
        {
            // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
            // events for ad playback and errors.
            Log.i("TVTestLibrary", "Ads were successfully loaded");
            adsManager = adsManagerLoadedEvent.getAdsManager();

            // Attach event and error event listeners.
            adsManager.addAdErrorListener(
                    adErrorEvent ->
                    {
                        /** An event raised when there is an error loading or playing ads.  */
                        /** An event raised when there is an error loading or playing ads.  */
                        Log.i("TVTestLibrary", "Ad Error: " + adErrorEvent.getError().getMessage());
                        String universalAdIds =
                                Arrays.toString(adsManager.getCurrentAd().getUniversalAdIds());
                        Log.i(
                                "TVTestLibrary",
                                "Discarding the current ad break with universal "
                                        + "ad Ids: "
                                        + universalAdIds
                        );
                        adsManager.discardAdBreak();
                    }
            );

            playerView.getAdViewGroup().setOnClickListener(view -> {
                if (skipped) {
                    closeAdsManager();
                }
            });
            playerView.setClickable(true);
            playerView.setOnClickListener(view -> {
                Log.e("aaaaaa", "onClick");
                if (skipped) {
                    closeAdsManager();
                }
            });

            adEventListener = adEvent -> {

//                Log.e("AAAATVTestLibrary", "Event: " + adEvent.getType());

                if (adsManager != null) {
                    /** Responds to AdEvents.  */
                    if (adEvent.getType() != AdEvent.AdEventType.AD_PROGRESS) {
                        Log.i("TVTestLibrary", "Event: " + adEvent.getType());
                        if (playerView.getPlayer().getCurrentPosition() > 0 &&
                                playerView.getPlayer().getDuration() <= 0) {
                            closeAdsManager();
                        }
                    }
                    if (adEvent.getType() != AdEvent.AdEventType.STARTED) {
                        playerView.setVisibility(View.VISIBLE);
                        Log.i("TVTestLibrary", "Event: " + adEvent.getType());
                    }

                    if (adEvent.getType() == AdEvent.AdEventType.LOADED) {
                        Log.i("TVTestLibrary", "Event: " + adEvent.getType());
                        // AdEventType.LOADED is fired when ads are ready to play.

                        // This sample app uses the sample tag
                        // single_preroll_skippable_ad_tag_url that requires calling
                        // AdsManager.start() to start ad playback.
                        // If you use a different ad tag URL that returns a VMAP or
                        // an ad rules playlist, the adsManager.init() function will
                        // trigger ad playback automatically and the IMA SDK will
                        // ignore the adsManager.start().
                        // It is safe to always call adsManager.start() in the
                        // LOADED event.
                        adsManager.start();
                        playerView.setVisibility(View.VISIBLE);
                        player.setPlayWhenReady(true);

                    } else if (adEvent.getType() == AdEvent.AdEventType.CONTENT_PAUSE_REQUESTED) {
                        Log.i("TVTestLibrary", "Event: " + adEvent.getType());
                        // AdEventType.CONTENT_PAUSE_REQUESTED is fired when you
                        // should pause your content and start playing an ad.
                        playerView.setVisibility(View.VISIBLE);
                        pauseCallback.onPauseCall();
                    } else if (adEvent.getType() == AdEvent.AdEventType.COMPLETED) {
                        Log.i("TVTestLibrary", "Event: " + adEvent.getType());
                        // AdEventType.CONTENT_PAUSE_REQUESTED is fired when you
                        // should pause your content and start playing an ad.
//                                    player.setPlayWhenReady(false);
//                                    playerView.setVisibility(View.GONE);
//                                    resumeCallback.onResumeCall();
                        closeAdsManager();
                    } else if (adEvent.getType() == AdEvent.AdEventType.CONTENT_RESUME_REQUESTED) {
                        // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad
                        // you should play your content.
//                                    player.setPlayWhenReady(false);
//                                    playerView.setVisibility(View.GONE);
//                                    resumeCallback.onResumeCall();
                        closeAdsManager();
                    } else if (adEvent.getType() == AdEvent.AdEventType.ALL_ADS_COMPLETED) {
                        closeAdsManager();
                    } else if (adEvent.getType() == AdEvent.AdEventType.TAPPED
                            || adEvent.getType() == AdEvent.AdEventType.CLICKED
                            || adEvent.getType() == AdEvent.AdEventType.PAUSED) {
//                        player.setPlayWhenReady(!adsPaused);
//                        adsPaused = !adsPaused;
                        if (skipped) {
                            closeAdsManager();
                        }
                    } else {
                    }
                }
            };

            adsManager.addAdEventListener(adEventListener);
            AdsRenderingSettings adsRenderingSettings = ImaSdkFactory.getInstance().createAdsRenderingSettings();
            adsManager.init(adsRenderingSettings);
        };
        basicAdsLoader.addAdsLoadedListener(adsLoadedListener);
    }

    public void closeAdsManager() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        if (playerView != null) {
            playerView.setVisibility(View.GONE);
        }
        if (adsManager != null) {
            // Calling adsManager.destroy() triggers the function
            adsManager.destroy();
            adsManager = null;
        }
        if (basicAdsLoader != null && adsLoadedListener != null) {
            basicAdsLoader.removeAdsLoadedListener(adsLoadedListener);
        }
        if (adsManager != null && adsLoadedListener != null) {
            adsManager.removeAdEventListener(adEventListener);
        }
        if (resumeCallback != null) {
            resumeCallback.onResumeCall();
        }
    }

}



