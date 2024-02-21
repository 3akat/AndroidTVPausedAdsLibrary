package com.example.androidtvlibrary.main.player_screen;

import static com.example.androidtvlibrary.main.player_screen.PlayerViewFragment.ADS_SKIP_LISTENER;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.androidtvlibrary.R;
import com.example.androidtvlibrary.main.adapter.PlayerView;

public class PlayerScreen extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_player_view);
        if (savedInstanceState == null && getIntent() != null) {
            attachFragmentWithActivityArgs(new PlayerViewFragment());
        }
    }

    protected void attachFragment(@IdRes int id, @NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .commit();
    }

    protected void attachFragmentWithActivityArgs(@NonNull Fragment fragment) {
        attachFragmentWithActivityArgs(android.R.id.content, fragment);
    }

    protected void attachFragmentWithActivityArgs(@IdRes int id, @NonNull Fragment fragment) {
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            args.putAll(intent.getExtras());
        }

        args.putParcelable(ADS_SKIP_LISTENER, createOnSkipListener());

        fragment.setArguments(args);
        attachFragment(id, fragment);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(android.R.id.content);
        if (fragment == null) {
            fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        } else {
            super.onBackPressed();
        }
    }

    public void onAdsSkipped(){
        this.finish();
    }

    private PlayerView.OnScipListener createOnSkipListener(){
        return new PlayerView.OnScipListener(){
            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(@NonNull Parcel parcel, int i) {

            }

            @Override
            public void onAdsSkip() {
                onAdsSkipped();
            }
        };
    }
}
