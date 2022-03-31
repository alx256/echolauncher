package com.example.echolauncher;

import android.app.WallpaperManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

/**
 * This class implements the functionality of the home
 * screen pages fragment which contains the pages where
 * apps and widgets can be pinned
 */

public class HomeScreenPages extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen, null, false);

        ViewPager2 pager = view.findViewById(R.id.homeScreenPager);
        HomeScreenAdapter adapter = new HomeScreenAdapter(getActivity());
        pager.setAdapter(adapter);
        // Start on page 1
        pager.setCurrentItem(1, false);

        // Get the user's wallpaper
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        ImageView wallpaperView = view.findViewById(R.id.wallpaperView);
        // Set the background to the user's wallpaper
        wallpaperView.setImageDrawable(wallpaperManager.getDrawable());

        return view;
    }
}
