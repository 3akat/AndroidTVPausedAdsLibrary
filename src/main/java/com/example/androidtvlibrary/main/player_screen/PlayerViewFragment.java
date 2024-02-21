package com.example.androidtvlibrary.main.player_screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidtvlibrary.R;
import com.example.androidtvlibrary.main.adapter.PlayerView;

public class PlayerViewFragment extends Fragment {

    public static String SCIPPED_ADS = "scipped_ads";
    public static String ADS_ACTION_LISTENER = "on_listener";
    public static String ADS_SKIP_LISTENER = "skip_listener";


    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TAG = PlayerViewFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================
    private PlayerView videoView;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    public static PlayerViewFragment getInstance(@NonNull Boolean isScipped, PlayerView.OnActionListener onActionListener ) {
        PlayerViewFragment fragment = new PlayerViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ADS_ACTION_LISTENER, onActionListener);
        args.putBoolean(SCIPPED_ADS, isScipped);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerView getPlayerView(){
        return this.videoView;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wow_player_ads_view, container, false);
        this.videoView = v.findViewById(R.id.ads_player_view);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.videoView.setPlayerData();
        this.videoView.adAdsLoader(this.getArguments() != null && this.getArguments().getBoolean(SCIPPED_ADS));
        this.videoView.addOnActionListener(this.getArguments().getParcelable(ADS_ACTION_LISTENER));
        this.videoView.addOnSkipListener(this.getArguments().getParcelable(ADS_SKIP_LISTENER));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.videoView != null){
            this.videoView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.videoView.onPause();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
