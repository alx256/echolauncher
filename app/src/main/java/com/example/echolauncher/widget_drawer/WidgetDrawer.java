package com.example.echolauncher.widget_drawer;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.example.echolauncher.R;
import com.example.echolauncher.apps.Library;
import com.example.echolauncher.drawer.DrawerAdapter;
import com.example.echolauncher.drawer.WidgetItem;

/**
 * Class for the widget drawer fragment
 */

public class WidgetDrawer extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.widget_drawer, container, false);

        initDrawer();

        // In order to update widgets (because Android forbids updating
        // UI elements on a thread that did not create them), a handler is created
        // that recursively calls itself in order to constantly update
        // the widgets
        handler = new Handler();
        updateRunnable = () -> {
            for (WidgetItem item : Library.getAllWidgets()) {
                item.getWidget().tick();
                item.update();
            }
            handler.postDelayed(updateRunnable, 1000 /* 1 second delay between updates */);
        };
        handler.post(updateRunnable);

        return view;
    }

    private void initDrawer() {
        // Initialise the gridView with a
        // new DrawerAdapter
        GridView drawerGridView = view.findViewById(R.id.drawerGrid);
        DrawerAdapter adapter = new DrawerAdapter(view.getContext(), Library.getAllWidgets());
        drawerGridView.setAdapter(adapter);
    }

    private View view;
    private Handler handler;
    private Runnable updateRunnable;
}
