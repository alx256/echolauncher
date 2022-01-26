package com.example.echolauncher;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.gridlayout.widget.GridLayout;

import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenGrid extends Fragment {
    private List<AppItem> apps;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_screen_grid, container, false);

//        final GridView gridView = view.findViewById(R.id.homeScreenGrid);
//        gridView.getLayoutParams().width = Globals.metricsFull.widthPixels;
        final GridLayout gridLayout = view.findViewById(R.id.homeScreenGrid);
        apps = new ArrayList<>();

        AppAdapter adapter = new AppAdapter(view.getContext(), apps);
//        adapter.isHomeScreen = true;
//        gridView.setAdapter(adapter);

//        float itemHeight = adapter.getHeight() + 20.0f;

        gridLayout.setColumnCount(Globals.metricsFit.widthPixels / new AppItem().getHeight());
        gridLayout.setRowCount(Globals.metricsFit.widthPixels / new AppItem().getHeight());

        for (int x = 0; x < gridLayout.getColumnCount(); x++)
            for (int y = 0; y < gridLayout.getRowCount(); y++)
                apps.add(new AppItem());

        final int RIGHT_AREA = Globals.metricsFull.widthPixels - 200;

        ImageView delete = view.findViewById(R.id.cross);
        Log.d("", Float.toString(Globals.metricsFit.heightPixels));
//        delete.setY(-(Globals.metricsFull.heightPixels - Globals.metricsFit.heightPixels));
        delete.setY(-Globals.metricsFull.heightPixels + delete.getLayoutParams().height);
        delete.setVisibility(View.INVISIBLE);

        gridLayout.addView(new AppItem().toView(getContext()));

        for (int x = 0; x < gridLayout.getColumnCount(); x++)
            for (int y = 0; y < 10; y++)
                gridLayout.addView(new AppItem().toView(getContext()));

        gridLayout.setOnDragListener(new View.OnDragListener() {
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
