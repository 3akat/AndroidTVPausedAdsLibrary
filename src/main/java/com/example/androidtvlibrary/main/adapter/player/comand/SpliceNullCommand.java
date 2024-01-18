package com.example.androidtvlibrary.main.adapter.player.comand;

import android.os.Parcel;

public final class SpliceNullCommand extends SpliceCommand {

    // Parcelable implementation.

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Do nothing.
    }

    public static final Creator<SpliceNullCommand> CREATOR =
            new Creator<SpliceNullCommand>() {

                @Override
                public SpliceNullCommand createFromParcel(Parcel in) {
                    return new SpliceNullCommand();
                }

                @Override
                public SpliceNullCommand[] newArray(int size) {
                    return new SpliceNullCommand[size];
                }

            };

}
