package com.example.androidtvlibrary.main.adapter;

import androidx.annotation.IntDef;
import androidx.media3.common.util.Assertions;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public final class TestException extends IOException {

    /**
     * Types of ad load exceptions. One of {@link #TYPE_AD}, {@link #TYPE_AD_GROUP}, {@link
     * #TYPE_ALL_ADS} or {@link #TYPE_UNEXPECTED}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_AD, TYPE_AD_GROUP, TYPE_ALL_ADS, TYPE_UNEXPECTED})
    public @interface Type {
    }

    /**
     * Type for when an ad failed to load. The ad will be skipped.
     */
    public static final int TYPE_AD = 0;
    /**
     * Type for when an ad group failed to load. The ad group will be skipped.
     */
    public static final int TYPE_AD_GROUP = 1;
    /**
     * Type for when all ad groups failed to load. All ads will be skipped.
     */
    public static final int TYPE_ALL_ADS = 2;
    /**
     * Type for when an unexpected error occurred while loading ads. All ads will be skipped.
     */
    public static final int TYPE_UNEXPECTED = 3;

    /**
     * Returns a new ad load exception of {@link #TYPE_AD}.
     */
    public static TestException createForAd(Exception error) {
        return new TestException(TYPE_AD, error);
    }

    /**
     * Returns a new ad load exception of {@link #TYPE_AD_GROUP}.
     */
    public static TestException createForAdGroup(Exception error, int adGroupIndex) {
        return new TestException(
                TYPE_AD_GROUP, new IOException("Failed to load ad group " + adGroupIndex, error));
    }

    /**
     * Returns a new ad load exception of {@link #TYPE_ALL_ADS}.
     */
    public static TestException createForAllAds(Exception error) {
        return new TestException(TYPE_ALL_ADS, error);
    }

    /**
     * Returns a new ad load exception of {@link #TYPE_UNEXPECTED}.
     */
    public static TestException createForUnexpected(RuntimeException error) {
        return new TestException(TYPE_UNEXPECTED, error);
    }

    /**
     * The {@link Type} of the ad load exception.
     */
    public final @Type int type;

    private TestException(@Type int type, Exception cause) {
        super(cause);
        this.type = type;
    }

    /**
     * Returns the {@link RuntimeException} that caused the exception if its type is {@link
     * #TYPE_UNEXPECTED}.
     */
    public RuntimeException getRuntimeExceptionForUnexpected() {
        Assertions.checkState(type == TYPE_UNEXPECTED);
        return (RuntimeException) Assertions.checkNotNull(getCause());
    }
}
