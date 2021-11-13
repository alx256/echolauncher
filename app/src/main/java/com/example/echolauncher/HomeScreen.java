package com.example.echolauncher;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends Fragment {
    private List<AppItem> apps;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_screen, container, false);

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(view.getContext());
        final ImageView wallpaperView = view.findViewById(R.id.wallpaperView);
        wallpaperView.setImageDrawable(wallpaperManager.getDrawable());

        final GridView gridView = view.findViewById(R.id.homeScreenGrid);
        apps = new ArrayList<>();

        for (int i = 0; i < 4 * 12; i++)
            apps.add(new AppItem());

        gridView.setAdapter(new AppAdapter(view.getContext(), apps));

        return view;
    }
}
