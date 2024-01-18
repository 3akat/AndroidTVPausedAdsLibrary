package com.example.androidtvlibrary.main.adapter.player.comand;

import com.example.androidtvlibrary.main.adapter.Metadata;

public abstract class SpliceCommand implements Metadata.Entry {

    @Override
    public String toString() {
        return "SCTE-35 splice command: type=" + getClass().getSimpleName();
    }

    // Parcelable implementation.

    @Override
    public int describeContents() {
        return 0;
    }

}
