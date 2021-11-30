package com.example.echolauncher;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
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
        gridView.getLayoutParams().width = Globals.metricsFull.widthPixels;
        apps = new ArrayList<>();

        AppAdapter adapter = new AppAdapter(view.getContext(), apps);
        adapter.isHomeScreen = true;
        gridView.setAdapter(adapter);

        float itemHeight = adapter.getHeight() + 20.0f;

        for (int i = 0;
             i < 200;
             i++)
            apps.add(new AppItem());

        final int RIGHT_AREA = Globals.metricsFull.widthPixels - 200;

        ImageView delete = view.findViewById(R.id.cross);
        Log.d("", Float.toString(Globals.metricsFit.heightPixels));
//        delete.setY(-(Globals.metricsFull.heightPixels - Globals.metricsFit.heightPixels));
        delete.setY(-Globals.metricsFull.heightPixels + delete.getLayoutParams().height);
        delete.setVisibility(View.INVISIBLE);

        gridView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                delete.setVisibility(View.VISIBLE);

                totalX += dragEvent.getX();
                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                    if (dragEvent.getX() >= RIGHT_AREA)
                        Log.d("", Integer.toString(RIGHT_AREA));
                }

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED)
                    delete.setVisibility(View.INVISIBLE);

                return true;
            }
        });

        return view;
    }

    private float totalX = 0.0f;
}
