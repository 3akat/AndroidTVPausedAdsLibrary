package com.example.androidtvlibrary.main.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.media3.common.C;

import java.util.Arrays;

public final class TrackGroup implements Parcelable {

    /**
     * The number of tracks in the group.
     */
    public final int length;

    private final Format[] formats;

    // Lazily initialized hashcode.
    private int hashCode;

    /**
     * @param formats The track formats. Must not be null, contain null elements or be of length 0.
     */
    public TrackGroup(Format... formats) {
        checkState(formats.length > 0);
        this.formats = formats;
        this.length = formats.length;
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    /* package */ TrackGroup(Parcel in) {
        length = in.readInt();
        formats = new Format[length];
        for (int i = 0; i < length; i++) {
            formats[i] = in.readParcelable(Format.class.getClassLoader());
        }
    }

    /**
     * Returns the format of the track at a given index.
     *
     * @param index The index of the track.
     * @return The track's format.
     */
    public Format getFormat(int index) {
        return formats[index];
    }

    /**
     * Returns the index of the track with the given format in the group. The format is located by
     * identity so, for example, {@code group.indexOf(group.getFormat(index)) == index} even if
     * multiple tracks have formats that contain the same values.
     *
     * @param format The format.
     * @return The index of the track, or {@link C#INDEX_UNSET} if no such track exists.
     */
    @SuppressWarnings("ReferenceEquality")
    public int indexOf(Format format) {
        for (int i = 0; i < formats.length; i++) {
            if (format == formats[i]) {
                return i;
            }
        }
        return C.INDEX_UNSET;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = 17;
            result = 31 * result + Arrays.hashCode(formats);
            hashCode = result;
        }
        return hashCode;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TrackGroup other = (TrackGroup) obj;
        return length == other.length && Arrays.equals(formats, other.formats);
    }

    // Parcelable implementation.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(length);
        for (int i = 0; i < length; i++) {
            dest.writeParcelable(formats[i], 0);
        }
    }

    public static final Parcelable.Creator<TrackGroup> CREATOR =
            new Parcelable.Creator<TrackGroup>() {

                @Override
                public TrackGroup createFromParcel(Parcel in) {
                    return new TrackGroup(in);
                }

                @Override
                public TrackGroup[] newArray(int size) {
                    return new TrackGroup[size];
                }
            };
}
