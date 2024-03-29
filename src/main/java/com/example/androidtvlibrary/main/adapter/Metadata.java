package com.example.androidtvlibrary.main.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.media3.common.Format;

import java.util.Arrays;
import java.util.List;

public final class Metadata implements Parcelable {

    /** A metadata entry. */
    public interface Entry extends Parcelable {

        /**
         * Returns the {@link Format} that can be used to decode the wrapped metadata in {@link
         * #getWrappedMetadataBytes()}, or null if this Entry doesn't contain wrapped metadata.
         */
        @Nullable
        default com.example.androidtvlibrary.main.adapter.Format getWrappedMetadataFormat() {
            return null;
        }

        /**
         * Returns the bytes of the wrapped metadata in this Entry, or null if it doesn't contain
         * wrapped metadata.
         */
        @Nullable
        default byte[] getWrappedMetadataBytes() {
            return null;
        }
    }

    private final Entry[] entries;

    /**
     * @param entries The metadata entries.
     */
    public Metadata(Entry... entries) {
        this.entries = entries;
    }

    /**
     * @param entries The metadata entries.
     */
    public Metadata(List<? extends Entry> entries) {
        this.entries = new Entry[entries.size()];
        entries.toArray(this.entries);
    }

    /* package */ Metadata(Parcel in) {
        entries = new Metadata.Entry[in.readInt()];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = in.readParcelable(Entry.class.getClassLoader());
        }
    }

    /**
     * Returns the number of metadata entries.
     */
    public int length() {
        return entries.length;
    }

    /**
     * Returns the entry at the specified index.
     *
     * @param index The index of the entry.
     * @return The entry at the specified index.
     */
    public Metadata.Entry get(int index) {
        return entries[index];
    }

    /**
     * Returns a copy of this metadata with the entries of the specified metadata appended. Returns
     * this instance if {@code other} is null.
     *
     * @param other The metadata that holds the entries to append. If null, this methods returns this
     *     instance.
     * @return The metadata instance with the appended entries.
     */
    public Metadata copyWithAppendedEntriesFrom(@Nullable Metadata other) {
        if (other == null) {
            return this;
        }
        return copyWithAppendedEntries(other.entries);
    }

    /**
     * Returns a copy of this metadata with the specified entries appended.
     *
     * @param entriesToAppend The entries to append.
     * @return The metadata instance with the appended entries.
     */
    public Metadata copyWithAppendedEntries(Entry... entriesToAppend) {
        if (entriesToAppend.length == 0) {
            return this;
        }
        return new Metadata(Util.nullSafeArrayConcatenation(entries, entriesToAppend));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Metadata other = (Metadata) obj;
        return Arrays.equals(entries, other.entries);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(entries);
    }

    @Override
    public String toString() {
        return "entries=" + Arrays.toString(entries);
    }

    // Parcelable implementation.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(entries.length);
        for (Entry entry : entries) {
            dest.writeParcelable(entry, 0);
        }
    }

    public static final Parcelable.Creator<Metadata> CREATOR =
            new Parcelable.Creator<Metadata>() {
                @Override
                public Metadata createFromParcel(Parcel in) {
                    return new Metadata(in);
                }

                @Override
                public Metadata[] newArray(int size) {
                    return new Metadata[size];
                }
            };
}
