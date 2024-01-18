package com.example.androidtvlibrary.main.adapter.wow;

import androidx.annotation.Nullable;

import com.example.androidtvlibrary.main.adapter.C;


/**
 * Identifier for a {@link MediaPeriod}.
 */
final class MediaPeriodId {

    /**
     * The unique id of the timeline period.
     */
    public final Object periodUid;

    /**
     * If the media period is in an ad group, the index of the ad group in the period.
     * {@link C#INDEX_UNSET} otherwise.
     */
    public final int adGroupIndex;

    /**
     * If the media period is in an ad group, the index of the ad in its ad group in the period.
     * {@link C#INDEX_UNSET} otherwise.
     */
    public final int adIndexInAdGroup;

    /**
     * The sequence number of the window in the buffered sequence of windows this media period is
     * part of. {@link C#INDEX_UNSET} if the media period id is not part of a buffered sequence of
     * windows.
     */
    public final long windowSequenceNumber;

    /**
     * The index of the next ad group to which the media period's content is clipped, or {@link
     * C#INDEX_UNSET} if there is no following ad group or if this media period is an ad.
     */
    public final int nextAdGroupIndex;

    /**
     * Creates a media period identifier for a dummy period which is not part of a buffered sequence
     * of windows.
     *
     * @param periodUid The unique id of the timeline period.
     */
    public MediaPeriodId(Object periodUid) {
        this(periodUid, /* windowSequenceNumber= */ C.INDEX_UNSET);
    }

    /**
     * Creates a media period identifier for the specified period in the timeline.
     *
     * @param periodUid            The unique id of the timeline period.
     * @param windowSequenceNumber The sequence number of the window in the buffered sequence of
     *                             windows this media period is part of.
     */
    public MediaPeriodId(Object periodUid, long windowSequenceNumber) {
        this(
                periodUid,
                /* adGroupIndex= */ C.INDEX_UNSET,
                /* adIndexInAdGroup= */ C.INDEX_UNSET,
                windowSequenceNumber,
                /* nextAdGroupIndex= */ C.INDEX_UNSET);
    }

    /**
     * Creates a media period identifier for the specified clipped period in the timeline.
     *
     * @param periodUid            The unique id of the timeline period.
     * @param windowSequenceNumber The sequence number of the window in the buffered sequence of
     *                             windows this media period is part of.
     * @param nextAdGroupIndex     The index of the next ad group to which the media period's content is
     *                             clipped.
     */
    public MediaPeriodId(Object periodUid, long windowSequenceNumber, int nextAdGroupIndex) {
        this(
                periodUid,
                /* adGroupIndex= */ C.INDEX_UNSET,
                /* adIndexInAdGroup= */ C.INDEX_UNSET,
                windowSequenceNumber,
                nextAdGroupIndex);
    }

    /**
     * Creates a media period identifier that identifies an ad within an ad group at the specified
     * timeline period.
     *
     * @param periodUid            The unique id of the timeline period that contains the ad group.
     * @param adGroupIndex         The index of the ad group.
     * @param adIndexInAdGroup     The index of the ad in the ad group.
     * @param windowSequenceNumber The sequence number of the window in the buffered sequence of
     *                             windows this media period is part of.
     */
    public MediaPeriodId(
            Object periodUid, int adGroupIndex, int adIndexInAdGroup, long windowSequenceNumber) {
        this(
                periodUid,
                adGroupIndex,
                adIndexInAdGroup,
                windowSequenceNumber,
                /* nextAdGroupIndex= */ C.INDEX_UNSET);
    }

    private MediaPeriodId(
            Object periodUid,
            int adGroupIndex,
            int adIndexInAdGroup,
            long windowSequenceNumber,
            int nextAdGroupIndex) {
        this.periodUid = periodUid;
        this.adGroupIndex = adGroupIndex;
        this.adIndexInAdGroup = adIndexInAdGroup;
        this.windowSequenceNumber = windowSequenceNumber;
        this.nextAdGroupIndex = nextAdGroupIndex;
    }

    /**
     * Returns a copy of this period identifier but with {@code newPeriodUid} as its period uid.
     */
    public MediaPeriodId copyWithPeriodUid(Object newPeriodUid) {
        return periodUid.equals(newPeriodUid)
                ? this
                : new MediaPeriodId(
                newPeriodUid, adGroupIndex, adIndexInAdGroup, windowSequenceNumber, nextAdGroupIndex);
    }

    /**
     * Returns whether this period identifier identifies an ad in an ad group in a period.
     */
    public boolean isAd() {
        return adGroupIndex != C.INDEX_UNSET;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MediaPeriodId periodId = (MediaPeriodId) obj;
        return periodUid.equals(periodId.periodUid)
                && adGroupIndex == periodId.adGroupIndex
                && adIndexInAdGroup == periodId.adIndexInAdGroup
                && windowSequenceNumber == periodId.windowSequenceNumber
                && nextAdGroupIndex == periodId.nextAdGroupIndex;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + periodUid.hashCode();
        result = 31 * result + adGroupIndex;
        result = 31 * result + adIndexInAdGroup;
        result = 31 * result + (int) windowSequenceNumber;
        result = 31 * result + nextAdGroupIndex;
        return result;
    }
}
