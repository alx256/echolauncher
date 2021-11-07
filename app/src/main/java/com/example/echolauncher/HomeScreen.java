package com.example.echolauncher;

import android.Manifest;
import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

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

        return view;
    }

    private void displayAllApps() {
        apps = InstalledAppsManager.getApps(view.getContext());
    }

    private void displaySearchedApps(String string) {
        apps = InstalledAppsManager.searchFor(string);
    }

    private void initDrawer() {
        View drawer = view.findViewById(R.id.appDrawerLayout);
        final GridView drawerGridView = view.findViewById(R.id.appDrawerGrid);
        drawerGridView.setAdapter(new AppAdapter(view.getContext(), apps));
    }

    private void initSearchBar() {
        TextInputEditText input = view.findViewById(R.id.appSearchBar);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    displaySearchedApps(s.toString());
                    initDrawer();
                } else {
                    displayAllApps();
                    initDrawer();
                }
            }
        });
    }
}
