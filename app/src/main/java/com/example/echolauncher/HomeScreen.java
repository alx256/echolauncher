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
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

public class HomeScreen extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen, null, false);

        ViewPager2 pager = view.findViewById(R.id.homeScreenPager);
        HomeScreenAdapter adapter = new HomeScreenAdapter(getActivity());
        pager.setAdapter(adapter);
        pager.setCurrentItem(1, false);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        ImageView wallpaperView = view.findViewById(R.id.wallpaperView);
        wallpaperView.setImageDrawable(wallpaperManager.getDrawable());

        return view;
    }
}
