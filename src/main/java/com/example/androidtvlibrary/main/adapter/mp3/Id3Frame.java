package com.example.androidtvlibrary.main.adapter.mp3;

import com.example.androidtvlibrary.main.adapter.Metadata;

public abstract class Id3Frame implements Metadata.Entry {

    /**
     * The frame ID.
     */
    public final String id;

    public Id3Frame(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
